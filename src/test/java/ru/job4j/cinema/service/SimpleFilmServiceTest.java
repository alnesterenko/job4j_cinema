package ru.job4j.cinema.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cinema.model.File;
import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.model.Genre;
import ru.job4j.cinema.repository.FilmRepository;
import ru.job4j.cinema.repository.Sql2oFilmRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SimpleFilmServiceTest {

    private static FilmService filmService;

    private FilmRepository filmRepository;

    private FileService fileService;

    private GenreService genreService;

    private static Film firstTestFilm;

    private static Film secondTestFilm;

    private static File testFile;

    private static Genre testGenre;

    @BeforeAll
    public static void initTestRepository() {
        FilmRepository mockFilmRepository = mock(Sql2oFilmRepository.class);
        FileService mockFileService = mock(SimpleFileService.class);
        GenreService mockGenreService = mock(SimpleGenreService.class);
        firstTestFilm = new Film(1, "Тестовый фильм1", "Это тестовый фильм1", 1982, 1, 16, 100, 1);
        secondTestFilm = new Film(2, "Тестовый фильм2", "Это тестовый фильм2", 1984, 1, 18, 120, 1);
        testFile = new File(1, "Test File", "files/test_file.jpg");
        testGenre = new Genre(1, "Test genre");
        when(mockFilmRepository.findById(1)).thenReturn(Optional.of(firstTestFilm));
        when(mockFilmRepository.findById(2)).thenReturn(Optional.of(secondTestFilm));
        when(mockFilmRepository.findAll()).thenReturn(List.of(firstTestFilm, secondTestFilm));
        when(mockFileService.findFileById(any(Integer.class))).thenReturn(Optional.of(testFile));
        when(mockFileService.findAllFiles()).thenReturn(List.of(testFile));
        when(mockGenreService.findGenreById(any(Integer.class))).thenReturn(Optional.of(testGenre));
        when(mockGenreService.findAllGenres()).thenReturn(List.of(testGenre));

        filmService = new SimpleFilmService(mockFilmRepository, mockFileService, mockGenreService);
    }

    @Test
    public void whenRequestOneFilmThenGetSameFilm() {
        var testGenreName = testGenre.getName();
        var testFilePath = testFile.getPath();

        var optionalFirstFilmDto = filmService.findFilmById(1);

        assertThat(optionalFirstFilmDto.isPresent()).isTrue();
        assertThat(optionalFirstFilmDto.get().getGenre()).isEqualTo(testGenreName);
        assertThat(optionalFirstFilmDto.get().getPathToFile()).isEqualTo(testFilePath);
        assertThat(optionalFirstFilmDto.get().getYear()).isEqualTo(firstTestFilm.getYear());
    }

    @Test
    public void whenRequestListOfFilmsThenGetCorrectList() {
        var testGenreName = testGenre.getName();
        var testFilePath = testFile.getPath();

        var filmDtoList = new ArrayList<>(filmService.findAllFilms());

        assertThat(filmDtoList.size()).isEqualTo(2);
        assertThat(filmDtoList.get(0).getGenre()).isEqualTo(filmDtoList.get(1).getGenre()).isEqualTo(testGenreName);
        assertThat(filmDtoList.get(0).getPathToFile()).isEqualTo(filmDtoList.get(1).getPathToFile()).isEqualTo(testFilePath);
        assertThat(filmDtoList.get(0).getDurationInMinutes()).isEqualTo(firstTestFilm.getDurationInMinutes());
        assertThat(filmDtoList.get(1).getDescription()).isEqualTo(secondTestFilm.getDescription());
    }

    @Test
    public void whenListOfFilesNotContainsWrongFile() {
        var filmDtoList = new ArrayList<>(filmService.findAllFilms());

        assertThat(filmDtoList.contains(new Film(1, "Тестовый фильм3", "Это тестовый фильм3", 1986, 1, 16, 100, 1))).isFalse();
    }
}