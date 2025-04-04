package net.bplearning.ntag424.exception;

public class BoundaryErrorException extends Cla90Exception {
    public BoundaryErrorException() {
        super("BOUNDARY_ERROR: Attempt to read/write data from/to beyond the file’s/record’s limits. Attempt to exceed the limits of a value file.");
    }

    public BoundaryErrorException(String message) {
        super(message);
    }
}
