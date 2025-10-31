package ru.job4j.cinema.service.ticket;

import ru.job4j.cinema.dto.TicketDto;
import ru.job4j.cinema.model.Ticket;

import java.util.Optional;

public interface TicketService {

    Optional<TicketDto> saveTicket(Ticket ticket);

    Optional<TicketDto> findByTicketId(int id);

    Optional<TicketDto> findByFilmSessionAndRowNumberAndPlaceNumber(int sessionId, int rowNumber, int placeNumber);
}
