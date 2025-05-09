package com.shopme.admin.category;

import lombok.Getter;
import lombok.Setter;

/**
 *  When paginating categories, we return a List not a Page . So we create this class
 *  to hold totalPages and totalElements values.
 *
 */
@Getter
@Setter
public class CategoryPageInfo {
    private int totalPages ;
    private long totalElements ;
}
