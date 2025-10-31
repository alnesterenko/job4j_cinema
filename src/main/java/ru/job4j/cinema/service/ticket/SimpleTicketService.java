package ru.job4j.cinema.service.ticket;

import org.springframework.stereotype.Service;
import ru.job4j.cinema.dto.FilmSessionDto;
import ru.job4j.cinema.dto.TicketDto;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.repository.ticket.TicketRepository;
import ru.job4j.cinema.service.user.UserService;
import ru.job4j.cinema.service.filmsession.FilmSessionService;

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

    private TicketDto createTicketDto(Ticket ticket, User user, FilmSessionDto filmSessionDto) {
        return new TicketDto(
                /* Если он не бывает в БД, то зачем ему номер(id)? Нужен! Потому что у всего должен быть номер! ))) */
                ticket.getId(),
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
