package de.ffm.rka.rkareddit.domain.validator.user;

public interface UserValidationGroup {

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
     * marker interface for authenticated user password change
     */
    interface ValidationUserChangePassword {
    }

    /**
     * marker interface for unauthenticated user password change
     */
    interface UnauthenticatedUserRecoverPassword {
    }

    /**
     * marker interface for user password change
     */
    interface ValidationUserChangeEmail {
    }
}
