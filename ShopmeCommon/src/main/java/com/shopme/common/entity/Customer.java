package com.shopme.common.entity;

import com.shopme.common.entity.setting.country.Country;
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
@Table(name = "customers", uniqueConstraints = {
    @UniqueConstraint(
            name = "uk_customer_full_name",
            columnNames = {"first_name", "last_name"}
    )
})
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 45, unique = true, nullable = false)
    private String email;

    @Column(length = 64, nullable = false)
    private String password;

    @Column(length = 45, nullable = false)
    private String firstName;

    @Column(length = 45, nullable = false)
    private String lastName;

    @Column(length = 15, unique = true, nullable = false)
    private String phoneNumber;

    @Column(length = 128, nullable = false)
    private String addressLine1;

    @Column(length = 128)
    private String addressLine2;

    @Column(length = 45, nullable = false)
    private String city;

    @Column(length = 45, nullable = false)
    private String state;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;

    @Column(length = 10, nullable = false)
    private String postalCode;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdTime;
    private boolean enabled;

    @Column(length = 64)
    private String verificationCode;

    public String getFullName() {
        return firstName + " " + lastName;
    }
}