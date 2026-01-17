package com.shopme.admin.product.service;

import com.shopme.admin.utility.paging_and_sorting.PagingAndSortingHelper;
import com.shopme.common.exception.ProductNotFoundException;
import com.shopme.admin.product.repository.ProductRepository;
import com.shopme.admin.utility.FileUploadUtil;
import com.shopme.common.entity.product.Product;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.*;

@RequiredArgsConstructor
@Service
public class ProductService {
    private final ProductRepository productRepository;
    public static final int PRODUCTS_PER_PAGE = 5;


    public List<Product> listAll() {
        return (List<Product>) productRepository.findAll(Sort.by("id").ascending());
    }

    public void listByPageWithSorting(int pageNumber, PagingAndSortingHelper helper, Integer prodCatId) {
        String keyword = helper.keyword();
        keyword = (keyword == null || keyword.isEmpty()) ? "" : keyword; // set a default value if keyword is null or empty for the sake of the condition below

        Pageable pageable = helper.createPageable(pageNumber, PRODUCTS_PER_PAGE);
        Page<Product> page;

        if (keyword.isEmpty() && prodCatId.equals(0)) {
            page = productRepository.findAll(pageable);
        } else if (prodCatId.equals(0))
            page = productRepository.findAll(keyword, pageable);
        else
            page = productRepository.findAll(keyword, prodCatId, pageable);

        helper.addToModel(page, pageNumber);
    }



    public Product save(Product product) {
        return productRepository.save(product);
    }

    public Product save(Product product,
                        RedirectAttributes redirectAttributes,
                        MultipartFile mainImageMultipart,
                        MultipartFile[] extraImageMultiparts,
                        String[] detailNames,
                        String[] detailValues,
                        String[] extraImageIDs,
                        String[] extraImageNames,
                        String[] detailIDs) throws IOException {
        String message = product.getId() == null ?
                "New Product has been created!" : "Product has been updated successfully!";

        ProductSaveHelper.setProductTimestamps(product);
        ProductSaveHelper.setProductAlias(product);

        ProductSaveHelper.setMainImage(product, mainImageMultipart);
        ProductSaveHelper.setExistingExtraImages(product, extraImageIDs, extraImageNames);
        ProductSaveHelper.setNewAddedExtraImages(product, extraImageMultiparts);

        ProductSaveHelper.setProductDetails(product, detailIDs, detailNames, detailValues); // better approach

        // alternative approach for setting product details (less efficient approach)
//        setExistingProductDetails(product, detailIDs, detailNames, detailValues);
//        setNewAddedProductDetails(product, detailNames, detailValues);

        Product savedProduct = productRepository.save(product);

        ProductSaveHelper.saveMainImageFile(savedProduct, mainImageMultipart);
        ProductSaveHelper.saveExtraImageFiles(savedProduct, extraImageMultiparts);

        ProductSaveHelper.deleteRemovedExtraImagesFromFileSystem(product);

        redirectAttributes.addFlashAttribute("message", message);
        return savedProduct;
    }

    public void save(Product product, RedirectAttributes redirectAttributes) throws ProductNotFoundException {
        Integer id = product.getId();
        Product prodInDB = findById(id);

        prodInDB.setCost(product.getCost());
        prodInDB.setPrice(product.getPrice());
        prodInDB.setDiscountPercent(product.getDiscountPercent());

        save(prodInDB);

        String message = "Product has been updated successfully!";
        redirectAttributes.addFlashAttribute("message", message);
    }

    public boolean checkUniqueName(String name, Integer id) {
        Product byName  = productRepository.findByName(name);
        if (byName == null)
            return true;

        // Check if the name belongs to the edited product (i.e. user doesn't need to change the product name)
        // If new product case, returns false as the id param is null
        return byName.getId().equals(id);
    }

    public Product findById(int id) throws ProductNotFoundException {
        return productRepository.findById(id).orElseThrow(
                () -> new ProductNotFoundException("Could not find any product with ID: " + id)
        );
    }

    @Transactional
    public void updateProductEnabledStatus(int id, boolean status) {
        productRepository.updateProductEnabledStatus(id, status);
    }

    public void delete(int id) throws ProductNotFoundException {
        Integer count = productRepository.countById(id);
        if (count == 0)
            throw new ProductNotFoundException("Could not find any product with ID: " + id);

        productRepository.deleteById(id);
        // remove extra dir first so the main dir can be removed
        FileUploadUtil.removeDir("../product-images/" + id + "/extras");
        FileUploadUtil.removeDir("../product-images/" + id);
    }
}
