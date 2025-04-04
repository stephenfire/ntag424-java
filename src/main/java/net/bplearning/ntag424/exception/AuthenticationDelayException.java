package net.bplearning.ntag424.exception;

public class AuthenticationDelayException extends Cla90Exception {
    public AuthenticationDelayException() {
        super("AUTHENTICATION_DELAY: Currently not allowed to authenticate. Keep trying until full delay is spent.");
    }

    public AuthenticationDelayException(String message) {
        super(message);
    }
}
