package pt.ulisboa.tecnico.cmov.airdesk.core.file.exception;

/**
 * Created by edgar on 01-04-2015.
 */
public class FileExceedsMaxQuotaException extends FileException {

    private static final String mMessage = "File size exceeds max quota %s/%s";

    public FileExceedsMaxQuotaException(long bytesToUse, long usableSpace) {
        super(String.format(mMessage, bytesToUse, usableSpace));
    }
}
