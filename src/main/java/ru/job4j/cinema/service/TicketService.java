package ru.job4j.cinema.service;

import ru.job4j.cinema.dto.TicketDto;
import ru.job4j.cinema.model.Ticket;

import java.util.Collection;
import java.util.Optional;

public interface TicketService {

    Optional<TicketDto> saveTicket(Ticket ticket);

    Optional<TicketDto> findByTicketId(int id);

    Optional<TicketDto> findByFilmSessionAndRowNumberAndPlaceNumber(int sessionId, int rowNumber, int placeNumber);

    Collection<TicketDto> findByUserId(int id);

    Collection<TicketDto> findByFilmSessionId(int sessionId);

    Collection<TicketDto> findAll();
}
