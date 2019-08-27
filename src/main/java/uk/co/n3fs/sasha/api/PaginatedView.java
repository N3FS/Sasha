package uk.co.n3fs.sasha.api;

import java.util.List;

public interface PaginatedView<T> {

    List<T> getAllElements();

    List<T> getPage(int page);

    int getPageSize();

    default int getTotalElements() {
        return getAllElements().size();
    }

    default int getTotalPages() {
        return (int) Math.ceil(getTotalElements() / getPageSize());
    };

}
