package net.bplearning.ntag424.exception;

public class LengthErrorException extends Cla90Exception {
    public LengthErrorException() {
        super("LENGTH_ERROR: Length of command string invalid.");
    }

    public LengthErrorException(String message) {
        super(message);
    }
}
