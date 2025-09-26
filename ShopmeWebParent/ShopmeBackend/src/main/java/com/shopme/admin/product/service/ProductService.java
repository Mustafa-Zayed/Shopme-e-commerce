package com.shopme.admin.product.service;

import com.shopme.admin.product.exception.ProductNotFoundException;
import com.shopme.admin.product.repository.ProductRepository;
import com.shopme.admin.utils.FileUploadUtil;
import com.shopme.common.entity.Product;
import com.shopme.common.entity.ProductDetail;
import com.shopme.common.entity.ProductImage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class ProductService {
    private final ProductRepository productRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);
    public static final int PRODUCTS_PER_PAGE = 5;


    public List<Product> listAll() {
        return (List<Product>) productRepository.findAll(Sort.by("id").ascending());
    }

    public Page<Product> listByPageWithSorting(int pageNumber, String sortField, String sortDir,
                                               String keyword, Integer prodCatId) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        // page number is a 0-based index, but sent from the client as a 1-based index, so we need to subtract 1.
        Pageable pageable = PageRequest.of(pageNumber - 1, PRODUCTS_PER_PAGE, sort);

        if (keyword.isEmpty() && prodCatId.equals(0))
            return productRepository.findAll(pageable);

        if (prodCatId.equals(0))
            return productRepository.findAll(keyword, pageable);

        return productRepository.findAll(keyword, prodCatId, pageable);
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

        setProductTimestamps(product);
        setProductAlias(product);

        setMainImage(product, mainImageMultipart);
        setExistingExtraImages(product, extraImageIDs, extraImageNames);
        setNewAddedExtraImages(product, extraImageMultiparts);

        setProductDetails(product, detailIDs, detailNames, detailValues); // better approach

        // alternative approach for setting product details (less efficient approach)
//        setExistingProductDetails(product, detailIDs, detailNames, detailValues);
//        setNewAddedProductDetails(product, detailNames, detailValues);

        Product savedProduct = productRepository.save(product);

        saveMainImageFile(savedProduct, mainImageMultipart);
        saveExtraImageFiles(savedProduct, extraImageMultiparts);

        deleteRemovedExtraImagesFromFileSystem(product);

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

    private void setProductTimestamps(Product product) {
        product.setUpdatedTime(new Date());
        if (product.getId() == null) {
            product.setCreatedTime(new Date());
        }
    }

    private void setProductAlias(Product product) {
        if (product.getAlias() == null || product.getAlias().isEmpty())
            product.setAlias(product.getName().replaceAll(" ", "-"));
        else
            product.setAlias(product.getAlias().replaceAll(" ", "-"));
    }

    private void setMainImage(Product product, MultipartFile mainImageMultipart) {
        if (mainImageMultipart != null && !mainImageMultipart.isEmpty()) {
            String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(mainImageMultipart.getOriginalFilename()));
            product.setMainImage(originalFilename);
        } else {
            // In the create mode, if the user does not upload a new file, photos field will sent as
            // empty string "", not null, because of <input type="hidden" th:field="*{mainImage}"> in the
            // product_form, and that will cause a constraint violation in getMainImage() method in
            // Product class. So we must set mainImage to default.png.
            if (product.getMainImage() == null || product.getMainImage().isEmpty())
                product.setMainImage("default.png"); // not needed as the image field is not nullable, just for the sake of completeness.
        }
    }

    private void setExistingExtraImages(Product product, String[] extraImageIDs, String[] extraImageNames) {
        if (extraImageIDs == null || extraImageIDs.length == 0)
            return;

        Set<ProductImage> newProductImages = new HashSet<>();

        for (int i = 0; i < extraImageIDs.length; i++) {
            if (!extraImageIDs[i].isBlank() && !extraImageNames[i].isBlank()) {
                Integer id = Integer.parseInt(extraImageIDs[i]);
                String name = extraImageNames[i];

                ProductImage productImage = ProductImage.builder()
                        .id(id)
                        .name(name)
                        .product(product) // set product_id to current product
                        .build();

                newProductImages.add(productImage);
            }
        }

        // In this step, any extra image without hidden name or id inputs is removed, and the remaining ones
        // are added. Because of the orphanRemoval setting in the @OneToMany mapping, the removed image will
        // also be deleted from the database.

        // product.setExtraImages(newProductImages); // this are replacing the whole collection reference that Hibernate is tracking, it gets an error when removing the whole extra images so newProductImages still equals new HashSet<>() and we don't initialize the extraImages field in Product entity.

        // Instead of replacing the collection reference, modify the existing one
        // This way: Hibernate still tracks the same Set object, Orphans (images removed from the set)
        // will be detected and deleted, New images will be persisted.
        product.getExtraImages().clear();
        product.getExtraImages().addAll(newProductImages);

    }

    private void setNewAddedExtraImages(Product product, MultipartFile[] extraImageMultiparts) {
        if (extraImageMultiparts != null) {
            for (MultipartFile multipartFile : extraImageMultiparts) {
                if (!multipartFile.isEmpty()) {
                    String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
                    if (!product.containsImage(originalFilename)) // to avoid duplicates
                        product.addExtraImage(originalFilename);
                }
            }
        }
    }

    private void setProductDetails(Product product, String[] detailIDs,
                                           String[] detailNames, String[] detailValues) {
            if (detailNames == null || detailNames.length == 0)
                return;

            for (int i = 0; i < detailNames.length; i++){
                int id = Integer.parseInt(detailIDs[i]);
                String name = detailNames[i];
                String value = detailValues[i];

                if (!product.containsDetail(detailNames[i], detailValues[i])) { // to avoid duplicates
                    if (id != 0) { // update an existing detail
                        product.addProductDetails(id, name, value);
                    } else if (!detailNames[i].isBlank() && !detailValues[i].isBlank()) { // add a new detail
                        product.addProductDetails(name, value);
                    }
                }
            }
    }

