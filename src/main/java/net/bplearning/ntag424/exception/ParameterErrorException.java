package net.bplearning.ntag424.exception;

public class ParameterErrorException extends Cla90Exception {
    public ParameterErrorException() {
        super("PARAMETER_ERROR: Value of the parameter(s) invalid.");
    }

    public ParameterErrorException(String message) {
        super(message);
    }
}
