package ru.job4j.cinema.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cinema.dto.FilmSessionDto;
import ru.job4j.cinema.dto.TicketDto;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.repository.Sql2oTicketRepository;
import ru.job4j.cinema.repository.TicketRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SimpleTicketServiceTest {

    private static TicketService ticketService;

    private static Ticket firstTestTicket;

    private static Ticket secondTestTicket;

    private static TicketDto firstTestTicketDto;

    private static TicketDto secondTestTicketDto;

    private static User testUser;

    private static FilmSessionDto testFilmSessionDto;

    private static TicketRepository mockTicketRepository;

    @SuppressWarnings("checkstyle:methodlength")
    @BeforeAll
    public static void initTestRepository() {
        mockTicketRepository = mock(Sql2oTicketRepository.class);
        UserService mockUserService = mock(SimpleUserService.class);
        FilmSessionService mockFilmSessionService = mock(SimpleFilmSessionService.class);
        /* LocalDateTime.of(2023, 10, 26, 14, 30, 15); // Год, месяц, день, час, минута, секунда */
        firstTestTicket = new Ticket(1, 1, 8, 5, 1);
        secondTestTicket = new Ticket(2, 1, 2, 8, 1);
        firstTestTicketDto = new TicketDto(
                1,
                8,
                5,
                "Случайный посетитель",
                "Жил-был пёс",
                "Красный зал",
                LocalDateTime.of(2026, 10, 17, 18, 0, 0),
                LocalDateTime.of(2026, 10, 17, 20, 0, 0),
                120);
        secondTestTicketDto = new TicketDto(
                2,
                2,
                8,
                "Случайный посетитель",
                "Жил-был пёс",
                "Красный зал",
                LocalDateTime.of(2026, 10, 17, 18, 0, 0),
                LocalDateTime.of(2026, 10, 17, 20, 0, 0),
                120);
        testUser = new User(1, "Случайный посетитель", "alz@mail.ru", "1234");
        testFilmSessionDto = new FilmSessionDto(
                1,
                "Жил-был пёс",
                1,
                "Красный зал",
                LocalDateTime.of(2026, 10, 17, 18, 0, 0),
                LocalDateTime.of(2026, 10, 17, 20, 0, 0),
                120);
        when(mockTicketRepository.findById(1)).thenReturn(Optional.of(firstTestTicket));
        when(mockTicketRepository.findById(2)).thenReturn(Optional.of(secondTestTicket));
        /* Порядок важе сперва общий, а потом более конкретный случай. Полная противоположность совету ИИ. */
        when(mockTicketRepository.findByFilmSessionAndRowNumberAndPlaceNumber(
                anyInt(),
                anyInt(),
                anyInt()))
                .thenReturn(Optional.empty());
        when(mockTicketRepository.findByFilmSessionAndRowNumberAndPlaceNumber(
                /* Оказалось, если есть матчеры, то ПРОСТО передать число нельзя! Только через специальный матчер. */
                eq(1),
                anyInt(),
                anyInt()))
                .thenReturn(Optional.ofNullable(secondTestTicket));
        when(mockUserService.findUserById(any(Integer.class))).thenReturn(Optional.of(testUser));
        when(mockFilmSessionService.findFilmSessionById(any(Integer.class))).thenReturn(Optional.of(testFilmSessionDto));

        ticketService = new SimpleTicketService(mockUserService, mockFilmSessionService, mockTicketRepository);
    }

    @Test
    public void whenSaveManyTimesOneTicket() {
        when(mockTicketRepository.save(firstTestTicket)).thenReturn(Optional.ofNullable(firstTestTicket)).thenReturn(Optional.empty());

        var resultOfFirstTrySaveTicket = ticketService.saveTicket(firstTestTicket);
        var resultOfSecondTrySaveTicket = ticketService.saveTicket(firstTestTicket);
        var resultOfThirdTrySaveTicket = ticketService.saveTicket(firstTestTicket);

        assertThat(resultOfFirstTrySaveTicket.isPresent()).isTrue();
        assertThat(resultOfFirstTrySaveTicket.orElse(null)).usingRecursiveComparison().isEqualTo(firstTestTicketDto);
        assertThat(resultOfSecondTrySaveTicket).isEqualTo(resultOfThirdTrySaveTicket).isEmpty();
    }

    @Test
    public void whenRequestOneTicketDtoThenGetSameTicketDto() {

        var optionalSecondTestTicketDto = ticketService.findByTicketId(2);

        assertThat(optionalSecondTestTicketDto.isPresent()).isTrue();
        assertThat(optionalSecondTestTicketDto.orElse(null)).usingRecursiveComparison().isEqualTo(secondTestTicketDto);
    }

    @Test
    public void whenRequestOneTicketDtoThenGetEmptyOptional() {

        var optionalSecondTestTicketDto = ticketService.findByTicketId(3);

        assertThat(optionalSecondTestTicketDto.isPresent()).isFalse();
        assertThat(optionalSecondTestTicketDto.orElse(null)).isNull();
    }

    @Test
    public void whenTryToFindTicketDtoByFilmSessionIdAndRowNumberAndPlaceNumberThenSuccess() {

        var optionalSecondTestTicketDto = ticketService.findByFilmSessionAndRowNumberAndPlaceNumber(1, 2, 8);

        assertThat(optionalSecondTestTicketDto.isPresent()).isTrue();
        assertThat(optionalSecondTestTicketDto.orElse(null)).usingRecursiveComparison().isEqualTo(secondTestTicketDto);
    }

    @Test
    public void whenTryToFindTicketDtoByFilmSessionIdAndRowNumberAndPlaceNumberThenFailed() {

        var optionalSecondTestTicketDto = ticketService.findByFilmSessionAndRowNumberAndPlaceNumber(2, 2, 8);

        assertThat(optionalSecondTestTicketDto.isPresent()).isFalse();
        assertThat(optionalSecondTestTicketDto.orElse(null)).isNull();
    }
}