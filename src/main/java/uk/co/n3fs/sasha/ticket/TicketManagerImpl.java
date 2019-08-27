package uk.co.n3fs.sasha.ticket;

import java.sql.SQLException;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import uk.co.n3fs.sasha.api.search.TicketSearchQuery;
import uk.co.n3fs.sasha.api.search.TicketSearchView;
import uk.co.n3fs.sasha.api.type.Location;
import uk.co.n3fs.sasha.api.type.Subscription;
import uk.co.n3fs.sasha.api.type.Ticket;
import uk.co.n3fs.sasha.api.type.TicketComment;
import uk.co.n3fs.sasha.database.DatabaseManager;

import static uk.co.n3fs.sasha.database.Queries.*;

public class TicketManagerImpl implements uk.co.n3fs.sasha.api.TicketManager {

    private final DatabaseManager dbManager;

    public TicketManagerImpl(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    // TODO: assigning, subscriptions, listing

    @Override
    public Ticket createTicket(Ticket ticket) {
        if (ticket.getId() != null) throw new RuntimeException("Cannot create ticket that already exists");

        String insertTicketQuery = dbManager.replace(TICKET_CREATE);

        String reporter = ticket.getReporter().toString();
        Instant reportedAt = ticket.getReportedAt();
        Instant updatedAt = ticket.getUpdatedAt();
        boolean open = ticket.isOpen();
        String assignee = ticket.getAssignee() != null ? ticket.getAssignee().toString() : null;
        Location location = ticket.getLocation();
        String world = location != null ? location.getWorld() : null;
        Integer x = location != null ? location.getX() : null;
        Integer y = location != null ? location.getY() : null;
        Integer z = location != null ? location.getZ() : null;

        final long[] ticketId = new long[1];

        boolean success = dbManager.getDatabase().createTransaction(stm -> {
            int rows = stm.executeUpdateQuery(insertTicketQuery, reporter, reportedAt, updatedAt, open, x, y, z, world, assignee);
            if (rows != 1) return false;
            ticketId[0] = stm.getLastInsertId();
            return true;
        });

        return success ? Ticket.Builder.from(ticket).setId((int) ticketId[0]).build() : null;
    }

    @Override
    public Ticket getTicket(int id) {
        try {
            return Ticket.Builder.from(dbManager.getDatabase().getFirstRow(dbManager.replace(TICKET_GET_BY_ID), id)).build();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public TicketSearchView searchTickets(TicketSearchQuery query) {
        List<Ticket> tickets = Collections.emptyList();
        try {
            tickets = dbManager.getDatabase()
                .getResults(query.getStatement(), query.getParams())
                .stream()
                .map(row -> Ticket.Builder.from(row).build())
                .collect(Collectors.toList());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new TicketSearchView(tickets, 10);
    }

    @Override
    public TicketComment createTicketComment(TicketComment comment) {
        if (comment.getId() != null) throw new RuntimeException("Cannot create comment that already exists");

        String conversationIdsQuery = dbManager.replace(COMMENT_LIST_CONVERSATION_IDS);
        String insertCommentQuery = dbManager.replace(COMMENT_CREATE);
        String updateTicketTimestampQuery = dbManager.replace(TICKET_UPDATE_UPDATED_AT);
        String updateTicketOpenQuery = dbManager.replace(TICKET_UPDATE_OPEN_STATE);

        int ticketId = comment.getTicket();
        String authorId = comment.getAuthor().toString();
        Instant writtenAt = comment.getWrittenAt();
        String message = comment.getMessage();
        Boolean newState = comment.getNewState();

        final long[] commentId = new long[1];

        boolean success = dbManager.getDatabase().createTransaction(stm -> {
            List<Integer> conversationIds = stm.executeQueryGetFirstColumnResults(conversationIdsQuery, ticketId);
            int nextConversationId = Collections.max(conversationIds) + 1;

            int rows = stm.executeUpdateQuery(insertCommentQuery, ticketId, authorId, nextConversationId, writtenAt, message, newState);
            if (rows != 1) return false;
            commentId[0] = stm.getLastInsertId();

            if (newState != null) {
                rows = stm.executeUpdateQuery(updateTicketOpenQuery, newState, ticketId);
                if (rows != 1) return false;
            }

            rows = stm.executeUpdateQuery(updateTicketTimestampQuery, writtenAt, ticketId);
            return rows == 1;
        });

        return success ? TicketComment.Builder.from(comment).setId((int) commentId[0]).build() : null;
    }

    @Override
    public Subscription createSubscription(Subscription sub) {
        if (sub.getId() != null) throw new RuntimeException("Cannot create subscription that already exists");

        String insertSubQuery = dbManager.replace(SUBSCRIPTION_CREATE);

        int ticketId = sub.getTicket();
        String userId = sub.getUser().toString();
        Instant lastSeen = sub.getLastSeen();

        final long[] subId = new long[1];

        boolean success = dbManager.getDatabase().createTransaction(stm -> {
            int rows = stm.executeUpdateQuery(insertSubQuery, ticketId, userId, lastSeen);
            if (rows != 1) return false;
            subId[0] = stm.getLastInsertId();
            return true;
        });

        return success ? Subscription.Builder.from(sub).setId((int) subId[0]).build() : null;
    }
}
