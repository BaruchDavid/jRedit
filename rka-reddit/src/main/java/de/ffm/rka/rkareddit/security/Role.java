package de.ffm.rka.rkareddit.security;

public enum Role {

	ADMIN("ADMIN"),
	USER("USER"),
	DBA("DBA"),
	ACTUATOR("ACTUATOR"),
	ANONYMOUS("ANONYMOUS");
	
	private String userRole;
	
	Role(String role) {
		this.userRole = role;
	}
}
