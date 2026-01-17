package com.shopme.admin.utility.paging_and_sorting;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.List;

/**
 * Helper for handling the paging and sorting parameters coming from the request.
 */
public record PagingAndSortingHelper(ModelAndViewContainer model, String sortDir, String sortField, String keyword,
                                     String listItemsName, String pathURL) {

    public void listByPageWithSorting(int pageNumber, int ITEMS_PER_PAGE, SearchRepository<?, ?> repository) {

        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        // page number is a 0-based index, but sent from the client as a 1-based index, so we need to subtract 1.
        Pageable pageable = PageRequest.of(pageNumber - 1, ITEMS_PER_PAGE, sort);
        Page<?> page;
        if (keyword == null || keyword.isEmpty())
            page = repository.findAll(pageable);
        else
            page = repository.findAll(keyword, pageable);

        addToModel(page, pageNumber);
    }

    public void addToModel(Page<?> page, int pageNumber) {
        List<?> listItems = page.getContent();
        int totalPages = page.getTotalPages();
        long totalItems = page.getTotalElements();

        int pageSize = page.getSize(); // items per page
        // int pageNumber = page.getNumber();

        long startCount = ((long) (pageNumber - 1) * pageSize) + 1;
        long endCount = (startCount + pageSize) - 1;
        if (endCount > totalItems) endCount = totalItems;
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);

        model.addAttribute(listItemsName, listItems);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);

        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("sortField", sortField);
        model.addAttribute("keyword", keyword);
        model.addAttribute("reverseSortDir", reverseSortDir);

        model.addAttribute("path", pathURL);
    }

    public Pageable createPageable(int pageNumber, int pageSize) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        // page number is a 0-based index, but sent from the client as a 1-based index, so we need to subtract 1.
        return PageRequest.of(pageNumber - 1, pageSize, sort);
    }
}
