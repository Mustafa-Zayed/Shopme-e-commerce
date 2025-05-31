package com.shopme.common.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 256, unique = true, nullable = false)
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

    private Float length;
    private Float width;
    private Float height;
    private Float weight;

    private Float averageRating;
    private Integer reviewCount;
    private Float cost;
}
