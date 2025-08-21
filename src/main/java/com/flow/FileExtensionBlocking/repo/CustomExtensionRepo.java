package com.flow.FileExtensionBlocking.repo;

import com.flow.FileExtensionBlocking.domain.CustomExtension;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomExtensionRepo extends JpaRepository<CustomExtension, Long> {
    Optional<CustomExtension> findByName(String name);
    boolean existsByName(String name);
    long count();
}
