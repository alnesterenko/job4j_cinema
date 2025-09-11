package ru.job4j.cinema.repository;

import ru.job4j.cinema.model.Ticket;

import java.util.Collection;
import java.util.Optional;

public interface TicketRepository {

    Optional<Ticket> save(Ticket ticket);

    Optional<Ticket> findById(int id);

    Optional<Ticket> findByFilmSessionAndRowNumberAndPlaceNumber(int sessionId, int rowNumber, int placeNumber);

    Collection<Ticket> findByUserId(int userId);

    Collection<Ticket> findByFilmSession(int sessionId);

    Collection<Ticket> findAll();

    boolean deleteById(int id);
}
