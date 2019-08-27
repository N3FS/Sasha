package uk.co.n3fs.sasha.api;

import uk.co.n3fs.sasha.api.search.TicketSearchQuery;
import uk.co.n3fs.sasha.api.search.TicketSearchView;
import uk.co.n3fs.sasha.api.type.Subscription;
import uk.co.n3fs.sasha.api.type.Ticket;
import uk.co.n3fs.sasha.api.type.TicketComment;

public interface TicketManager {

    Ticket createTicket(Ticket ticket);

    Ticket getTicket(int id);

    TicketSearchView searchTickets(TicketSearchQuery query);

    TicketComment createTicketComment(TicketComment comment);

    Subscription createSubscription(Subscription sub);

}
