package com.spring.petcareConnect.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.spring.petcareConnect.enums.RoleName;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(exclude = {"users"})
@Entity
@Table(name = "roles", indexes = {
        @Index(name = "idx_role_name", columnList = "role_name", unique = true)
})
public class Role implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleId;

    @NotNull(message = "Role name cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "role_name", nullable = false, unique = true, length = 50)
    private RoleName roleName;

    @ToString.Exclude
    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    @JsonBackReference
    private Set<User> users = new HashSet<>();

    @Override
    public String getAuthority() {
        return roleName.name();
    }
}