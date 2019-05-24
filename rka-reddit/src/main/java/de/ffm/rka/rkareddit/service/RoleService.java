package de.ffm.rka.rkareddit.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import de.ffm.rka.rkareddit.domain.Role;
import de.ffm.rka.rkareddit.repository.RoleRepository;

@Service
public class RoleService {
	private static final Logger LOGGER = LoggerFactory.getLogger(RoleService.class);
	private RoleRepository roleRepository;
	
	
	public RoleService(RoleRepository roleRepository) {
		this.roleRepository = roleRepository;
	}


	public Role findByName(String roleName) {
		LOGGER.info("TRY TO FIND ROLE {}", roleName);
		return roleRepository.findByName(roleName);
	}
}
