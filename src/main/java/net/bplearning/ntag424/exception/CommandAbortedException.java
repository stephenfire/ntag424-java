package net.bplearning.ntag424.exception;

public class CommandAbortedException extends Cla90Exception {
    public CommandAbortedException() {
        super("COMMAND_ABORTED: Previous Command was not fully completed. Not all Frames were requested or provided by the PCD.");
    }

    public CommandAbortedException(String message) {
        super(message);
    }
}
