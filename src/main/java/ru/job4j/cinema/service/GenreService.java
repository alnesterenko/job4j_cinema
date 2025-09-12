package ru.job4j.cinema.service;

import ru.job4j.cinema.model.Genre;

import java.util.Collection;
import java.util.Optional;

public interface GenreService {

    Optional<Genre> findGenreById(int id);

    Collection<Genre> findAllGenres();
}
