package com.app.persistence.repository;

import com.app.persistence.entity.RoleEntity;
import com.app.persistence.entity.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByRoleEnum(RoleEnum roleEnum);
}
