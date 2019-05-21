package de.ffm.rka.rkareddit.security;

import org.springframework.security.core.GrantedAuthority;

public final class Role{
	
	/**
	 * verifies that the user is admin
	 */
	public static final String ADMIN="ADMIN";
	/**
	 * verifies that the user has logged in
	 */
	public static final String USER="USER"; 
	
	/**
	 * verifies that the user is dba
	 */
	public static final String DBA="DBA";
	
	/**
	 * verifies that the user is actuator
	 */
	public static final String ACTUATOR="ACTUATOR";
	
}
