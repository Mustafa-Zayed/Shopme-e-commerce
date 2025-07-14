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
    private Float listPrice;
    private Float discountPercent;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private Set<ProductImage> extraImages;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductDetail> productDetails;

    private Float length;
    private Float width;
    private Float height;
    private Float weight;

    private Float averageRating;
    private Integer reviewCount;
    private Float cost;

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

    @Transient
    public String getMainImagePath() {
        if (Objects.equals(mainImage, "default.png") || id == null)
            return "/images/image-thumbnail.png";
        return "/product-images/" + id + "/" + mainImage;
    }
}
