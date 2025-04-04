package net.bplearning.ntag424.exception;

public class IllegalCommandCodeException extends Cla90Exception {
    public IllegalCommandCodeException() {
        super("ILLEGAL_COMMAND_CODE: Command code not supported.");
    }

    public IllegalCommandCodeException(String message) {
        super(message);
    }
}
