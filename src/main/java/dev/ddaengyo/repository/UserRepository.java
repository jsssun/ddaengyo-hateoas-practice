package dev.ddaengyo.repository;

import dev.ddaengyo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
