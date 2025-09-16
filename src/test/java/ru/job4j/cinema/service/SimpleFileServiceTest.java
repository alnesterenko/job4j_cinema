package ru.job4j.cinema.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cinema.model.File;
import ru.job4j.cinema.repository.FileRepository;
import ru.job4j.cinema.repository.Sql2oFileRepository;

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

    @BeforeAll
    public static void initTestRepository() {
        FileRepository mockFileRepository = mock(Sql2oFileRepository.class);
        firstTestFile = new File(1, "Панда кунг-фу", "files/panda.jpg");
        secondTestFile = new File(2, "Один маленький человек", "files/one_small_man.jpg");
        when(mockFileRepository.findById(1)).thenReturn(Optional.of(firstTestFile));
        when(mockFileRepository.findById(2)).thenReturn(Optional.of(secondTestFile));
        when(mockFileRepository.findAll()).thenReturn(List.of(firstTestFile, secondTestFile));

        fileService = new SimpleFileService(mockFileRepository);
    }

    @Test
    public void whenRequestOneFileThenGetSameFile() {
        var optionalFirstFile = fileService.findFileById(1);

        assertThat(optionalFirstFile.isPresent()).isTrue();
        assertThat(optionalFirstFile.get()).usingRecursiveComparison().isEqualTo(firstTestFile);
    }

    @Test
    public void whenRequestListOfFilesThenGetCorrectList() {
        var fileList = new ArrayList<>(fileService.findAllFiles());

        assertThat(fileList.size()).isEqualTo(2);
        assertThat(fileList.get(0)).usingRecursiveComparison().isEqualTo(firstTestFile);
        assertThat(fileList.get(1)).usingRecursiveComparison().isEqualTo(secondTestFile);
    }

    @Test
    public void whenListOfFilesNotContainsWrongFile() {
        var fileList = new ArrayList<>(fileService.findAllFiles());

        assertThat(fileList.contains(new File(2, "Панда кунг-фу", "files/one_small_man.jpg"))).isFalse();
    }
}