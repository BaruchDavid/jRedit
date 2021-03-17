package de.ffm.rka.rkareddit.exception;

public class IllegalVoteException extends RuntimeException {

    public IllegalVoteException() {
        super();
    }

    /**
     * Constructs an <code>IllegalVoteException</code> with the
     * specified detail message.
     * @param  error the detail message.
     */
    public IllegalVoteException(String error) {
        super(error);
    }
}
