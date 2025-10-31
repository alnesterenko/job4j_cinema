package ru.job4j.cinema.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.service.filmsession.FilmSessionService;
import ru.job4j.cinema.service.hall.HallService;
import ru.job4j.cinema.service.ticket.TicketService;

@Controller
@RequestMapping("/tickets") /* Работать с билетами будем по URI /tickets/** */
public class TicketController {

    private final TicketService ticketService;

    private final FilmSessionService filmSessionService;

    private final HallService hallService;

    public TicketController(
            TicketService simpleTicketService,
            FilmSessionService simpleFilmSessionService,
            HallService simpleHallService) {
        this.ticketService = simpleTicketService;
        this.filmSessionService = simpleFilmSessionService;
        this.hallService = simpleHallService;
    }

    @GetMapping("/{id}")
    public String getById(Model model, @PathVariable int id) {
        var filmSessionDtoOptional = filmSessionService.findFilmSessionById(id);
        if (filmSessionDtoOptional.isEmpty()) {
            model.addAttribute("message", "Кинопоказ с указанным идентификатором не найден");
            return "messages/404";
        }
        var filmSessionDto = filmSessionDtoOptional.get();
        var hallOptional = hallService.findHallById(filmSessionDto.getHallId());
        if (hallOptional.isEmpty()) {
            model.addAttribute("message", "Кинозал с указанным идентификатором не найден");
            return "messages/404";
        }
        var hall = hallOptional.get();
        model.addAttribute("filmSessionDto", filmSessionDto);
        model.addAttribute("hall", hall);
        return "tickets/buyticket";
    }

    @PostMapping("/buy")
    public String buyTicket(@ModelAttribute Ticket ticket, Model model) {
        var ticketDtoOptional = ticketService.saveTicket(ticket);
        if (ticketDtoOptional.isEmpty()) {
            model.addAttribute("message", "Что-то пошло не так. Попробуйте ещё раз.");
            return "messages/404";
        }
        model.addAttribute("ticketDto", ticketDtoOptional.get());
        return "messages/success";
    }
}
