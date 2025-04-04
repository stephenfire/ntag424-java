package net.bplearning.ntag424.exception;

public class AuthenticationErrorException extends Cla90Exception {
    public AuthenticationErrorException() {
        super("AUTHENTICATION_ERROR: Current authentication status does not allow the requested command.");
    }

    public AuthenticationErrorException(String message) {
        super(message);
    }
}
