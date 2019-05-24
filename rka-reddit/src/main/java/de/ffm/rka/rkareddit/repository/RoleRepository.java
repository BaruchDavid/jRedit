package de.ffm.rka.rkareddit.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.ffm.rka.rkareddit.domain.Role;


public interface RoleRepository extends JpaRepository<Role, Long> {

	Role findByName(String roleName);

}
