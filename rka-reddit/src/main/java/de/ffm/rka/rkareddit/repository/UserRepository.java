package de.ffm.rka.rkareddit.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import de.ffm.rka.rkareddit.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);
	Optional<User> findByEmailAndActivationCode(String email, String code);
	
	@Query("SELECT COUNT(usr.userLinks) "
			+ "FROM User usr "
			+ "JOIN usr.userLinks "
			+ "WHERE usr.userId =:userId")
	List<Long> getSizeForLinkByUser(@Param("userId") long userId);
	
}
