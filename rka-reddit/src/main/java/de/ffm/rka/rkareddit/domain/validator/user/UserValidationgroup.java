package de.ffm.rka.rkareddit.domain.validator.user;

public interface UserValidationgroup {

    /**
     * marker interface for user changes
     */
	interface ValidationChangeUserProperties {
    }

    /**
     * marker interface for user registration
     */
	interface ValidationUserRegistration {
    }

    /**
     * marker interface for user password change
     */
	interface ValidationUserChangePassword {
    }

    /**
     * marker interface for user password change
     */
	interface ValidationUserChangeEmail {
    }
}
