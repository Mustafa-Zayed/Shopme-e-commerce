package com.shopme.admin.utility.paging_and_sorting.resolver;

import com.shopme.admin.utility.paging_and_sorting.PagingAndSortingHelper;
import com.shopme.admin.utility.paging_and_sorting.PagingAndSortingParam;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * This class implements the HandlerMethodArgumentResolver interface to resolve
 * the paging and sorting parameters from the request.
 * <p>Note that only parameters that have the {@link PagingAndSortingParam} annotation will be resolved as shown below
 * in <i>supportsParameter</i> method
 */
public class PagingAndSortingArgumentResolver implements HandlerMethodArgumentResolver {

    /**
     * Checks whether the method parameter is annotated with {@link PagingAndSortingParam} to resolve it.
     * <p>
     * Alternatively, We can do without <b>@PagingAndSortingParam</b> check by detecting the parameter type
     * if it is of type {@link PagingAndSortingHelper}:
     * <pre><code>
     *     return parameter.getParameterType().equals(PagingAndSortingHelper.class);
     * </code></pre>
     *
     * @param parameter the method parameter to check
     * @return true if the parameter has the {@link PagingAndSortingParam} annotation, false otherwise.
     */

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(PagingAndSortingParam.class);
        // return parameter.getParameterAnnotation(PagingAndSortingParam.class) != null;
        // return parameter.getParameterType().equals(PagingAndSortingHelper.class);
    }

    /**
     * Resolves the method parameter to a value.
     * @param parameter the method parameter to resolve. This parameter must
     * have previously been passed to {@link #supportsParameter} which must
     * have returned {@code true}.
     * @param model the ModelAndViewContainer for the current request
     * @param request the current request
     * @param binderFactory a factory for creating {@link WebDataBinder} instances
     * @return the resolved argument value
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer model,
                                  NativeWebRequest request, WebDataBinderFactory binderFactory) {
        String sortDir = request.getParameter("sortDir");
        String sortField = request.getParameter("sortField");
        String keyword = request.getParameter("keyword");

        PagingAndSortingParam annotation = parameter.getParameterAnnotation(PagingAndSortingParam.class);
        String listItemsName = annotation.listItemsName();
        String pathURL = annotation.pathURL();

        return new PagingAndSortingHelper(model, sortDir, sortField, keyword,
                listItemsName, pathURL);
    }
}
