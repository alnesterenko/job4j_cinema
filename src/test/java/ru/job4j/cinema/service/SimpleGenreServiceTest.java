package ru.job4j.cinema.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cinema.model.Genre;
import ru.job4j.cinema.repository.GenreRepository;
import ru.job4j.cinema.repository.Sql2oGenreRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SimpleGenreServiceTest {

    private static GenreService genreService;

    private static Genre firstTestGenre;

    private static Genre secondTestGenre;

    @BeforeAll
    public static void initTestRepository() {
        GenreRepository mockGenreRepository = mock(Sql2oGenreRepository.class);
        firstTestGenre = new Genre(1, "Фантастика");
        secondTestGenre = new Genre(2, "Комедия");
        when(mockGenreRepository.findById(1)).thenReturn(Optional.of(firstTestGenre));
        when(mockGenreRepository.findById(2)).thenReturn(Optional.of(secondTestGenre));
        when(mockGenreRepository.findAll()).thenReturn(List.of(firstTestGenre, secondTestGenre));

        genreService = new SimpleGenreService(mockGenreRepository);
    }

    @Test
    public void whenRequestOneGenreThenGetSameGenre() {
        var optionalFirstGenre = genreService.findGenreById(1);

        assertThat(optionalFirstGenre.isPresent()).isTrue();
        assertThat(optionalFirstGenre.get()).usingRecursiveComparison().isEqualTo(firstTestGenre);
    }

    @Test
    public void whenRequestListOfGenresThenGetCorrectList() {
        var genreList = new ArrayList<>(genreService.findAllGenres());

        assertThat(genreList.size()).isEqualTo(2);
        assertThat(genreList.get(0)).usingRecursiveComparison().isEqualTo(firstTestGenre);
        assertThat(genreList.get(1)).usingRecursiveComparison().isEqualTo(secondTestGenre);
    }

    @Test
    public void whenListOfGenresNotContainsWrongGenre() {
        var genreList = new ArrayList<>(genreService.findAllGenres());

        assertThat(genreList.contains(new Genre(2, "Ужасы"))).isFalse();
    }
}