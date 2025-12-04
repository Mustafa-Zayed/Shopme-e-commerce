package com.shopme.common.entity.setting.country;

import com.shopme.common.entity.setting.state.State;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "countries")
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 45, nullable = false, unique = true)
    private String name;

    @Column(length = 5, nullable = false, unique = true)
    private String code;

    @OneToMany(mappedBy = "country")
    @ToString.Exclude
    private Set<State> states;
}
