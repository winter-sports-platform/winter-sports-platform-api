package com.wintersports.repositories.user;

import com.wintersports.entities.User;
import com.wintersports.repositories.IBaseRepository;

import java.util.Optional;

public interface IUserRepository extends IBaseRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
