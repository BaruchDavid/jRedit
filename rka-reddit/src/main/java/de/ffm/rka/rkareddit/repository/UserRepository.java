package de.ffm.rka.rkareddit.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import de.ffm.rka.rkareddit.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);
	Optional<User> findByEmailAndActivationCode(String email, String code);
	
	@Query("SELECT usr "
			+ "FROM User usr "
			+ "JOIN FETCH usr.userLinks "
			+ "WHERE usr.userId =:userId")
	User getSizeForLinkByUser(@Param("userId") long userId);
	
	
	@Query("SELECT usr "
			+"FROM User usr "
			+ "JOIN FETCH usr.userComments "
			+ "WHERE usr.userId =:userId")
	User getSizeForCommentsByUser(@Param("userId") long userId);
	
}
