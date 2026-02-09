package com.github.birulazena.UserService.specification;

import com.github.birulazena.UserService.entity.PaymentCard;
import org.springframework.data.jpa.domain.Specification;

public class PaymentCardSpecification {

    public static Specification<PaymentCard> hasFirstName(String name) {
        return (root, query, criteriaBuilder) -> {
            if(name == null)
                return criteriaBuilder.conjunction();
            return criteriaBuilder.equal(root.join("user").get("name"), name);
        };
    }

    public static Specification<PaymentCard> hasSurname(String surname) {
        return (root, query, criteriaBuilder) -> {
          if(surname == null)
              return criteriaBuilder.conjunction();
          return criteriaBuilder.equal(root.join("user").get("surname"), surname);
        };
    }
}