// alternative approach for setting product details (less efficient approach)
//    private void setExistingProductDetails(Product product, String[] detailIDs,
//                                           String[] detailNames, String[] detailValues) {
//        if (detailIDs == null || detailIDs.length == 0)
//            return;
//
//        List<ProductDetail> newProductDetails = new ArrayList<>();
//
//        for (int i = 0; i < detailIDs.length; i++) {
//            if (!detailIDs[i].isBlank() && !detailNames[i].isBlank() && !detailValues[i].isBlank()) {
//                Integer id = Integer.parseInt(detailIDs[i]);
//                String name = detailNames[i];
//                String value = detailValues[i];
//
//                ProductDetail productDetail = ProductDetail.builder()
//                        .id(id)
//                        .name(name)
//                        .value(value)
//                        .product(product) // set product_id to current product
//                        .build();
//
//                newProductDetails.add(productDetail);
//            }
//        }
//
//        product.getProductDetails().clear();
//        product.getProductDetails().addAll(newProductDetails);
//    }
//
//    private void setNewAddedProductDetails(Product product, String[] detailNames, String[] detailValues) {
//        if (detailNames != null && detailValues != null) {
//            for (int i = 0; i < detailNames.length; i++) {
//                if (!detailNames[i].isBlank() && !detailValues[i].isBlank()) {
//                    if (!product.containsDetail(detailNames[i], detailValues[i])) // to avoid duplicates
//                        product.addProductDetails(detailNames[i], detailValues[i]);
//                }
//            }
//        }
//    }

    private void saveMainImageFile(Product product, MultipartFile mainImageMultipart) throws IOException {
        if (mainImageMultipart != null && !mainImageMultipart.isEmpty()) {
            String uploadDir = "../product-images/" + product.getId();
            FileUploadUtil.saveFile(uploadDir, product.getMainImage(), mainImageMultipart);
        }
    }

    private void saveExtraImageFiles(Product product, MultipartFile[] extraImageMultiparts) throws IOException {
        if (extraImageMultiparts != null) {
            for (MultipartFile multipartFile : extraImageMultiparts) {
                if (!multipartFile.isEmpty()) {
                    String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
                    String uploadDir = "../product-images/" + product.getId() + "/extras/";
                    FileUploadUtil.saveFile(uploadDir, originalFilename, multipartFile);
                }
            }
        }
    }

    private void deleteRemovedExtraImagesFromFileSystem(Product product) {
        String extraImagesDir = "../product-images/" + product.getId() + "/extras/";
        Path extraImagesPath = Path.of(extraImagesDir);

        try(Stream<Path> pathStream = Files.list(extraImagesPath)) {
            pathStream.forEach(image -> {
                String imageName = image.toFile().getName();
                if (!product.containsImage(imageName))
                    FileUploadUtil.removeFile(image);
            });
        } catch (IOException e) {
            LOGGER.error("Error listing directory: {}", extraImagesDir);
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
