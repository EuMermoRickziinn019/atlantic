package com.atlantic.atlanticapi.core.ISBServices;

import com.atlantic.atlanticapi.models.adm.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ISBServicesUser extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
}