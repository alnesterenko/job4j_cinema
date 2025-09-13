package ru.job4j.cinema.service;

import org.springframework.stereotype.Service;
import ru.job4j.cinema.dto.FilmDto;
import ru.job4j.cinema.dto.FilmSessionDto;
import ru.job4j.cinema.model.*;
import ru.job4j.cinema.repository.FilmRepository;
import ru.job4j.cinema.repository.FilmSessionRepository;
import ru.job4j.cinema.repository.HallRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class SimpleFilmSessionService implements FilmSessionService {

    private final FilmSessionRepository filmSessionRepository;

    private final FilmRepository filmRepository;

    private final HallService hallService;

    public SimpleFilmSessionService(
            FilmSessionRepository sql2oFilmSessionRepository,
            FilmRepository sql2oFilmRepository,
            HallService simpleHallService) {
        this.filmSessionRepository = sql2oFilmSessionRepository;
        this.filmRepository = sql2oFilmRepository;
        this.hallService = simpleHallService;
    }

    /* Собираем три компонента: кинопоказ, название фильма и название кинозала. Если какого-то компонента нет, то и всего кинопоказа нет. */
    @Override
    public Optional<FilmSessionDto> findFilmSessionById(int id) {
        Optional<FilmSessionDto> resultDtoFilmSession = Optional.empty();
        var filmSessionOptional = filmSessionRepository.findById(id);
        if (filmSessionOptional.isPresent()) {
            var filmSession = filmSessionOptional.get();
            var filmOptional = filmRepository.findById(filmSession.getFilmId());
            var hallOptional = hallService.findHallById(filmSession.getHallsId());
            if (filmOptional.isPresent() && hallOptional.isPresent()) {
                var film = filmOptional.get();
                var hall = hallOptional.get();
                resultDtoFilmSession = Optional.of(createFilmSessionDto(filmSession, film, hall));
            }
        }
        return resultDtoFilmSession;
    }

    /* Если администратор что-то забыл внести в одну из трёх БД, то случится локальный армагедец!
    * В реальном проекте я бы тут вставил ОГРОМНУЮ проверку. */
    @Override
    public Collection<FilmSessionDto> findAllFilmSessions() {
        List<FilmSessionDto> filmSessionDtoList = new ArrayList<>();
        List<FilmSession> filmSessionList = new ArrayList<>(filmSessionRepository.findAll());
        List<Film> filmList = new ArrayList<>(filmRepository.findAll());
        List<Hall> hallList = new ArrayList<>(hallService.findAllHalls());
        for (FilmSession oneFilmSession : filmSessionList) {
            /* Так как, в списках нумерация начинается с 0, а не с 1, как в БД, то приходится отнимать 1 */
            filmSessionDtoList.add(createFilmSessionDto(
                    oneFilmSession,
                    filmList.get(oneFilmSession.getFilmId() - 1),
                    hallList.get(oneFilmSession.getHallsId() - 1)));
        }
        return filmSessionDtoList;
    }

    private FilmSessionDto createFilmSessionDto(FilmSession filmSession, Film film, Hall hall) {
        return new FilmSessionDto(
                filmSession.getId(),
                film.getName(),
                hall.getName(),
                filmSession.getStartTime(),
                filmSession.getEndTime(),
                filmSession.getPrice());
    }
}
