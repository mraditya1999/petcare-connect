    package com.petconnect.backend.entity;

    import com.fasterxml.jackson.annotation.JsonBackReference;
    import jakarta.persistence.*;
    import lombok.*;
    import org.springframework.security.core.GrantedAuthority;

    import java.util.HashSet;
    import java.util.Set;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString(exclude = {"users"})
    @EqualsAndHashCode(exclude = {"users"})
    @Entity
    @Table(name = "roles")
    public class Role implements GrantedAuthority {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "role_id")
        private Long roleId;

        @Enumerated(EnumType.STRING)
        @Column(name = "role_name", nullable = false, unique = true, length = 50)
        private RoleName roleName;

        @ManyToMany(mappedBy = "roles")
        @JsonBackReference
        private Set<User> users = new HashSet<>();

        @Override
        public String getAuthority() {
            return roleName != null ? roleName.name() : null;
        }

        public enum RoleName {
            ADMIN, SPECIALIST, USER
        }

    }
