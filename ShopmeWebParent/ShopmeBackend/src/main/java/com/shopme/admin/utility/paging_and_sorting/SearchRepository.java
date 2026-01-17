package com.shopme.admin.utility.paging_and_sorting;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * This generic interface extended by other repositories to provide generic search functionality,
 * used in <i>listByPageWithSorting</i> method in {@link PagingAndSortingHelper} class.
 */
@NoRepositoryBean
public interface SearchRepository<T, ID> extends CrudRepository<T, ID>, PagingAndSortingRepository<T, ID> {
    Page<T> findAll(String keyword, Pageable pageable);
}