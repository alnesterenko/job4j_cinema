package ru.job4j.cinema.repository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cinema.configuration.DatasourceConfiguration;
import ru.job4j.cinema.repository.file.Sql2oFileRepository;

import java.util.Collection;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

class Sql2oFileRepositoryTest {

    private static Sql2oFileRepository sql2oFileRepository;

    @BeforeAll
    public static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oFileRepositoryTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        var sql2o = configuration.databaseClient(datasource);

        sql2oFileRepository = new Sql2oFileRepository(sql2o);
    }

    @Test
    public void whenGetAllThenSuccess() {
        var files = sql2oFileRepository.findAll();
        assertThat(files.size()).isGreaterThan(0);
        assertThat(files instanceof Collection).isTrue();
    }

    @Test
    public void whenGetAllAndFindByIdFirstAndLastThenSuccess() {
        var files = sql2oFileRepository.findAll();
        var first = sql2oFileRepository.findById(1);
        var last = sql2oFileRepository.findById(files.size());
        assertThat(first.isPresent()).isTrue();
        assertThat(last.isPresent()).isTrue();
    }

    /* !!! В базе данных нумерация стартует не с 0, а с 1 !!! */
    @Test
    public void whenGetAllAndFindAfterlastThenFailure() {
        var files = sql2oFileRepository.findAll();
        var afterlast = sql2oFileRepository.findById(files.size() + 1);
        assertThat(afterlast.isPresent()).isFalse();
    }

    @Test
    public void whenGetAllAndFindBeforefirstThenFailure() {
        var beforefirst = sql2oFileRepository.findById(0);
        assertThat(beforefirst.isPresent()).isFalse();
    }
}