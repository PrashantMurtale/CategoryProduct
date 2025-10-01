package com.Rest.CategoryProduct.Security;

import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.Rest.CategoryProduct.Entity.User; // Adjust import path if your User entity is elsewhere
import com.Rest.CategoryProduct.Repository.UserRepository; // Adjust import path if needed

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // Convert user's roles to Spring Security authorities (adjust if your User has a List<Role> instead of a single role)
        var authorities = user.getRoles().stream() // Assuming getRoles() returns Collection<String> or List<Role>
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());

        // If roles is a single String field, use: Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()))

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword()) // Ensure passwords are encoded (e.g., via BCryptPasswordEncoder)
                .authorities(authorities)
                .accountExpired(!user.isAccountNonExpired()) // Map your entity's enabled/expired flags as needed
                .accountLocked(!user.isAccountNonLocked())
                .credentialsExpired(!user.isCredentialsNonExpired())
                .disabled(!user.isEnabled())
                .build();
    }
}
