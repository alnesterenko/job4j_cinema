package ru.job4j.cinema.repository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cinema.configuration.DatasourceConfiguration;

import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

class Sql2oFilmSessionRepositoryTest {

    private static Sql2oFilmSessionRepository sql2oFilmSessionRepository;

    @BeforeAll
    public static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oFilmSessionRepositoryTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        var sql2o = configuration.databaseClient(datasource);

        sql2oFilmSessionRepository = new Sql2oFilmSessionRepository(sql2o);
    }

    @Test
    public void whenGetAllThenSuccess() {
        var filmSessions = sql2oFilmSessionRepository.findAll();
        assertThat(filmSessions.size()).isGreaterThan(0);
        assertThat(filmSessions instanceof Collection).isTrue();
    }

    @Test
    public void whenGetAllAndFindByIdFirstAndLastThenSuccess() {
        var filmSessions = sql2oFilmSessionRepository.findAll();
        var first = sql2oFilmSessionRepository.findById(1);
        var last = sql2oFilmSessionRepository.findById(filmSessions.size());
        assertThat(first.isPresent()).isTrue();
        assertThat(last.isPresent()).isTrue();
    }

    /* !!! В базе данных нумерация стартует не с 0, а с 1 !!! */
    @Test
    public void whenGetAllAndFindAfterlastThenFailure() {
        var filmSessions = sql2oFilmSessionRepository.findAll();
        var afterlast = sql2oFilmSessionRepository.findById(filmSessions.size() + 1);
        assertThat(afterlast.isPresent()).isFalse();
    }

    @Test
    public void whenFindBeforefirstThenFailure() {
        var beforefirst = sql2oFilmSessionRepository.findById(0);
        assertThat(beforefirst.isPresent()).isFalse();
    }

    @Test
    public void whenGetListOfFilmSessionNotInOrderThenSuccess() {
        int[] idsArr = new int[] {1, 3, 5};
        var filmSessionList = sql2oFilmSessionRepository.findByManyIds(idsArr);
        assertThat(filmSessionList.size()).isEqualTo(idsArr.length);
    }

    @Test
    public void whenGetListOfFilmSessionNotInOrderWithEmptyListOfIdsThenSuccess() {
        int[] idsArr = new int[] {};
        var filmSessionList = sql2oFilmSessionRepository.findByManyIds(idsArr);
        assertThat(filmSessionList.size()).isEqualTo(idsArr.length);
    }

    @Test
    public void whenGetListOfFilmSessionNotInOrderWithIncorrectIdThenSuccess() {
        int[] idsArr = new int[] {1, 3, 0, -1};
        int[] correctIdsArr = Arrays.stream(idsArr).filter(id -> id >= 1).toArray();
        var filmSessionList = sql2oFilmSessionRepository.findByManyIds(idsArr);
        assertThat(filmSessionList.size()).isEqualTo(correctIdsArr.length);
    }
}