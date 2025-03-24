package com.shopme.common.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 128, unique = true, nullable = false)
    private String email;

    @Column(length = 64, nullable = false)
    private String password;

    @Column(length = 45, unique = true, nullable = false)
    private String firstName;

    @Column(length = 45, unique = true, nullable = false)
    private String lastName;

    @Column(length = 64)
    private String photos;
    private boolean enabled;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    // convenience method for adding roles
    public void addRole(Role role) {
        if (this.roles == null)
            this.roles = new HashSet<>();

        this.roles.add(role);
    }

    @Transient
    public String getPhotosImagePath() {
        if (photos == null || id == null)
            return "/images/default-user.png";
        return "/user-photos/" + id + "/" + photos;
    }

    @Transient
    public String getFullName() {
        return firstName + " " + lastName;
    }
}