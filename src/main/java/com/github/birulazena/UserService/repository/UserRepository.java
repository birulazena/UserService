package com.github.birulazena.UserService.repository;

import com.github.birulazena.UserService.entity.User;
import jakarta.persistence.Entity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    @EntityGraph(attributePaths = "cards")
    Optional<User> findById(Long id);

    Page<User> findAll(Specification<User> specification, Pageable pageable);

    @Modifying
    @Query(value = "UPDATE users SET active = :active WHERE id = :id",
    nativeQuery = true)
    int updateActive(Long id, Boolean active);

}
