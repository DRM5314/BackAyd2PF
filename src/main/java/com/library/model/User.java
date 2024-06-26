package com.library.model;

import com.library.enums.Rol;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId", nullable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Rol role;

    @Column(name = "name", nullable = false, length = 60)
    private String name;

    @Column(name = "email", nullable = false, length = 60)
    private String email;

    @Column(name = "username", nullable = false, length = 45)
    private String username;

    @Column(name = "password", nullable = false, length = 500)
    private String password;

    @Column(name = "status")
    private Integer status;

}