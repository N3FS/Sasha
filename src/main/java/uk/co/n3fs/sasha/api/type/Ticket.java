package uk.co.n3fs.sasha.api.type;

import co.aikar.idb.DbRow;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Ticket {

    private final Integer id;
    private final UUID reporter;
    private final Instant reportedAt;
    private final Instant updatedAt;
    private final boolean open;
    private final Location location;
    private final UUID assignee;

    private Ticket(int id, UUID reporter, Instant reportedAt, Instant updatedAt, Boolean open, Location location, UUID assignee) {
        this.id = id;
        this.reporter = Objects.requireNonNull(reporter, "Missing reporter!");
        this.reportedAt = Objects.requireNonNull(reportedAt, "Missing report timestamp!");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Missing updated timestamp!");
        this.open = Objects.requireNonNull(open, "Missing open state!");
        this.location = location;
        this.assignee = assignee;
    }

    public Integer getId() {
        return id;
    }

    public UUID getReporter() {
        return reporter;
    }

    public Instant getReportedAt() {
        return reportedAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public boolean isOpen() {
        return open;
    }

    public Location getLocation() {
        return location;
    }

    public UUID getAssignee() {
        return assignee;
    }

    public static class Builder {
        private Integer id = null;
        private UUID reporter;
        private Instant reportedAt;
        private Instant updatedAt;
        private Boolean open = null;
        private Location location = null;
        private UUID assignee = null;

        private Builder() {}

        public Builder setId(int id) {
            this.id = id;
            return this;
        }

        public Builder setReporter(UUID reporter) {
            this.reporter = reporter;
            return this;
        }

        public Builder setReporter(String reporter) {
            this.reporter = UUID.fromString(reporter);
            return this;
        }

        public Builder setReportedAt(Instant reportedAt) {
            this.reportedAt = reportedAt;
            return this;
        }

        public Builder setUpdatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder setOpen(boolean open) {
            this.open = open;
            return this;
        }

        public Builder setLocation(Location location) {
            this.location = location;
            return this;
        }

        public Builder setAssignee(UUID assignee) {
            this.assignee = assignee;
            return this;
        }

        public Builder setAssignee(String assignee) {
            this.assignee = UUID.fromString(assignee);
            return this;
        }

        public Ticket build() {
            return new Ticket(id, reporter, reportedAt, updatedAt, open, location, assignee);
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public static Builder from(Ticket ticket) {
            return newBuilder()
                .setId(ticket.id)
                .setReporter(ticket.reporter)
                .setReportedAt(ticket.reportedAt)
                .setUpdatedAt(ticket.updatedAt)
                .setOpen(ticket.open)
                .setLocation(ticket.location)
                .setAssignee(ticket.assignee);
        }

        public static Builder from(DbRow row) {
            if (row == null || row.isEmpty()) return null;

            final Builder builder = newBuilder()
                .setId(row.getInt("id"))
                .setReportedAt(row.get("reported_at"))
                .setUpdatedAt(row.get("updated_at"))
                .setOpen(row.get("open"));

            try {
                builder.setReporter(UUID.fromString(row.getString("reporter_id")));
            } catch (IllegalArgumentException ignored) {}

            try {
                builder.setLocation(new Location(
                    row.getString("location_world"),
                    row.getInt("location_x"),
                    row.getInt("location_y"),
                    row.getInt("location_z")
                ));
            } catch (NullPointerException ignored) {}

            try {
                builder.setAssignee(UUID.fromString(row.getString("assignee_id")));
            } catch (IllegalArgumentException ignored) {}

            return builder;
        }
    }
}
