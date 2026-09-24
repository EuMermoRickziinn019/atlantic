package com.atlantic.ISBServices;

import com.atlantic.models.user.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ISBServicesUser extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
}
