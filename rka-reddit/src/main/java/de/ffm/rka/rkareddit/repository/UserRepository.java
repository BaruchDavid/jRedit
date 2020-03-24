package de.ffm.rka.rkareddit.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import de.ffm.rka.rkareddit.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
	
	
	Optional<User> findByEmail(String email);
	Optional<User> findByEmailAndActivationCode(String email, String code);
	
	/**git
	 * email is unique value like id for fetching Roles
	 * @author RKA
	 */
	@Query("SELECT usr "
			+ "FROM User usr "
			+ "JOIN FETCH usr.roles "
			+ "WHERE usr.email =:email")
	User findByEmailWithRoles(@Param("email") String email);
	
	/**
	 * email is unique value like id for fetching Links
	 * @author RKA
	 */
	@Query("SELECT usr "
			+ "FROM User usr "
			+ "LEFT OUTER JOIN FETCH usr.userLinks "
			+ "WHERE usr.email =:email")
	User fetchUserWithLinks(@Param("email") String userId);
	
	/**
	 * email is unique value like id for fetching comments
	 * @author RKA
	 */
	@Query("SELECT usr "
			+"FROM User usr "
			+ "LEFT OUTER JOIN FETCH usr.userComments "
			+ "WHERE usr.email =:email")
	User fetchUserWithComments(@Param("email") String userId);
	
}
