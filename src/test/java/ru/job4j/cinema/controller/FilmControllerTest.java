package ru.job4j.cinema.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.cinema.dto.FilmDto;
import ru.job4j.cinema.service.FilmService;

import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FilmControllerTest {

    private FilmController filmController;

    private FilmService filmService;

    @BeforeEach
    public void initServices() {
        filmService = mock(FilmService.class);
        filmController = new FilmController(filmService);
    }

    public void whenRequestAllMoviesPageThenGetPageWithAllMovies() {
        var testFilmDto1 = new FilmDto(
                1,
                "Один маленький человек",
                "Описание тестового фильма 1",
                1984,
                16,
                120,
                "Тестовый жанр",
                1);
        var testFilmDto2 = new FilmDto(
                2,
                "Два маленьких человека",
                "Описание тестового фильма 2",
                1982,
                14,
                136,
                "Тестовый жанр 2",
                2);
        var expectedMovies = List.of(testFilmDto1, testFilmDto2);
        when(filmService.findAllFilms()).thenReturn(expectedMovies);

        var model = new ConcurrentModel();
        var view = filmController.getAll(model);
        var actualMovies = model.getAttribute("films");

        assertThat(view).isEqualTo("movies/list");
        assertThat(actualMovies).isEqualTo(expectedMovies);
    }

    @Test
    public void whenRequestAllMoviesPageThenGetEmptyPage() {
        var expectedMovies = new LinkedList<FilmDto>();
        when(filmService.findAllFilms()).thenReturn(expectedMovies);

        var model = new ConcurrentModel();
        var view = filmController.getAll(model);
        var actualMovies = model.getAttribute("films");

        assertThat(view).isEqualTo("movies/list");
        assertThat(actualMovies).isEqualTo(expectedMovies);
    }
}