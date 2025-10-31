package ru.job4j.cinema.repository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cinema.configuration.DatasourceConfiguration;
import ru.job4j.cinema.repository.film.Sql2oFilmRepository;

import java.util.Collection;
import java.util.Properties;

import static org.assertj.core.api.Assertions.*;

class Sql2oFilmRepositoryTest {

    private static Sql2oFilmRepository sql2oFilmRepository;

    @BeforeAll
    public static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oFilmRepositoryTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        var sql2o = configuration.databaseClient(datasource);

        sql2oFilmRepository = new Sql2oFilmRepository(sql2o);
    }

    @Test
    public void whenGetAllThenSuccess() {
        var films = sql2oFilmRepository.findAll();
        assertThat(films.size()).isGreaterThan(0);
        assertThat(films instanceof Collection).isTrue();
    }

    @Test
    public void whenGetAllAndFindByIdFirstAndLastThenSuccess() {
        var films = sql2oFilmRepository.findAll();
        var first = sql2oFilmRepository.findById(1);
        var last = sql2oFilmRepository.findById(films.size());
        assertThat(first.isPresent()).isTrue();
        assertThat(last.isPresent()).isTrue();
    }

    /* !!! В базе данных нумерация стартует не с 0, а с 1 !!! */
    @Test
    public void whenGetAllAndFindAfterlastThenFailure() {
        var films = sql2oFilmRepository.findAll();
        var afterlast = sql2oFilmRepository.findById(films.size() + 1);
        assertThat(afterlast.isPresent()).isFalse();
    }

    @Test
    public void whenGetAllAndFindBeforefirstThenFailure() {
        var beforefirst = sql2oFilmRepository.findById(0);
        assertThat(beforefirst.isPresent()).isFalse();
    }
}