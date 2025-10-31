package ru.job4j.cinema.service.file;

import ru.job4j.cinema.dto.FileDto;

import java.util.Collection;
import java.util.Optional;

public interface FileService {

    Optional<FileDto> findFileById(int id);

    Collection<FileDto> findAllFiles();
}
