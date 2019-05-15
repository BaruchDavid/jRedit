package de.ffm.rka.rkareddit.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import de.ffm.rka.rkareddit.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);
}
