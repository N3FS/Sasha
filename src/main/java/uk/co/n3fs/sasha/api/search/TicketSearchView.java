package uk.co.n3fs.sasha.api.search;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import uk.co.n3fs.sasha.api.PaginatedView;
import uk.co.n3fs.sasha.api.type.Ticket;

public class TicketSearchView implements PaginatedView<Ticket> {

    private final List<Ticket> elements;
    private final int pageSize;

    public TicketSearchView(Collection<Ticket> elements, int pageSize) {
        this.elements = Lists.newLinkedList(elements);
        this.pageSize = pageSize;
    }

    @Override
    public List<Ticket> getAllElements() {
        return Collections.unmodifiableList(elements);
    }

    @Override
    public List<Ticket> getPage(int page) {
        if (page < 1 || page > getTotalPages()) return Collections.emptyList();

        final int startIndex = ((page - 1) * pageSize) + 1;
        final int endIndex = page * pageSize;
        final List<Ticket> pageList = new ArrayList<>();

        for (int i = startIndex; i <= endIndex; i++) {
            pageList.add(elements.get(i));
        }

        return Collections.unmodifiableList(pageList);
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }
}
