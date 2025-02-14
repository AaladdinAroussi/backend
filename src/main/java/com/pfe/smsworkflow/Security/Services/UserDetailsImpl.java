package com.pfe.smsworkflow.Security.Services;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.pfe.smsworkflow.Models.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

/*
 * La classe UserDetailsImpl implémente l'interface UserDetails de Spring Security pour fournir
 * les détails nécessaires à l'authentification et à l'autorisation d'un utilisateur,
 * en encapsulant des informations comme l'ID, le nom d'utilisateur,
 * l'email, le mot de passe (qui est ignoré en sérialisation via @JsonIgnore),
 * et les rôles sous forme de GrantedAuthority, qui sont utilisés pour déterminer les permissions
 * de l'utilisateur. Elle fournit également des méthodes pour vérifier l'état du compte,
 * telles que si le compte est expiré, verrouillé ou si les identifiants sont valides.
 */

public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String phone;
    private String email;

    @JsonIgnore
    private String password;

    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Long id, String phone, String email, String password,
                           Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    /*
     * La méthode 'build' permet de construire un objet UserDetailsImpl à partir d'un objet User.
     * Elle extrait les rôles de l'utilisateur et les convertit en une liste de GrantedAuthority.
     */
    public static UserDetailsImpl build(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name())) // Récupère les rôles
                .collect(Collectors.toList());

        // Retourne une nouvelle instance de UserDetailsImpl avec les informations de l'utilisateur
        return new UserDetailsImpl(
                user.getId(),
                user.getPhone(),
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities; // Récupère les autorités (rôles)
    }

    public Long getId() {
        return id; // Récupère l'ID de l'utilisateur
    }

    public String getEmail() {
        return email; // Récupère l'email de l'utilisateur
    }
    public String getPhone() {
        return phone; // Récupère phone de l'utilisateur
    }


    @Override
    public String getPassword() {
        return password; // Récupère le mot de passe (mais ignoré lors de la sérialisation JSON)
    }

    @Override
    public String getUsername() {return phone; }// Récupère le numéro de téléphone comme nom d'utilisateur

    /*
     * Ces méthodes retournent toujours 'true', car dans ce cas, on suppose que l'utilisateur
     * n'a pas de restrictions spécifiques (compte non expiré, non verrouillé, etc.).
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    /*
     * La méthode equals compare l'ID de l'utilisateur pour déterminer si deux objets UserDetailsImpl
     * sont égaux. Cette méthode est utile pour Spring Security pour vérifier les identifiants de l'utilisateur.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }
}
