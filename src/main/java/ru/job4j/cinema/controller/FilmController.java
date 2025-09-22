package ru.job4j.cinema.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.job4j.cinema.service.FilmService;

@Controller
@RequestMapping({"films", "movies"})
public class FilmController {

    private final FilmService filmService;

    public FilmController(FilmService simpleFilmService) {
        this.filmService = simpleFilmService;
    }

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("films", filmService.findAllFilms());
        return "movies/list";
    }
}
