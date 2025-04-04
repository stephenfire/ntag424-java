package net.bplearning.ntag424.exception;

public class PermissionDeniedException extends Cla90Exception {
    public PermissionDeniedException() {
        super("PERMISSION_DENIED: Current configuration / status does not allow the requested command.");
    }

    public PermissionDeniedException(String message) {
        super(message);
    }
}
