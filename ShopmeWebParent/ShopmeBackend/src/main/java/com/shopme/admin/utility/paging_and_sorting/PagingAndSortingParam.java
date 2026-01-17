package com.shopme.admin.utility.paging_and_sorting;

import com.shopme.admin.utility.paging_and_sorting.resolver.PagingAndSortingArgumentResolver;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * This annotation is used to mark a method parameter to be resolved by the
 * {@link PagingAndSortingArgumentResolver}.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface PagingAndSortingParam {
    String listItemsName();
    String pathURL();
}
