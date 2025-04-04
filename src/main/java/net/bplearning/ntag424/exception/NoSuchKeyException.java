package net.bplearning.ntag424.exception;

public class NoSuchKeyException extends Cla90Exception {
    public NoSuchKeyException() {
        super("NO_SUCH_KEY: Invalid key number specified.");
    }

    public NoSuchKeyException(String message) {
        super(message);
    }
}
