package de.ffm.rka.rkareddit.repository;


import de.ffm.rka.rkareddit.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
	
	
	Optional<User> findByEmail(String email);
	Optional<User> findByEmailAndActivationCode(String email, String code);
	Optional<User> findByNewEmailAndActivationCode(String email, String activationCode);
	
	/**git
	 * email is unique value like id for fetching Roles
	 * @author RKA
	 */
	@Query("SELECT usr "
			+ "FROM Users usr "
			+ "JOIN FETCH usr.roles "
			+ "WHERE usr.email =:email")
	Optional<User> findByEmailWithRoles(@Param("email") String email);
	
	/**
	 * email is unique value like id for fetching Links
	 * @author RKA
	 */
	@Query("SELECT usr "
			+ "FROM Users usr "
			+ "LEFT OUTER JOIN FETCH usr.userLinks "
			+ "WHERE usr.email =:email")
	Optional<User> fetchUserWithLinks(@Param("email") String userId);
	
	/**
	 * email is unique value like id for fetching comments
	 * @author RKA
	 */
	@Query("SELECT usr "
			+"FROM Users usr "
			+ "LEFT OUTER JOIN FETCH usr.userComment "
			+ "WHERE usr.email =:email")
	Optional<User> fetchUserWithComments(@Param("email") String userId);

	@Query("SELECT usr "
			+ "FROM Users usr "
			+ "LEFT OUTER JOIN FETCH usr.userLinks ln "
			+ "LEFT OUTER JOIN FETCH usr.userComment cm "
			+ "WHERE usr.email =:email")
	Optional<User> fetchUserWithLinksAndComments(@Param("email") String userId);
	
	@Query("SELECT usr "
			+ "FROM Users usr "
			+ "LEFT OUTER JOIN FETCH usr.userClickedLinks "
			+ "WHERE usr.email =:email")
    Optional<User> findClickedUserLinks(@Param("email") String requestedUser);
}
