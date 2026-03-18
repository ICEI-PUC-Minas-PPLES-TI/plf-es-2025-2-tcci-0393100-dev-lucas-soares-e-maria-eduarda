package br.pucminas.graphtest.adapters.inbound.security;

import br.pucminas.graphtest.application.domain.model.UserProfileEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@Builder
@Getter
@AllArgsConstructor
public class UserDetailsAdapter implements UserDetails {

    private UUID id;
    private String email;
    private String senha;
    private UserProfileEnum perfilUsuario;

    public boolean ehPerfil(UserProfileEnum perfilUsuario) {
        return this.perfilUsuario.equals(perfilUsuario);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(this.perfilUsuario.getDescricao()));
    }

    @Override
    public String getPassword() {
        return this.senha;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

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
}
