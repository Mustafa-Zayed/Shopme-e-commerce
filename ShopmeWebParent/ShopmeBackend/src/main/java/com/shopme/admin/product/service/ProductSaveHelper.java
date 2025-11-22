package com.shopme.admin.product.service;

import com.shopme.admin.utility.FileUploadUtil;
import com.shopme.common.entity.product.Product;
import com.shopme.common.entity.product.ProductImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

public class ProductSaveHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductSaveHelper.class);

    static void setProductTimestamps(Product product) {
        product.setUpdatedTime(new Date());
        if (product.getId() == null) {
            product.setCreatedTime(new Date());
        }
    }

    static void setProductAlias(Product product) {
        if (product.getAlias() == null || product.getAlias().isEmpty())
            product.setAlias(product.getName().replaceAll(" ", "-"));
        else
            product.setAlias(product.getAlias().replaceAll(" ", "-"));
    }

    static void setMainImage(Product product, MultipartFile mainImageMultipart) {
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

    static void setExistingExtraImages(Product product, String[] extraImageIDs, String[] extraImageNames) {
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

    static void setNewAddedExtraImages(Product product, MultipartFile[] extraImageMultiparts) {
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

    static void setProductDetails(Product product, String[] detailIDs,
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

    static void saveMainImageFile(Product product, MultipartFile mainImageMultipart) throws IOException {
        if (mainImageMultipart != null && !mainImageMultipart.isEmpty()) {
            String uploadDir = "../product-images/" + product.getId();
            FileUploadUtil.saveFile(uploadDir, product.getMainImage(), mainImageMultipart);
        }
    }

    static void saveExtraImageFiles(Product product, MultipartFile[] extraImageMultiparts) throws IOException {
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

    static void deleteRemovedExtraImagesFromFileSystem(Product product) {
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

}
