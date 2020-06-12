package de.ffm.rka.rkareddit.domain.validator;

public interface Validationgroups {

	/** marker interface for user changes */
	public interface ValidationChangeUserProperties {
	}
	
	/** marker interface for user registration */
	public interface ValidationUserRegistration {
	}
	
	/** marker interface for user password change */
	public interface ValidationUserChangePassword {
	}
	
	/** marker interface for user password change */
	public interface ValidationUserChangeEmail {
	}
}
