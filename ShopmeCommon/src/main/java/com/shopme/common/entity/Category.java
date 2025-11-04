package com.shopme.common.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;
import java.util.Set;

@Builder
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 128, nullable = false, unique = true)
    @ToString.Include
    private String name;

    @Column(length = 64, nullable = false, unique = true)
    private String alias;

    @Column(length = 128, nullable = false)
    private String image;

    private boolean enabled;

    @Column(name = "all_parent_ids", length = 256)
    private String allParentIDs;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    // @OrderBy("name ASC")
    @ToString.Exclude
    private Set<Category> children;

    @ManyToMany(mappedBy = "categories")
    @ToString.Exclude
    private Set<Brand> brands;

    @Transient
    public String getImagePath() {
        if (Objects.equals(image, "default.png") || id == null)
            return "/images/image-thumbnail.png";
        return "/category-photos/" + id + "/" + image;
    }

    @Transient
    public boolean hasChildren() {
        return children != null && !children.isEmpty();
    }
}
