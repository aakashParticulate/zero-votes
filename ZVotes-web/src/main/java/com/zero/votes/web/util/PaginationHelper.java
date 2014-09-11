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
        this.offset = 1;
    }

    public abstract int getItemsCount();

    public abstract DataModel createPageDataModel();
    
    public void setPage(int page) {
        if (page >= 0 && page <= getNumPages() && page != this.page) {
            this.page = page;
        }
    }
    
    public int getPage() {
        if (page > getNumPages()) {
            this.page = 0;
        }
        return page;
    }

    public int getPageFirstItem() {
        return getPage() * pageSize;
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
        return (getPage() + 1) * pageSize + 1 <= getItemsCount();
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
        // we will do floor division cause pages starts at 0 not 1
        return (int)Math.ceil((double)getItemsCount() / (double)pageSize) - 1;
    }

    public List<String> getPaginationEntries() {
        HashSet<Integer> entries = new HashSet<>();

        entries.add(0);
        for (int i = getPage() - offset; i <= getPage() + offset; i++) {
            if (i > 0 && i < getNumPages()) {
                entries.add(i);
            }
        } 
        entries.add(getNumPages());
        
        Object[] entries_in_array = entries.toArray();
        List<String> entries_as_string = new ArrayList<>();
        
        for (int i = 0; i < entries_in_array.length; i++) {
            entries_as_string.add(String.valueOf(entries_in_array[i]));
            
            if (i + 1 < entries_in_array.length) {
                if ((int)entries_in_array[i] + 1 < (int)entries_in_array[i + 1]) {
                    entries_as_string.add("DOTS");
                }
            }
        }

        return entries_as_string;
    }

}
