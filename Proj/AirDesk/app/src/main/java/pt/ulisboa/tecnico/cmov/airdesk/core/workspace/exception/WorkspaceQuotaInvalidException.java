package pt.ulisboa.tecnico.cmov.airdesk.core.workspace.exception;

import android.content.Context;
import android.text.format.Formatter;

import pt.ulisboa.tecnico.cmov.airdesk.util.FileManager;

/**
 * Created by edgar on 30-03-2015.
 */
public class WorkspaceQuotaInvalidException extends WorkspaceException {

    private static final String mMessage = "Workspace quota is invalid, must be bigger than '0' and smaller than '%s'";

    public WorkspaceQuotaInvalidException() {
        super("Invalid quota");
    }

    public WorkspaceQuotaInvalidException(Context context, long maxQuota) {
        super(String.format(mMessage, Formatter.formatFileSize(context, maxQuota)));
    }

}
