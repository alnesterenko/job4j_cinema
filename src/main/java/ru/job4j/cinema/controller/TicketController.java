package ru.job4j.cinema.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.job4j.cinema.repository.FilmSessionRepository;
import ru.job4j.cinema.service.FilmSessionService;
import ru.job4j.cinema.service.HallService;
import ru.job4j.cinema.service.TicketService;

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
            return "errors/404";
        }
        var filmSessionDto = filmSessionDtoOptional.get();
        var hall = hallService.findHallById(filmSessionDto.getHallId()).get();
        model.addAttribute("filmSessionDto", filmSessionDto);
        model.addAttribute("hall", hall);
        return "tickets/buyticket";
    }
}
