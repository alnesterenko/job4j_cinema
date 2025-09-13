package ru.job4j.cinema.service;

import ru.job4j.cinema.model.Hall;

import java.util.Collection;
import java.util.Optional;

public interface HallService {

    Optional<Hall> findHallById(int id);

    Collection<Hall> findAllHalls();
}
