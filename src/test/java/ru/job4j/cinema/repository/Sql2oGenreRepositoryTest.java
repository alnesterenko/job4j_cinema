package ru.job4j.cinema.repository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cinema.configuration.DatasourceConfiguration;

import java.util.Collection;
import java.util.Properties;

import static org.assertj.core.api.Assertions.*;

class Sql2oGenreRepositoryTest {

    private static Sql2oGenreRepository sql2oGenreRepository;

    @BeforeAll
    public static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oGenreRepositoryTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        var sql2o = configuration.databaseClient(datasource);

        sql2oGenreRepository = new Sql2oGenreRepository(sql2o);
    }

    @Test
    public void whenGetAllThenSuccess() {
        var genres = sql2oGenreRepository.findAll();
        assertThat(genres.size()).isGreaterThan(0);
        assertThat(genres instanceof Collection).isTrue();
    }

    @Test
    public void whenGetAllAndFindByIdFirstAndLastThenSuccess() {
        var genres = sql2oGenreRepository.findAll();
        var first = sql2oGenreRepository.findById(1);
        var last = sql2oGenreRepository.findById(genres.size());
        assertThat(first.isPresent()).isTrue();
        assertThat(last.isPresent()).isTrue();
    }

    /* !!! В базе данных нумерация стартует не с 0, а с 1 !!! */
    @Test
    public void whenGetAllAndFindAfterlastThenFailure() {
        var genres = sql2oGenreRepository.findAll();
        var afterlast = sql2oGenreRepository.findById(genres.size() + 1);
        assertThat(afterlast.isPresent()).isFalse();
    }

    @Test
    public void whenGetAllAndFindBeforefirstThenFailure() {
        var beforefirst = sql2oGenreRepository.findById(0);
        assertThat(beforefirst.isPresent()).isFalse();
    }
}