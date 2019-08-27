package uk.co.n3fs.sasha.api.search;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import uk.co.n3fs.sasha.api.type.LocationRange;

public class TicketSearchQueryBuilder {

    private UUID reporter;
    private UUID assignee;
    private Instant reportedBefore;
    private Instant reportedAfter;
    private Instant updatedBefore;
    private Instant updatedAfter;
    private Boolean open;
    private LocationRange locationRange;

    private TicketSearchQueryBuilder() {}

    public void setReporter(UUID reporter) {
        this.reporter = reporter;
    }

    public void setAssignee(UUID assignee) {
        this.assignee = assignee;
    }

    public void setReportedBefore(Instant reportedBefore) {
        this.reportedBefore = reportedBefore;
    }

    public void setReportedAfter(Instant reportedAfter) {
        this.reportedAfter = reportedAfter;
    }

    public void setUpdatedBefore(Instant updatedBefore) {
        this.updatedBefore = updatedBefore;
    }

    public void setUpdatedAfter(Instant updatedAfter) {
        this.updatedAfter = updatedAfter;
    }

    public void setOpen(Boolean open) {
        this.open = open;
    }

    public void setLocationRange(LocationRange locationRange) {
        this.locationRange = locationRange;
    }

    public static TicketSearchQueryBuilder newBuilder() {
        return new TicketSearchQueryBuilder();
    }

    public TicketSearchQuery buildQuery() {
        StringBuilder statement = new StringBuilder("SELECT * FROM {prefix}tickets ");
        List<Object> paramsList = new ArrayList<>();
        boolean initial = true;

        if (reporter != null) {
            addClause(statement, "reporter_id = ?", initial);
            paramsList.add(reporter);
            initial = false;
        }

        if (assignee != null) {
            addClause(statement, "assignee_id = ?", initial);
            paramsList.add(assignee);
            initial = false;
        }

        if (reportedBefore != null) {
            addClause(statement, "reported_at < ?", initial);
            paramsList.add(reportedBefore);
            initial = false;
        }

        if (reportedAfter != null) {
            addClause(statement, "reported_at > ?", initial);
            paramsList.add(reportedAfter);
            initial = false;
        }

        if (updatedBefore != null) {
            addClause(statement, "updated_at < ?", initial);
            paramsList.add(updatedBefore);
            initial = false;
        }

        if (updatedAfter != null) {
            addClause(statement, "updated_at > ?", initial);
            paramsList.add(updatedAfter);
            initial = false;
        }

        if (open != null) {
            addClause(statement, "open = ?", initial);
            paramsList.add(open);
            initial = false;
        }

        if (locationRange != null) {
            addClause(statement, "world = ? AND x >= ? AND y >= ? AND z >= ? AND x <= ? AND y <= ? AND z <= ?", initial);
            paramsList.addAll(Arrays.asList(locationRange.getWorld(), locationRange.getX1(), locationRange.getY1(), locationRange.getZ1(), locationRange.getX2(), locationRange.getY2(), locationRange.getZ2()));
            initial = false;
        }

        return new TicketSearchQuery(statement.toString(), paramsList.toArray());
    }

    private static void addClause(StringBuilder sb, String clause, boolean initial) {
        sb.append(initial ? "WHERE " : "AND ").append(clause).append(" ");
    }

}
