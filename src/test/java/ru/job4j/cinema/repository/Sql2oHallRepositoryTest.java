package ru.job4j.cinema.repository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cinema.configuration.DatasourceConfiguration;
import ru.job4j.cinema.repository.hall.Sql2oHallRepository;

import java.util.Collection;
import java.util.Properties;

import static org.assertj.core.api.Assertions.*;

class Sql2oHallRepositoryTest {

    private static Sql2oHallRepository sql2oHallRepository;

    @BeforeAll
    public static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oHallRepositoryTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        var sql2o = configuration.databaseClient(datasource);

        sql2oHallRepository = new Sql2oHallRepository(sql2o);
    }

    @Test
    public void whenGetAllThenSuccess() {
        var halls = sql2oHallRepository.findAll();
        assertThat(halls.size()).isGreaterThan(0);
        assertThat(halls instanceof Collection).isTrue();
    }

    @Test
    public void whenGetAllAndFindByIdFirstAndLastThenSuccess() {
        var halls = sql2oHallRepository.findAll();
        var first = sql2oHallRepository.findById(1);
        var last = sql2oHallRepository.findById(halls.size());
        assertThat(first.isPresent()).isTrue();
        assertThat(last.isPresent()).isTrue();
    }

    /* !!! В базе данных нумерация стартует не с 0, а с 1 !!! */
    @Test
    public void whenGetAllAndFindAfterlastThenFailure() {
        var halls = sql2oHallRepository.findAll();
        var afterlast = sql2oHallRepository.findById(halls.size() + 1);
        assertThat(afterlast.isPresent()).isFalse();
    }

    @Test
    public void whenGetAllAndFindBeforefirstThenFailure() {
        var beforefirst = sql2oHallRepository.findById(0);
        assertThat(beforefirst.isPresent()).isFalse();
    }
}