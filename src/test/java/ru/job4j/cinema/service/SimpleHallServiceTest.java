package ru.job4j.cinema.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cinema.model.Hall;
import ru.job4j.cinema.repository.hall.HallRepository;
import ru.job4j.cinema.repository.hall.Sql2oHallRepository;
import ru.job4j.cinema.service.hall.HallService;
import ru.job4j.cinema.service.hall.SimpleHallService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SimpleHallServiceTest {

    private static HallService hallService;

    private static Hall firstTestHall;

    private static Hall secondTestHall;

    @BeforeAll
    public static void initTestRepository() {
        HallRepository mockHallRepository = mock(Sql2oHallRepository.class);
        firstTestHall = new Hall(1, "Большой зал", 15, 300, "Тестовое описание большого зала");
        secondTestHall = new Hall(2, "Фиолетовый зал", 10, 200, "Тестовое описание фиолетового зала");
        when(mockHallRepository.findById(1)).thenReturn(Optional.of(firstTestHall));
        when(mockHallRepository.findById(2)).thenReturn(Optional.of(secondTestHall));
        when(mockHallRepository.findAll()).thenReturn(List.of(firstTestHall, secondTestHall));

        hallService = new SimpleHallService(mockHallRepository);
    }

    @Test
    public void whenRequestOneHallThenGetSameHall() {
        var optionalFirstHall = hallService.findHallById(1);

        assertThat(optionalFirstHall.isPresent()).isTrue();
        assertThat(optionalFirstHall.get()).usingRecursiveComparison().isEqualTo(firstTestHall);
    }

    @Test
    public void whenRequestListOfHallsThenGetCorrectList() {
        var hallList = new ArrayList<>(hallService.findAllHalls());

        assertThat(hallList.size()).isEqualTo(2);
        assertThat(hallList.get(0)).usingRecursiveComparison().isEqualTo(firstTestHall);
        assertThat(hallList.get(1)).usingRecursiveComparison().isEqualTo(secondTestHall);
    }

    @Test
    public void whenListOfHallsNotContainsWrongHall() {
        var hallList = new ArrayList<>(hallService.findAllHalls());

        assertThat(hallList.contains(new Hall(2, "Мраморный зал", 12, 250, "Описание мраморного зала"))).isFalse();
    }
}