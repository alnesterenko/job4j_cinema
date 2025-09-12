package ru.job4j.cinema.service;

import org.springframework.stereotype.Service;
import ru.job4j.cinema.dto.FilmDto;
import ru.job4j.cinema.model.File;
import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.model.Genre;
import ru.job4j.cinema.repository.FilmRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class SimpleFilmService implements FilmService {

    private final FilmRepository filmRepository;

    private final FileService fileService;

    private final GenreService genreService;

    public SimpleFilmService(FilmRepository sql2oFilmRepository, FileService simpleFileService, GenreService simpleGenreService) {
        this.filmRepository = sql2oFilmRepository;
        this.fileService = simpleFileService;
        this.genreService = simpleGenreService;
    }

    /* Собираем три компонента: сам фильм, жанр и файл-постер. Если какого-то компонента нет, то и всего фильма нет. */
    @Override
    public Optional<FilmDto> findFilmById(int id) {
        Optional<FilmDto> resultDtoFilm = Optional.empty();
        var filmOptional = filmRepository.findById(id);
        if (filmOptional.isPresent()) {
            var film = filmOptional.get();
            var fileOptional = fileService.findFileById(film.getFileId());
            var genreOptional = genreService.findGenreById(film.getGenreId());
            if (fileOptional.isPresent() && genreOptional.isPresent()) {
                var file = fileOptional.get();
                var genre = genreOptional.get();
                resultDtoFilm = Optional.of(createFilmDto(film, genre, file));
            }
        }
        return resultDtoFilm;
    }

    /* Если администратор что-то забыл внести в одну из трёх БД, то случится локальный армагедец! */
    @Override
    public Collection<FilmDto> findAllFilms() {
        List<FilmDto> filmDtoList = new ArrayList<>();
        List<Film> filmList = new ArrayList<>(filmRepository.findAll());
        List<File> fileList = new ArrayList<>(fileService.findAllFiles());
        List<Genre> genreList = new ArrayList<>(genreService.findAllGenres());
        for (Film oneFilm : filmList) {
            /* Так как, в списках нумерация начинается с 0, а не с 1, как в БД, то приходится отнимать 1 */
            filmDtoList.add(createFilmDto(
                    oneFilm,
                    genreList.get(oneFilm.getGenreId() - 1),
                    fileList.get(oneFilm.getFileId() - 1)));
        }
        return filmDtoList;
    }

    private FilmDto createFilmDto(Film film, Genre genre, File file) {
        return new FilmDto(
                film.getId(),
                film.getName(),
                film.getDescription(),
                film.getYear(),
                film.getMinimalAge(),
                film.getDurationInMinutes(),
                genre.getName(),
                file.getPath());
    }
}
