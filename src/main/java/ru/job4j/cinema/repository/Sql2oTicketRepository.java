package ru.job4j.cinema.repository;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Repository;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;
import ru.job4j.cinema.model.Ticket;

import java.util.Collection;
import java.util.Optional;
import java.util.logging.Logger;

@ThreadSafe
@Repository
public class Sql2oTicketRepository implements TicketRepository {

    private final Sql2o sql2o;

    private final Logger logger = Logger.getLogger(Sql2oTicketRepository.class.getName());

    public Sql2oTicketRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public Optional<Ticket> save(Ticket ticket) {
        Optional<Ticket> optionalTicket = Optional.empty();
        try (var connection = sql2o.open()) {
            var sql = """
                    INSERT INTO tickets(session_id, row_number, place_number, user_id)
                    VALUES (:sessionId, :rowNumber, :placeNumber, :userId)
                    """;
            var query = connection.createQuery(sql, true)
                    .addParameter("sessionId", ticket.getSessionId())
                    .addParameter("rowNumber", ticket.getRowNumber())
                    .addParameter("placeNumber", ticket.getPlaceNumber())
                    .addParameter("userId", ticket.getUserId());
            int generatedId = query.executeUpdate().getKey(Integer.class);
            ticket.setId(generatedId);
            optionalTicket = Optional.of(ticket);
        } catch (Sql2oException e) {
            /* В случае неудачи просто выводим сообщение об ошибке В КОНСОЛЬ */
            logger.info(e.getMessage());
        }
        return optionalTicket;
    }

    @Override
    public Optional<Ticket> findById(int id) {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("SELECT * FROM tickets WHERE id = :id");
            query.addParameter("id", id);
            var ticket = query.setColumnMappings(Ticket.COLUMN_MAPPING)
                    .executeAndFetchFirst(Ticket.class);
            return Optional.ofNullable(ticket);
        }
    }

    @Override
    public Optional<Ticket> findByFilmSessionAndRowNumberAndPlaceNumber(
            int sessionId,
            int rowNumber,
            int placeNumber) {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery(
                    "SELECT * FROM tickets WHERE session_id = :sessionId"
                            + " AND row_number = :rowNumber"
                            + " AND place_number = :placeNumber");
            query.addParameter("sessionId", sessionId)
                    .addParameter("rowNumber", rowNumber)
                    .addParameter("placeNumber", placeNumber);
            var ticket = query.setColumnMappings(Ticket.COLUMN_MAPPING)
                    .executeAndFetchFirst(Ticket.class);
            return Optional.ofNullable(ticket);
        }
    }

    @Override
    public Collection<Ticket> findByUserId(int userId) {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("SELECT * FROM tickets WHERE user_id = :userId");
            query.addParameter("userId", userId);
            return query.setColumnMappings(Ticket.COLUMN_MAPPING)
                    .executeAndFetch(Ticket.class);
        }
    }

    @Override
    public Collection<Ticket> findByFilmSession(int sessionId) {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("SELECT * FROM tickets WHERE session_id = :sessionId");
            query.addParameter("sessionId", sessionId);
            return query.setColumnMappings(Ticket.COLUMN_MAPPING)
                    .executeAndFetch(Ticket.class);
        }
    }

    @Override
    public Collection<Ticket> findAll() {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("SELECT * FROM tickets");
            return query.setColumnMappings(Ticket.COLUMN_MAPPING)
                    .executeAndFetch(Ticket.class);
        }
    }

    @Override
    public boolean deleteById(int id) {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("DELETE FROM tickets WHERE id = :id");
            query.addParameter("id", id);
            /* Убеждаемся, что удаление затронуло хоть какие-то строки */
            return (query.executeUpdate().getResult()) > 0;
        }
    }
}
