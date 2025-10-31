package ru.job4j.cinema.service.filmsession;

import ru.job4j.cinema.dto.FilmSessionDto;

import java.util.Collection;
import java.util.Optional;

public interface FilmSessionService {

    Optional<FilmSessionDto> findFilmSessionById(int id);

    Collection<FilmSessionDto> findAllFilmSessions();
}
