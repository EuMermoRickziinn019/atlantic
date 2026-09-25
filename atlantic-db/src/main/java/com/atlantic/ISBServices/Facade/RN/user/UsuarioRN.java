package com.atlantic.ISBServices.Facade.RN.user;

import com.atlantic.ISBServices.ISBServicesUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UsuarioRN implements UserDetailsService {
    private final ISBServicesUser usuarioRepository;

    public UsuarioRN(ISBServicesUser usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuário não encontrado com o e-mail: " + email
                ));
    }
}
