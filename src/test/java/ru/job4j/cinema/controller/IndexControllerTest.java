package ru.job4j.cinema.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class IndexControllerTest {

    private IndexController indexController;

    @BeforeEach
    public void initServices() {
        /* Вдруг добавятся ещё тесты ))) */
        indexController = new IndexController();
    }

    @Test
    public void whenRequestIndexPageThenGetIndexPageSuccess() {

        var view = indexController.getIndex();

        assertThat(view).isEqualTo("index");
    }
}