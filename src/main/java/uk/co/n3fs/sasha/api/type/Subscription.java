package uk.co.n3fs.sasha.api.type;

import co.aikar.idb.DbRow;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Subscription {

    private final Integer id;
    private final int ticket;
    private final UUID user;
    private final Instant lastSeen;

    private Subscription(Integer id, Integer ticket, UUID user, Instant lastSeen) {
        this.id = id;
        this.ticket = Objects.requireNonNull(ticket);
        this.user = Objects.requireNonNull(user);
        this.lastSeen = Objects.requireNonNull(lastSeen);
    }

    public Integer getId() {
        return id;
    }

    public int getTicket() {
        return ticket;
    }

    public UUID getUser() {
        return user;
    }

    public Instant getLastSeen() {
        return lastSeen;
    }

    public static class Builder {
        private Integer id;
        private int ticket;
        private UUID userId;
        private Instant lastSeen;

        private Builder() {}

        public Builder setId(Integer id) {
            this.id = id;
            return this;
        }

        public Builder setTicket(int ticket) {
            this.ticket = ticket;
            return this;
        }

        public Builder setUserId(UUID userId) {
            this.userId = userId;
            return this;
        }

        public Builder setLastSeen(Instant lastSeen) {
            this.lastSeen = lastSeen;
            return this;
        }

        public Subscription build() {
            return new Subscription(id, ticket, userId, lastSeen);
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public static Builder from(Subscription sub) {
            return newBuilder()
                .setId(sub.id)
                .setTicket(sub.ticket)
                .setUserId(sub.user)
                .setLastSeen(sub.lastSeen);
        }

        public static Builder from(DbRow row) {
            if (row == null || row.isEmpty()) return null;

            final Builder builder = newBuilder()
                .setId(row.get("id"))
                .setTicket(row.get("ticket_id"))
                .setLastSeen(row.get("last_seen"));

            try {
                builder.setUserId(UUID.fromString(row.get("user_id")));
            } catch (IllegalArgumentException ignored) {}

            return builder;
        }

    }
}
