package uk.co.n3fs.sasha.api.search;

public class TicketSearchQuery {

    private final String statement;
    private final Object[] params;

    TicketSearchQuery(String statement, Object[] params) {
        this.statement = statement;
        this.params = params;
    }

    public String getStatement() {
        return statement;
    }

    public Object[] getParams() {
        return params;
    }

}
