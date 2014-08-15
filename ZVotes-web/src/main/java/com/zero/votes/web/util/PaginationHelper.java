package com.zero.votes.web.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.faces.model.DataModel;

public abstract class PaginationHelper {

    private int pageSize;
    private int page;
    private int offset;

    public PaginationHelper(int pageSize) {
        this.pageSize = pageSize;
        this.offset = 2;
    }

    public abstract int getItemsCount();

    public abstract DataModel createPageDataModel();
    
    public void setPage(int page) {
        if (page >= 0 && page <= getNumPages() && page != this.page) {
            this.page = page;
        }
    }

    public int getPageFirstItem() {
        return page * pageSize;
    }

    public int getPageLastItem() {
        int i = getPageFirstItem() + pageSize - 1;
        int count = getItemsCount() - 1;
        if (i > count) {
            i = count;
        }
        if (i < 0) {
            i = 0;
        }
        return i;
    }

    public boolean isHasNextPage() {
        return (page + 1) * pageSize + 1 <= getItemsCount();
    }

    public void nextPage() {
        if (isHasNextPage()) {
            page++;
        }
    }

    public boolean isHasPreviousPage() {
        return page > 0;
    }

    public void previousPage() {
        if (isHasPreviousPage()) {
            page--;
        }
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getNumPages() {
        return (int) Math.ceil(getItemsCount() / (double) pageSize);
    }

    public List<Integer> getPaginationEntries() {
        HashSet<Integer> entries = new HashSet<>();

        entries.add(1);

        for (int i = page - offset; i <= page + offset; i++) {
            if (i > 1 && i < getNumPages()) {
                entries.add(i);
            }
        }

        entries.add(getNumPages());

        return new ArrayList<>(entries);
    }

}
