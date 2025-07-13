package com.shopme.admin.product.service;

import com.shopme.admin.product.exception.ProductNotFoundException;
import com.shopme.admin.product.repository.ProductRepository;
import com.shopme.admin.utils.FileUploadUtil;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Product;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class ProductService {
    private final ProductRepository productRepository;

    public List<Product> listAll() {
        return (List<Product>) productRepository.findAll(Sort.by("id").ascending());
    }

    public Page<Product> listByPageWithSorting(int pageNumber, String sortField, String sortDir,
                                               String keyword) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        // page number is a 0-based index, but sent from the client as a 1-based index, so we need to subtract 1.
        Pageable pageable = PageRequest.of(pageNumber - 1, 4, sort);

        if (keyword.isEmpty())
            return productRepository.findAll(pageable);

        return productRepository.findAll(keyword, pageable);
    }

    public Product save(Product brand) {
        return productRepository.save(brand);
    }

    public Product save(Product product,
                        RedirectAttributes redirectAttributes,
                        MultipartFile mainImageMultipart,
                        MultipartFile ...extraImageMultiparts) throws IOException {
        String message = product.getId() == null ?
                "New Product has been created!" : "Product has been updated successfully!";

        product.setUpdatedTime(new Date());

        if (product.getId() == null) {
            product.setCreatedTime(new Date());
        }

        if (product.getAlias() == null || product.getAlias().isEmpty())
            product.setAlias(product.getName().replaceAll(" ", "-"));
        else
            product.setAlias(product.getAlias().replaceAll(" ", "-"));

        saveMainImageMultipart(product, mainImageMultipart);

        saveExtraImageMultiparts(product, extraImageMultiparts);

        redirectAttributes.addFlashAttribute("message", message);
        return productRepository.save(product);
    }

    private void saveMainImageMultipart(Product product, MultipartFile mainImageMultipart) throws IOException {
        if (!mainImageMultipart.isEmpty()) {
            String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(mainImageMultipart.getOriginalFilename()));
            product.setMainImage(originalFilename);
            Product savedProduct = save(product); // product == savedProduct: true
            System.out.println("product.getId(): " + product.getId());
            String uploadDir = "../product-images/" + savedProduct.getId(); // product.getId() works as well, because the brand and savedProduct objects are the same.
            FileUploadUtil.saveFile(uploadDir, originalFilename, mainImageMultipart);
        } else {
            System.out.println("product.getMainImage(): " + product.getMainImage()); // product.getMainImage():
            // In the create mode, if the user does not upload a new file, photos field will sent as
            // empty string "", not null, because of <input type="hidden" th:field="*{mainImage}"> in the
            // product_form, and that will cause a constraint violation in getMainImage() method in
            // Product class. So we must set mainImage to default.png.
            if (product.getMainImage().isEmpty()) // not needed, just for the sake of completeness, as the image field is not nullable
                product.setMainImage("default.png");
            save(product);
        }
    }

    private void saveExtraImageMultiparts(Product product, MultipartFile[] extraImageMultiparts) throws IOException {
        for (MultipartFile multipartFile : extraImageMultiparts) {
            if (!multipartFile.isEmpty()) {
                String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
                System.out.println("originalFilename: " + originalFilename);
                product.addExtraImage(originalFilename);
                Product savedProduct = save(product);
                String uploadDir = "../product-images/" + savedProduct.getId() + "/extras/";
                FileUploadUtil.saveFile(uploadDir, originalFilename, multipartFile);
            }
        }
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
