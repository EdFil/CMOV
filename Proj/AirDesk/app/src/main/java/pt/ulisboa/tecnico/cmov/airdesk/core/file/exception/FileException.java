package pt.ulisboa.tecnico.cmov.airdesk.core.file.exception;

/**
 * Created by edgar on 01-04-2015.
 */
public class FileException extends RuntimeException {

    private static final String mMessage = "File error";

    FileException() {
        super(mMessage);
    }

    public FileException(String message) {
        super(message);
    }


}
