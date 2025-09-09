package ru.job4j.cinema.repository;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Repository;
import org.sql2o.Sql2o;
import ru.job4j.cinema.model.Film;

import java.util.Collection;
import java.util.Optional;

@ThreadSafe
@Repository
public class Sql2oFilmRepository implements FilmRepository {

    private final Sql2o sql2o;

    public Sql2oFilmRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public Optional<Film> findById(int id) {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("SELECT * FROM films WHERE id = :id");
            var film = query.addParameter("id", id)
                    .setColumnMappings(Film.COLUMN_MAPPING)
                    .executeAndFetchFirst(Film.class);
            return Optional.ofNullable(film);
        }
    }

    @Override
    public Collection<Film> findAll() {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("SELECT * FROM films");
            return query.setColumnMappings(Film.COLUMN_MAPPING)
                    .executeAndFetch(Film.class);
        }
    }
}
