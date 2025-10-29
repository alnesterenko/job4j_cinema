package ru.job4j.cinema.repository;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Repository;
import org.sql2o.Sql2o;
import ru.job4j.cinema.model.FilmSession;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@ThreadSafe
@Repository
public class Sql2oFilmSessionRepository implements FilmSessionRepository {

    private final Sql2o sql2o;

    public Sql2oFilmSessionRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public Optional<FilmSession> findById(int id) {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("SELECT * FROM film_sessions WHERE id = :id");
            var filmSession = query.addParameter("id", id)
                    .setColumnMappings(FilmSession.COLUMN_MAPPING)
                    .executeAndFetchFirst(FilmSession.class);
            return Optional.ofNullable(filmSession);
        }
    }

    @Override
    public Collection<FilmSession> findAll() {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("SELECT * FROM film_sessions");
            return query.setColumnMappings(FilmSession.COLUMN_MAPPING)
                    .executeAndFetch(FilmSession.class);
        }
    }

    @Override
    public Collection<FilmSession> findByManyIds(int[] ids) {
        if (ids.length > 0) {
            try (var connection = sql2o.open()) {
                String idList = String.join(",", Arrays.stream(ids)
                        .filter(id -> id >= 1)
                        .mapToObj(String::valueOf)
                        .toArray(String[]::new));
                var query = connection.createQuery("SELECT * FROM film_sessions WHERE id IN (" + idList + ")");
                return query.setColumnMappings(FilmSession.COLUMN_MAPPING)
                        .executeAndFetch(FilmSession.class);
            }
        }
        return List.of();
    }
}
