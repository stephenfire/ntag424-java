package net.bplearning.ntag424.exception;

public class FileNotFoundException extends Cla90Exception {
    public FileNotFoundException() {
        super("FILE_NOT_FOUND: Specified file number does not exist.");
    }

    public FileNotFoundException(String message) {
        super(message);
    }
}
