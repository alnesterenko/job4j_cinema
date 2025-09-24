package ru.job4j.cinema.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.job4j.cinema.service.FileService;

@RestController
@RequestMapping("/files")
public class FileController {

    private final FileService fileService;

    public FileController(FileService simpleFileService) {
        this.fileService = simpleFileService;
    }

    @GetMapping("/{id}")
    /* Если файл не найден по id, то клиенту возвращается статус 404,
     а если найден, то статус 200 с телом ответа в виде содержимого файла */
    public ResponseEntity<?> getById(@PathVariable int id) {
        var contentOptional = fileService.findFileById(id);
        if (contentOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        /* Возвращает файл в виде строки */
        return ResponseEntity.ok(contentOptional.get().getContent());
    }
}
