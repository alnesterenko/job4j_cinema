package ru.job4j.cinema.service;

import ru.job4j.cinema.model.File;

import java.util.Collection;
import java.util.Optional;

public interface FileService {

    Optional<File> findFileById(int id);

    Collection<File> findAllFiles();
}
