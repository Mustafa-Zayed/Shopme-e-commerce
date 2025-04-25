package com.shopme.admin.category.service;

import com.shopme.admin.category.exception.CategoryNotFoundException;
import com.shopme.admin.category.repository.CategoryRepository;
import com.shopme.admin.utils.FileUploadUtil;
import com.shopme.common.entity.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CategoryService {
    public static final int CATS_PER_PAGE = 4;

    private final CategoryRepository categoryRepository;

    public List<Category> listAll() {
        return (List<Category>) categoryRepository.findAll();
    }

    public Page<Category> listByPageWithSorting(int pageNumber, String sortField, String sortDir,
                                                String keyword) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        // page number is a 0-based index, but sent from the client as a 1-based index, so we need to subtract 1.
        Pageable pageable = PageRequest.of(pageNumber - 1, CATS_PER_PAGE, sort);

        if (keyword.isEmpty()) {
            List<Category> hierarchicalCategories = listCategoriesUsedInFormListApproach();
            return new PageImpl<>(hierarchicalCategories, pageable, hierarchicalCategories.size());

//            return categoryRepository.findAll(pageable);
        }
        return categoryRepository.findAll(keyword, pageable);
    }

//    public Map<Category, String> listCategoriesUsedInFormMapApproach() {
//        List<Category> categories = listAll();
//        Map<Category, String> categoriesUsedInForm = new LinkedHashMap<>();
//
//        categories.forEach(cat -> {
//            if (cat.getParent() == null)
//                listSubCategoriesMapApproach(categoriesUsedInForm, cat, 1);
//        });
//
//        return categoriesUsedInForm;
//    }
//
//    private void listSubCategoriesMapApproach(Map<Category, String> categoriesUsedInForm, Category parent,
//                                    int level) {
//        if (parent.getParent() == null) // root category
//            categoriesUsedInForm.put(parent, parent.getName());
//
//        Set<Category> children = parent.getChildren();
//        for (Category child : children) {
//            categoriesUsedInForm.put(child, "--".repeat(level) + child.getName());
//            if (!child.getChildren().isEmpty())
//                listSubCategoriesMapApproach(categoriesUsedInForm, child,level + 1);
//        }
//    }

    public List<Category> listCategoriesUsedInFormListApproach() {
        List<Category> categories = listAll();
        List<Category> categoriesUsedInForm = new ArrayList<>();

        categories.forEach(cat -> {
            if (cat.getParent() == null)
                listSubCategoriesListApproach(categoriesUsedInForm, cat, 1);
        });

        return categoriesUsedInForm;
    }

    private void listSubCategoriesListApproach(List<Category> categoriesUsedInForm, Category parent,
                                    int level) {
        if (parent.getParent() == null) // root category
            categoriesUsedInForm.add(Category.builder()
                    .id(parent.getId())
                    .name(parent.getName())
                    .alias(parent.getAlias())
                    .image(parent.getImage())
                    .enabled(parent.isEnabled())
                    .parent(parent.getParent())
                    .children(parent.getChildren())
                    .build());

        Set<Category> children = parent.getChildren().stream()
                .sorted(Comparator.comparing(Category::getName))
                .collect(Collectors.toCollection(LinkedHashSet::new));
        for (Category child : children) {
            categoriesUsedInForm.add(Category.builder()
                    .id(child.getId())
                    .name("--".repeat(level) + child.getName())
                    .alias(child.getAlias())
                    .image(child.getImage())
                    .enabled(child.isEnabled())
                    .parent(child.getParent())
                    .children(child.getChildren())
                    .build());
            if (!child.getChildren().isEmpty())
                listSubCategoriesListApproach(categoriesUsedInForm, child,level + 1);
        }
    }

    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    public void save(Category category,
                                 MultipartFile multipart,
                                 RedirectAttributes redirectAttributes) throws IOException {
        String message;
        message = category.getId() == null ?
                "New Category has been created!" : "Category has been updated successfully!";

        if (!multipart.isEmpty()) {
            String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(multipart.getOriginalFilename()));
            category.setImage(originalFilename);
            Category savedCategory = save(category); // category == savedCategory: true
            System.out.println("category.getId(): " + category.getId());
            String uploadDir = "../category-photos/" + savedCategory.getId(); // category.getId() works as well, because the category and savedCategory objects are the same.
            FileUploadUtil.saveFile(uploadDir, originalFilename, multipart);
        } else {
            System.out.println("category.getImage(): " + category.getImage()); // user.getImage():
            // In the create mode, if the user does not upload a new file, photos field will sent as
            // empty string "", not null, because of <input type="hidden" th:field="*{photos}"> in the
            // category_form, and that will cause a constraint violation in getImagePath() method in
            // Category class. So we must set photos to default.png.
            if (category.getImage().isEmpty()) // not needed, just for the sake of completeness, as the image field is not nullable
                category.setImage("default.png");
            save(category);
        }

        redirectAttributes.addFlashAttribute("message", message);
    }

    public Category findById(Integer id) throws CategoryNotFoundException {
        return categoryRepository.findById(id).orElseThrow(
                () -> new CategoryNotFoundException("Could not find any category with Id: " + id)
        );
    }
}
