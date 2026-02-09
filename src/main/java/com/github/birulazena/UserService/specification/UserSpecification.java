package com.github.birulazena.UserService.specification;

import com.github.birulazena.UserService.entity.User;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {

    public static Specification<User> hasFirstName(String name) {
        return (root, query, criteriaBuilder) -> {
            if(name == null)
                return criteriaBuilder.conjunction();
            return criteriaBuilder.equal(root.get("name"), name);
        };
    }

    public static Specification<User> hasSurname(String surname) {
        return (root, query, criteriaBuilder) ->  {
            if(surname == null)
                return criteriaBuilder.conjunction();
            return criteriaBuilder.equal(root.get("surname"), surname);
        };
    }

}
