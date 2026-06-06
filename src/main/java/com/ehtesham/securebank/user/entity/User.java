package com.ehtesham.securebank.user.entity;

import com.ehtesham.securebank.common.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)// insertable = false, removed after adding @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at", updatable = false) // insertable = false, removed after adding @UpdateTimestamp
    private LocalDateTime updatedAt;
}
