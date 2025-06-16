package com.shopme.common.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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

    private Float length;
    private Float width;
    private Float height;
    private Float weight;

    private Float averageRating;
    private Integer reviewCount;
    private Float cost;

    public void addExtraImage(String imageName) {
        if (extraImages == null)
            extraImages = new HashSet<>();

        ProductImage productImage = ProductImage.builder()
                .name(imageName)
                .product(this) // set product_id to current product
                .build();

        extraImages.add(productImage);
    }
}
