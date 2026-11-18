package io.github.carlossmariano.comunex.usuario.security;

import io.github.carlossmariano.comunex.usuario.entity.Usuario;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UsuarioDetails implements UserDetails {

    private final Usuario usuario;

    public UsuarioDetails(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
                new SimpleGrantedAuthority(
                        "ROLE_"+usuario.getTipoPerfil().name()
                )
        );
    }

    @Override
    public @Nullable String getPassword() {
        return usuario.getSenha();
    }

    @Override
    public String getUsername() {
        return  usuario.getEmail();
    }

    public Long getId(){
        return usuario.getIdUsuario();
    }

    public Usuario getUsuario(){
        return usuario;
    }
}
