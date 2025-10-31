package ru.job4j.cinema.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cinema.dto.FilmSessionDto;
import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.model.FilmSession;
import ru.job4j.cinema.model.Hall;
import ru.job4j.cinema.repository.film.FilmRepository;
import ru.job4j.cinema.repository.filmsession.FilmSessionRepository;
import ru.job4j.cinema.repository.film.Sql2oFilmRepository;
import ru.job4j.cinema.repository.filmsession.Sql2oFilmSessionRepository;
import ru.job4j.cinema.service.filmsession.FilmSessionService;
import ru.job4j.cinema.service.filmsession.SimpleFilmSessionService;
import ru.job4j.cinema.service.hall.HallService;
import ru.job4j.cinema.service.hall.SimpleHallService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SimpleFilmSessionServiceTest {

    private static FilmSessionService filmSessionService;

    private static FilmSession firstTestFilmSession;

    private static FilmSession secondTestFilmSession;

    private static Film testFilm;

    private static Hall testHall;

    @BeforeAll
    public static void initTestRepository() {
        FilmSessionRepository mockFilmSessionRepository = mock(Sql2oFilmSessionRepository.class);
        FilmRepository mockFilmRepository = mock(Sql2oFilmRepository.class);
        HallService mockHallService = mock(SimpleHallService.class);
        /* LocalDateTime.of(2023, 10, 26, 14, 30, 15); // Год, месяц, день, час, минута, секунда */
        firstTestFilmSession = new FilmSession(
                1,
                1, 1,
                LocalDateTime.of(2025, 9, 16, 18, 0, 0),
                LocalDateTime.of(2025, 9, 16, 20, 0, 0),
                100);
        secondTestFilmSession = new FilmSession(
                2,
                1,
                1,
                LocalDateTime.of(2026, 10, 17, 18, 0, 0),
                LocalDateTime.of(2026, 10, 17, 20, 0, 0),
                120);
        testFilm = new Film(1, "Тестовый фильм1", "Это тестовый фильм1", 1982, 1, 16, 100, 1);
        testHall = new Hall(1, "Test hall", 10, 200, "Это тестовый зал");
        when(mockFilmSessionRepository.findById(1)).thenReturn(Optional.of(firstTestFilmSession));
        when(mockFilmSessionRepository.findById(2)).thenReturn(Optional.of(secondTestFilmSession));
        when(mockFilmSessionRepository.findAll()).thenReturn(List.of(firstTestFilmSession, secondTestFilmSession));
        when(mockFilmRepository.findById(any(Integer.class))).thenReturn(Optional.of(testFilm));
        when(mockFilmRepository.findAll()).thenReturn(List.of(testFilm));
        when(mockHallService.findHallById(any(Integer.class))).thenReturn(Optional.of(testHall));
        when(mockHallService.findAllHalls()).thenReturn(List.of(testHall));

        filmSessionService = new SimpleFilmSessionService(mockFilmSessionRepository, mockFilmRepository, mockHallService);
    }

    @Test
    public void whenRequestOneFilmSessionDtoThenGetSameFilmSessionDto() {
        var testFilmName = testFilm.getName();
        var testHallName = testHall.getName();

        var optionalSecondFilmSessionDto = filmSessionService.findFilmSessionById(2);

        assertThat(optionalSecondFilmSessionDto.isPresent()).isTrue();
        assertThat(optionalSecondFilmSessionDto.get().getFilmName()).isEqualTo(testFilmName);
        assertThat(optionalSecondFilmSessionDto.get().getHallName()).isEqualTo(testHallName);
        assertThat(optionalSecondFilmSessionDto.get().getEndTime()).isEqualTo(secondTestFilmSession.getEndTime());
    }

    @Test
    public void whenRequestListOfFilmSessionsThenGetCorrectList() {
        var testFilmName = testFilm.getName();
        var testHallName = testHall.getName();

        var filmSessionDtoList = new ArrayList<>(filmSessionService.findAllFilmSessions());

        assertThat(filmSessionDtoList.size()).isEqualTo(2);
        assertThat(filmSessionDtoList.get(0).getFilmName()).isEqualTo(filmSessionDtoList.get(1).getFilmName()).isEqualTo(testFilmName);
        assertThat(filmSessionDtoList.get(0).getHallName()).isEqualTo(filmSessionDtoList.get(1).getHallName()).isEqualTo(testHallName);
        assertThat(filmSessionDtoList.get(0).getStartTime()).isEqualTo(firstTestFilmSession.getStartTime());
        assertThat(filmSessionDtoList.get(1).getPrice()).isEqualTo(secondTestFilmSession.getPrice());
    }

    @Test
    public void whenListOfFilesNotContainsWrongFile() {
        var filmSessionDtoList = new ArrayList<>(filmSessionService.findAllFilmSessions());

        assertThat(filmSessionDtoList.contains(new FilmSessionDto(
                3,
                "Тестовый фильм2",
                3,
                "Маленький зал",
                LocalDateTime.of(2026, 10, 17, 18, 0, 0),
                LocalDateTime.of(2026, 10, 17, 20, 0, 0),
                150))).isFalse();
    }
}