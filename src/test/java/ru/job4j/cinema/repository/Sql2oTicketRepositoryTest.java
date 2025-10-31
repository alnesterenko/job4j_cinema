package ru.job4j.cinema.repository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cinema.configuration.DatasourceConfiguration;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.repository.ticket.Sql2oTicketRepository;

import java.util.List;
import java.util.Properties;

import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class Sql2oTicketRepositoryTest {

    private static Sql2oTicketRepository sql2oTicketRepository;

    @BeforeAll
    public static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oTicketRepositoryTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        var sql2o = configuration.databaseClient(datasource);

        sql2oTicketRepository = new Sql2oTicketRepository(sql2o);
    }

    @BeforeEach
    public void clearUsers() {
        var tickets = sql2oTicketRepository.findAll();
        for (var oneTicket : tickets) {
            sql2oTicketRepository.deleteById(oneTicket.getId());
        }
    }

    @Test
    public void whenSaveThenGetSameUsingAllSearchMethods() {
        Ticket ticket = new Ticket(0, 1, 1, 1, 1);
        var savedTicketOptional = sql2oTicketRepository.save(ticket);
        var ticketFoundById = sql2oTicketRepository.findById(savedTicketOptional.get().getId()).get();
        var ticketFoundBySessionAndRowNumberAndPlaceNumber = sql2oTicketRepository.findByFilmSessionAndRowNumberAndPlaceNumber(
                savedTicketOptional.get().getSessionId(),
                savedTicketOptional.get().getRowNumber(),
                savedTicketOptional.get().getPlaceNumber());
        var ticketFoundByUserId = sql2oTicketRepository.findByUserId(savedTicketOptional.get().getUserId()).toArray(new Ticket[0])[0];
        var ticketFoundByFilmSession = sql2oTicketRepository.findByFilmSession(savedTicketOptional.get().getSessionId()).toArray(new Ticket[0])[0];
        var firstTicketOfFoundAll = sql2oTicketRepository.findAll().toArray(new Ticket[0])[0];
        assertThat(savedTicketOptional.get()).usingRecursiveComparison().isEqualTo(ticketFoundById)
                .isEqualTo(ticketFoundBySessionAndRowNumberAndPlaceNumber.get())
                .isEqualTo(ticketFoundByUserId)
                .isEqualTo(ticketFoundByFilmSession)
                .isEqualTo(firstTicketOfFoundAll);
    }

    @Test
    public void whenSaveSeveralThenGetAll() {
        var ticket1 = sql2oTicketRepository.save(new Ticket(0, 1, 2, 3, 4));
        var ticket2 = sql2oTicketRepository.save(new Ticket(0, 5, 6, 7, 8));
        var ticket3 = sql2oTicketRepository.save(new Ticket(0, 9, 10, 11, 12));
        var result = sql2oTicketRepository.findAll();
        assertThat(result).isEqualTo(List.of(ticket1.get(), ticket2.get(), ticket3.get()));
    }

    @Test
    public void whenDontSaveThenNothingFound() {
        assertThat(sql2oTicketRepository.findAll()).isEqualTo(emptyList());
        assertThat(sql2oTicketRepository.findById(0)).isEqualTo(empty());
    }

    @Test
    public void whenDeleteThenGetEmptyOptional() {
        var savedTicketOptional = sql2oTicketRepository.save(new Ticket(0, 1, 2, 3, 4));
        var isDeleted = sql2oTicketRepository.deleteById(savedTicketOptional.get().getId());
        var ticketFoundByIdOptional = sql2oTicketRepository.findById(savedTicketOptional.get().getId());
        var ticketFoundBySessionAndRowNumberAndPlaceNumber = sql2oTicketRepository.findByFilmSessionAndRowNumberAndPlaceNumber(
                savedTicketOptional.get().getSessionId(),
                savedTicketOptional.get().getRowNumber(),
                savedTicketOptional.get().getPlaceNumber());
        var ticketFoundByUserIdList = sql2oTicketRepository.findByUserId(savedTicketOptional.get().getUserId());
        var ticketFoundByFilmSessionList = sql2oTicketRepository.findByFilmSession(savedTicketOptional.get().getSessionId());
        var firstTicketOfFoundAllList = sql2oTicketRepository.findAll();
        assertThat(isDeleted).isTrue();
        assertThat(ticketFoundByIdOptional)
                .isEqualTo(ticketFoundBySessionAndRowNumberAndPlaceNumber)
                .isEqualTo(empty());
        assertThat(ticketFoundByUserIdList.size())
                .isEqualTo(ticketFoundByFilmSessionList.size())
                .isEqualTo(firstTicketOfFoundAllList.size())
                .isEqualTo(0);
    }

    @Test
    public void whenDeleteByInvalidIdThenGetFalse() {
        assertThat(sql2oTicketRepository.deleteById(0)).isFalse();
    }

    @Test
    public void whenSaveTwice() {
        Ticket ticket = new Ticket(0, 1, 1, 1, 1);
        var firstTicket = sql2oTicketRepository.save(ticket);
        var secondTicket = sql2oTicketRepository.save(ticket);
        var result = sql2oTicketRepository.findAll();
        assertThat(result.size()).isEqualTo(1);
        assertThat(firstTicket.isPresent()).isTrue();
        assertThat(secondTicket.isPresent()).isFalse();
    }

    @Test
    public void whenSavedTwoDifferentTicketWithTheSameFilmSessionAndRowNumberAndPlaceNumber() {
        int filmSession = 1;
        int rowNumber = 1;
        int placeNumber = 1;
        var firstTicketOptional = sql2oTicketRepository.save(new Ticket(0, filmSession, rowNumber, placeNumber, 2));
        var secondTicketOptional = sql2oTicketRepository.save(new Ticket(0, filmSession, rowNumber, placeNumber, 3));
        var wasFoundByFilmSession =
                sql2oTicketRepository.findByFilmSession(filmSession);
        var wasFoundAll = sql2oTicketRepository.findAll();
        var firstUserTickets = sql2oTicketRepository.findByUserId(2);
        var secondUserTickets = sql2oTicketRepository.findByUserId(3);
        assertThat(wasFoundByFilmSession.size()).isEqualTo(1);
        assertThat(wasFoundAll.size()).isEqualTo(1);
        assertThat(firstTicketOptional.isPresent()).isTrue();
        assertThat(secondTicketOptional.isPresent()).isFalse();
        assertThat(firstUserTickets.contains(firstTicketOptional.get())).isTrue();
        assertThat(secondUserTickets.contains(new Ticket(0, filmSession, rowNumber, placeNumber, 3))).isFalse();
    }
}