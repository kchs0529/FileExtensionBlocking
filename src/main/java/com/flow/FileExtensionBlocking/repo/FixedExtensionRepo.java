package com.flow.FileExtensionBlocking.repo;

import com.flow.FileExtensionBlocking.domain.FixedExtension;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FixedExtensionRepo extends JpaRepository<FixedExtension, Long> {
    Optional<FixedExtension> findByName(String name);
    boolean existsByName(String name);
}
