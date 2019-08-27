package uk.co.n3fs.sasha.api.type;

import co.aikar.idb.DbRow;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class TicketComment {

    private final Integer id;
    private final int ticket;
    private final UUID author;
    private final Integer conversationId;
    private final Instant writtenAt;
    private final String message;
    private final Boolean newState;

    private TicketComment(int id, int ticket, UUID author, int conversationId, Instant writtenAt, String message, Boolean newState) {
        this.id = id;
        this.ticket = Objects.requireNonNull(ticket, "Missing ticket ID");
        this.author = Objects.requireNonNull(author, "Missing author UUID");
        this.conversationId = conversationId;
        this.writtenAt = Objects.requireNonNull(writtenAt, "Missing written timestamp");
        this.message = Objects.requireNonNull(message, "Missing message");
        this.newState = newState;
    }

    public Integer getId() {
        return id;
    }

    public int getTicket() {
        return ticket;
    }

    public UUID getAuthor() {
        return author;
    }

    public int getConversationId() {
        return conversationId;
    }

    public Instant getWrittenAt() {
        return writtenAt;
    }

    public String getMessage() {
        return message;
    }

    public Boolean getNewState() {
        return newState;
    }

    public static class Builder {
        private Integer id = null;
        private int ticket;
        private UUID author;
        private int conversationId;
        private Instant writtenAt;
        private String message;
        private Boolean newState = null;

        private Builder() {}

        public Builder setId(int id) {
            this.id = id;
            return this;
        }

        public Builder setTicket(Ticket ticket) {
            this.ticket = Objects.requireNonNull(ticket.getId());
            return this;
        }

        public Builder setTicket(int ticket) {
            this.ticket = ticket;
            return this;
        }

        public Builder setAuthor(UUID author) {
            this.author = author;
            return this;
        }

        public Builder setConversationId(int conversationId) {
            this.conversationId = conversationId;
            return this;
        }

        public Builder setWrittenAt(Instant writtenAt) {
            this.writtenAt = writtenAt;
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setNewState(Boolean newState) {
            this.newState = newState;
            return this;
        }

        public TicketComment build() {
            return new TicketComment(id, ticket, author, conversationId, writtenAt, message, newState);
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public static Builder from(TicketComment comment) {
            return newBuilder()
                .setId(comment.id)
                .setTicket(comment.ticket)
                .setAuthor(comment.author)
                .setConversationId(comment.conversationId)
                .setWrittenAt(comment.writtenAt)
                .setMessage(comment.message)
                .setNewState(comment.newState);
        }

        public static Builder from(DbRow row) {
            if (row == null || row.isEmpty()) return null;

            final Builder builder = newBuilder()
                .setId(row.get("id"))
                .setTicket(row.get("ticket_id"))
                .setConversationId(row.get("conversation_id"))
                .setWrittenAt(row.get("written_at"))
                .setMessage(row.get("message"))
                .setNewState(row.get("new_open_state"));

            try {
                builder.setAuthor(UUID.fromString(row.get("author_id")));
            } catch (IllegalArgumentException ignored) {}

            return builder;
        }
    }
}
