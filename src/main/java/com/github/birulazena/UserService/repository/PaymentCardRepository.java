package com.github.birulazena.UserService.repository;

import com.github.birulazena.UserService.entity.PaymentCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PaymentCardRepository extends JpaRepository<PaymentCard, Long>, JpaSpecificationExecutor<PaymentCard> {

    PaymentCard save(PaymentCard paymentCard);

    @Query(value = "SELECT * FROM payment_cards WHERE id = :id", nativeQuery = true)
    Optional<PaymentCard> findById(Long id);

    Page<PaymentCard> findAll(Specification<PaymentCard> cardSpecification, Pageable pageable);

    List<PaymentCard> findAllByUserId(Long id);

    @Modifying
    @Query("UPDATE PaymentCard p SET p.active = :active WHERE p.id = :id")
    int updateActivate(Long id, Boolean active);

    void deleteById(Long id);
}
