package ru.job4j.cinema.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.cinema.dto.FilmSessionDto;
import ru.job4j.cinema.service.FilmSessionService;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FilmSessionControllerTest {

    private FilmSessionController filmSessionController;

    private FilmSessionService filmSessionService;

    @BeforeEach
    public void initServices() {
        filmSessionService = mock(FilmSessionService.class);
        filmSessionController = new FilmSessionController(filmSessionService);
    }

    @Test
    public void whenRequestAllFilmSessionPageThenGetPageWithAllFilmSession() {
        var testFilmSessionDto1 = new FilmSessionDto(
                1,
                "Один маленький человек",
                1,
                "Красный",
                LocalDateTime.of(2025, 9, 16, 18, 0, 0),
                LocalDateTime.of(2025, 9, 16, 20, 0, 0),
                100);
        var testFilmSessionDto2 = new FilmSessionDto(
                2,
                "Два маленьких человека",
                2,
                "Большой",
                LocalDateTime.of(2026, 10, 17, 18, 0, 0),
                LocalDateTime.of(2026, 10, 17, 20, 0, 0),
                120);
        var expectedFilmSessions = List.of(testFilmSessionDto1, testFilmSessionDto2);
        when(filmSessionService.findAllFilmSessions()).thenReturn(expectedFilmSessions);

        var model = new ConcurrentModel();
        var view = filmSessionController.getAll(model);
        var actualFilmSessions = model.getAttribute("filmSessions");

        assertThat(view).isEqualTo("sessions/session_list");
        assertThat(actualFilmSessions).isEqualTo(expectedFilmSessions);
    }

    @Test
    public void whenRequestAllFilmSessionPageThenGetEmptyPage() {
        var expectedFilmSessions = new LinkedList<FilmSessionDto>();
        when(filmSessionService.findAllFilmSessions()).thenReturn(expectedFilmSessions);

        var model = new ConcurrentModel();
        var view = filmSessionController.getAll(model);
        var actualFilmSessions = model.getAttribute("filmSessions");

        assertThat(view).isEqualTo("sessions/session_list");
        assertThat(actualFilmSessions).isEqualTo(expectedFilmSessions);
    }
}