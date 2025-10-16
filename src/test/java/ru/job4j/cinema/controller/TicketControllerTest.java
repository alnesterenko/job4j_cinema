package ru.job4j.cinema.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.cinema.dto.FilmSessionDto;
import ru.job4j.cinema.dto.TicketDto;
import ru.job4j.cinema.model.Hall;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.service.FilmSessionService;
import ru.job4j.cinema.service.HallService;
import ru.job4j.cinema.service.TicketService;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TicketControllerTest {

    private TicketController ticketController;

    private TicketService ticketService;

    private FilmSessionService filmSessionService;

    private HallService hallService;

    @BeforeEach
    public void initServices() {
        ticketService = mock(TicketService.class);
        filmSessionService = mock(FilmSessionService.class);
        hallService = mock(HallService.class);
        ticketController = new TicketController(ticketService, filmSessionService, hallService);
    }

    /* Тестируем getById() */
    @Test
    public void whenRequestFilmSessionByIdThenGetPageWithCorrectFilmSession() {
        var filmSessionDtoOptional = Optional.of(
                new FilmSessionDto(1,
                        "Один маленький человек",
                        1,
                        "Красный",
                        LocalDateTime.of(2025, 9, 16, 18, 0, 0),
                        LocalDateTime.of(2025, 9, 16, 20, 0, 0),
                        100));
        when(filmSessionService.findFilmSessionById(anyInt())).thenReturn(filmSessionDtoOptional);
        var testHall = new Hall(1, "Красный", 10, 15, "Тестовый зал");
        when(hallService.findHallById(anyInt())).thenReturn(Optional.of(testHall));

        var model = new ConcurrentModel();
        var view = ticketController.getById(model, 1);
        var filmSessionDto = model.getAttribute("filmSessionDto");
        var hall = model.getAttribute("hall");

        assertThat(view).isEqualTo("tickets/buyticket");
        assertThat(filmSessionDto).usingRecursiveComparison().isEqualTo(filmSessionDtoOptional.get());
        assertThat(hall).usingRecursiveComparison().isEqualTo(testHall);
    }

    @Test
    public void whenRequestFilmSessionByIdThenGetErrorPage() {
        when(filmSessionService.findFilmSessionById(anyInt())).thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        var view = ticketController.getById(model, 1);
        var message = model.getAttribute("message");

        assertThat(view).isEqualTo("messages/404");
        assertThat(message).isEqualTo("Кинопоказ с указанным идентификатором не найден");
    }

    /* Тестируем buyTicket */
    @Test
    public void whenBuyTicketSuccess() {
        var testTicket = new Ticket(1, 2, 3, 4, 1);
        var testTicketDto = new TicketDto(
                1,
                3,
                4,
                "testUser",
                "Тестовый фильм",
                "Тестовый зал",
                LocalDateTime.of(2026, 10, 17, 18, 0, 0),
                LocalDateTime.of(2026, 10, 17, 20, 0, 0),
                120);
        when(ticketService.saveTicket(any(Ticket.class))).thenReturn(Optional.of(testTicketDto));

        var model = new ConcurrentModel();
        var view = ticketController.buyTicket(testTicket, model);
        var ticketDto = model.getAttribute("ticketDto");

        assertThat(view).isEqualTo("messages/success");
        assertThat(ticketDto).usingRecursiveComparison().isEqualTo(testTicketDto);
    }

    @Test
    public void whenTryBuySameTicketAgain() {
        var testTicket = new Ticket(1, 2, 3, 4, 1);
        var testTicketDto = new TicketDto(
                1,
                3,
                4,
                "testUser",
                "Тестовый фильм",
                "Тестовый зал",
                LocalDateTime.of(2026, 10, 17, 18, 0, 0),
                LocalDateTime.of(2026, 10, 17, 20, 0, 0),
                120);
        when(ticketService.saveTicket(any(Ticket.class))).thenReturn(Optional.of(testTicketDto), Optional.empty());

        var model = new ConcurrentModel();
        ticketController.buyTicket(testTicket, model);
        var view = ticketController.buyTicket(testTicket, model);
        var message = model.getAttribute("message");

        assertThat(view).isEqualTo("messages/404");
        assertThat(message).isEqualTo("Что-то пошло не так. Попробуйте ещё раз.");
    }
}