package net.bplearning.ntag424.exception;

public class IntegrityErrorException extends Cla90Exception {
    public IntegrityErrorException() {
        super("INTEGRITY_ERROR: CRC or MAC does not match data. Padding bytes not valid.");
    }

    public IntegrityErrorException(String message) {
        super(message);
    }
}
