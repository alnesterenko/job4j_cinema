package ru.job4j.cinema.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.cinema.dto.FileDto;
import ru.job4j.cinema.service.FileService;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FileControllerTest {

    private static FileController fileController;

    private static FileService fileService;

    private static MultipartFile testFile;

    @BeforeAll
    public static void initServices() throws IOException {
        fileService = mock(FileService.class);
        fileController = new FileController(fileService);
        testFile = new MockMultipartFile("testFile.img", new byte[] {1, 2, 3});
        var testFileDto = new FileDto(testFile.getOriginalFilename(), testFile.getBytes());

        /* Сначала обобщённый ответ, а потом уже конкретный */
        when(fileService.findFileById(anyInt())).thenReturn(Optional.empty());
        when(fileService.findFileById(1)).thenReturn(Optional.of(testFileDto));
    }

    /* Тестируем getById() */
    @Test
    public void whenRequestFileGetByIdThenGetHttpStatusCode200AndSameContent() throws IOException {
        var responseEntity = fileController.getById(1);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(responseEntity.getBody()).isEqualTo(testFile.getBytes());
    }

    @Test
    public void whenRequestFileGetByIdThenGetResponseEntityNotFound() {
        var responseEntity = fileController.getById(2);

        assertThat(responseEntity).isEqualTo(ResponseEntity.notFound().build());
    }
}