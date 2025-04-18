package com.example.IdentityService.Entity;

import com.example.IdentityService.Entity.Belonging.UserFilmPlaylist;
import com.example.IdentityService.Entity.Reaction.Reaction;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true)
    private String username;

    @Column(unique = true)
    private String email;

    private String password;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "ip_address")
    private String ipAddress;

    private String country, role;

    @Column(name = "avatar_name")
    private String avatarName;

    @Column(name = "date_of_birth")
    private LocalDate DOB;

    @Lob
    @Column(name = "avatar_data", columnDefinition = "LONGBLOB")
    private byte[] avatarData;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<UserFilmPlaylist> userFilmPlaylist;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Reaction> reaction;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Comment> comments = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Watching> watchings = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> role);
    }
}
