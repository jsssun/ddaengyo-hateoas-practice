package dev.ddaengyo.repository;

import dev.ddaengyo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
<<<<<<< HEAD

import org.springframework.stereotype.Repository;

import java.util.Optional;

=======

import org.springframework.stereotype.Repository;

>>>>>>> 68c2edf970f7a52e4c28d9a8aad6fe4f4cd17c03
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}