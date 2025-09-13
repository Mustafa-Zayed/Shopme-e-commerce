package com.shopme.common.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Builder
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private Integer id;

    @Column(length = 256, unique = true, nullable = false)
    @ToString.Include
    private String name;

    @Column(length = 256, unique = true, nullable = false)
    private String alias;

    @Column(length = 1024, nullable = false)
    private String shortDescription;

    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String fullDescription;

    @Column(length = 45, nullable = false)
    private String mainImage;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdTime;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedTime;

    private boolean enabled;
    private boolean inStock;

    private Float cost;
    private Float price;
    private Float discountPercent;

    private Float length;
    private Float width;
    private Float height;
    private Float weight;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProductImage> extraImages = new HashSet<>(); // initialization is required here to avoid: A collection with cascade="all-delete-orphan" was no longer referenced by the owning entity instance: com.shopme.common.entity.Product.extraImages

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductDetail> productDetails = new ArrayList<>(); // initialization is required here to avoid: A collection with cascade="all-delete-orphan" was no longer referenced by the owning entity instance: com.shopme.common.entity.Product.productDetails

    // convenient method to add an extra image
    public void addExtraImage(String imageName) {
        if (extraImages == null)
            extraImages = new HashSet<>();

        ProductImage productImage = ProductImage.builder()
                .name(imageName)
                .product(this) // set product_id to current product
                .build();

        extraImages.add(productImage);
    }

    // convenient method to add product details
    public void addProductDetails(String name, String value) {
        if (productDetails == null)
            productDetails = new ArrayList<>();

        ProductDetail productDetail = ProductDetail.builder()
                .name(name)
                .value(value)
                .product(this) // set product_id to current product
                .build();

        productDetails.add(productDetail);
    }

    public void addProductDetails(Integer id, String name, String value) {
        if (productDetails == null)
            productDetails = new ArrayList<>();

        ProductDetail productDetail = ProductDetail.builder()
                .id(id)
                .name(name)
                .value(value)
                .product(this) // set product_id to current product
                .build();

        productDetails.add(productDetail);
    }

    @Transient
    public String getMainImagePath() {
        if (Objects.equals(mainImage, "default.png") || id == null)
            return "/images/image-thumbnail.png";
        return "/product-images/" + id + "/" + mainImage;
    }

    @Transient
    public boolean containsImage(String imageName) {
        if (extraImages == null || extraImages.isEmpty())
            return false;
        return extraImages.stream().anyMatch(image -> image.getName().equals(imageName));
    }

    @Transient
    public boolean containsDetail(String detailName, String detailValue) {
        if (productDetails == null || productDetails.isEmpty())
            return false;
        return productDetails.stream().anyMatch(detail ->
                detail.getName().equals(detailName) && detail.getValue().equals(detailValue)
        );
    }

    @Transient
    public String getShortName() {
        if (name.length() > 70)
            return name.substring(0, 70).concat("...");
        return name;
    }
}
