package pt.ulisboa.tecnico.cmov.airdesk.core.file.exception;

/**
 * Created by edgar on 01-04-2015.
 */
public class FileAlreadyBeeingEditedException extends FileException {

    private static final String mMessage = "File '%s' is already beeing edited.";

    public FileAlreadyBeeingEditedException(String fileName) {
        super(String.format(mMessage, fileName));
    }
}
