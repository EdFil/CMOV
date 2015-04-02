package pt.ulisboa.tecnico.cmov.airdesk.core.file.exception;

/**
 * Created by edgar on 01-04-2015.
 */
public class FileAlreadyExistsException extends FileException {

    private static final String mMessage = "File '%s' already exists.";

    public FileAlreadyExistsException(String fileName) {
        super(String.format(mMessage, fileName));
    }
}
