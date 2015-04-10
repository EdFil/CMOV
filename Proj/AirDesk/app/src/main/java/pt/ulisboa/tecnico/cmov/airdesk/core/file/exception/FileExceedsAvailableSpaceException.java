package pt.ulisboa.tecnico.cmov.airdesk.core.file.exception;

/**
 * Created by edgar on 01-04-2015.
 */
public class FileExceedsAvailableSpaceException extends FileException {

    private static final String mMessage = "Not enough storage on disk %s/%s";

    public FileExceedsAvailableSpaceException(long bytesToUse, long usableSpace) {
        super(String.format(mMessage, bytesToUse, usableSpace));
    }
}
