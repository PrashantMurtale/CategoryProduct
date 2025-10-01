package com.Rest.CategoryProduct.Entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Data // Lombok for getters/setters
public class User { // No longer implements UserDetails since we're using builder in service

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    private String email;

    @Column(nullable = false)
    private String password;

    private String role = "USER"; // Single role; change to Set<String> roles for multiple

    // If you add these for custom flags (optional):
    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;
    private boolean enabled = true;

    // Getters for flags (if not using Lombok fully):
    public boolean isAccountNonExpired() { return accountNonExpired; }
    public boolean isAccountNonLocked() { return accountNonLocked; }
    public boolean isCredentialsNonExpired() { return credentialsNonExpired; }
    public boolean isEnabled() { return enabled; }
}
