package ru.job4j.cinema.service;

import org.springframework.stereotype.Service;
import ru.job4j.cinema.dto.FilmSessionDto;
import ru.job4j.cinema.dto.TicketDto;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.repository.TicketRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class SimpleTicketService implements TicketService {

    private final UserService userService;

    private final FilmSessionService filmSessionService;

    private final TicketRepository ticketRepository;

    public SimpleTicketService(
            UserService simpleUserService,
            FilmSessionService simpleFilmSessionService,
            TicketRepository sql2oTicketRepository) {
        this.userService = simpleUserService;
        this.filmSessionService = simpleFilmSessionService;
        this.ticketRepository = sql2oTicketRepository;
    }

    @Override
    public Optional<TicketDto> saveTicket(Ticket ticket) {
        Optional<Ticket> optionalTicket = ticketRepository.save(ticket);
        return createOptionalTicketDto(optionalTicket);
    }

    @Override
    public Optional<TicketDto> findByTicketId(int id) {
        Optional<Ticket> optionalTicket = ticketRepository.findById(id);
        return createOptionalTicketDto(optionalTicket);
    }

    @Override
    public Optional<TicketDto> findByFilmSessionAndRowNumberAndPlaceNumber(int sessionId, int rowNumber, int placeNumber) {
        Optional<Ticket> optionalTicket = ticketRepository.findByFilmSessionAndRowNumberAndPlaceNumber(sessionId, rowNumber, placeNumber);
        return createOptionalTicketDto(optionalTicket);
    }

    @Override
    public Collection<TicketDto> findByUserId(int userId) {
        List<TicketDto> resultListOfTicketsDto = new ArrayList<>();
        Collection<Ticket> ticketsList = ticketRepository.findByUserId(userId);
        Optional<User> optionalUser = userService.findUserById(userId);
        Collection<FilmSessionDto> filmSessionDtoList = filmSessionService.findAllFilmSessions();
/* TODO Доделать этот и два остальных метода.
*   Sql2oFilmSessionRepository теперь выдаёт кинопоказы по переданному списку FilmSessionId. */
        return resultListOfTicketsDto;
    }

    @Override
    public Collection<TicketDto> findByFilmSessionId(int sessionId) {
        return List.of();
    }

    @Override
    public Collection<TicketDto> findAll() {
        return List.of();
    }

    private TicketDto createTicketDto(Ticket ticket, User user, FilmSessionDto filmSessionDto) {
        return new TicketDto(
                ticket.getRowNumber(),
                ticket.getPlaceNumber(),
                user.getName(),
                filmSessionDto.getFilmName(),
                filmSessionDto.getHallName(),
                filmSessionDto.getStartTime(),
                filmSessionDto.getEndTime(),
                filmSessionDto.getPrice());
    }

    private Optional<TicketDto> createOptionalTicketDto(Optional<Ticket> optionalTicket) {
        Optional<TicketDto> resultOptionalTicketDto = Optional.empty();
        if (optionalTicket.isPresent()) {
            Ticket ticket = optionalTicket.get();
            Optional<FilmSessionDto> optionalFilmSessionDto = filmSessionService.findFilmSessionById(ticket.getSessionId());
            Optional<User> optionalUser = userService.findUserById(ticket.getUserId());
            if (optionalFilmSessionDto.isPresent() && optionalUser.isPresent()) {
                FilmSessionDto filmSessionDto = optionalFilmSessionDto.get();
                User user = optionalUser.get();
                resultOptionalTicketDto = Optional.of(createTicketDto(ticket, user, filmSessionDto));
            }
        }
        return resultOptionalTicketDto;
    }
}
