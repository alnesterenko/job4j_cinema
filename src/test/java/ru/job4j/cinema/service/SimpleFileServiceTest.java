package ru.job4j.cinema.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cinema.dto.FileDto;
import ru.job4j.cinema.model.File;
import ru.job4j.cinema.repository.FileRepository;
import ru.job4j.cinema.repository.Sql2oFileRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SimpleFileServiceTest {

    private static FileService fileService;

    private static File firstTestFile;

    private static File secondTestFile;

    private static FileDto firstTestFileDto;

    private static FileDto secondTestFileDto;

    @BeforeAll
    public static void initTestRepository() throws IOException {
        FileRepository mockFileRepository = mock(Sql2oFileRepository.class);
/* Создаём два временных файла */
        firstTestFileDto = new FileDto("Панда кунг-фу", new byte[] {1, 2, 3});
        Path firstTempFile = Files.createTempFile("files", "panda.jpg");
        Files.write(firstTempFile, firstTestFileDto.getContent());
        firstTestFile = new File(1, "Панда кунг-фу", firstTempFile.toAbsolutePath().toString());

        secondTestFileDto = new FileDto("Один маленький человек", new byte[] {4, 5, 6});
        Path secondTempFile = Files.createTempFile("files", "one_small_man.jpg");
        Files.write(secondTempFile, secondTestFileDto.getContent());
        secondTestFile = new File(2, "Один маленький человек", secondTempFile.toAbsolutePath().toString());

        when(mockFileRepository.findById(1)).thenReturn(Optional.of(firstTestFile));
        when(mockFileRepository.findById(2)).thenReturn(Optional.of(secondTestFile));
        when(mockFileRepository.findById(3)).thenReturn(Optional.empty());
        when(mockFileRepository.findAll()).thenReturn(List.of(firstTestFile, secondTestFile));

        fileService = new SimpleFileService(mockFileRepository);
    }

    @Test
    public void whenRequestOneFileThenGetSameFile() {
        var optionalFirstFileDto = fileService.findFileById(1);

        assertThat(optionalFirstFileDto.isPresent()).isTrue();
        assertThat(optionalFirstFileDto.get()).usingRecursiveComparison().isEqualTo(firstTestFileDto);
    }

    @Test
    public void whenFileNotExistThenReturnEmptyOptional() {
        var result = fileService.findFileById(3);
        assertThat(result).isEmpty();
    }

    @Test
    public void whenRequestListOfFilesThenGetCorrectList() {
        var fileDtoList = new ArrayList<>(fileService.findAllFiles());

        assertThat(fileDtoList.size()).isEqualTo(2);
        assertThat(fileDtoList.get(0)).usingRecursiveComparison().isEqualTo(firstTestFileDto);
        assertThat(fileDtoList.get(1)).usingRecursiveComparison().isEqualTo(secondTestFileDto);
    }

    @Test
    public void whenListOfFilesNotContainsWrongFile() {
        var fileDtoList = new ArrayList<>(fileService.findAllFiles());

        assertThat(fileDtoList.contains(new FileDto("Панда кунг-фу", new byte[] {2, 4, 6}))).isFalse();
    }
}