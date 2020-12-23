package de.ffm.rka.rkareddit.repository;

import de.ffm.rka.rkareddit.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RoleRepository extends JpaRepository<Role, Long> {

	Role findByRoleName(String roleName);

}
