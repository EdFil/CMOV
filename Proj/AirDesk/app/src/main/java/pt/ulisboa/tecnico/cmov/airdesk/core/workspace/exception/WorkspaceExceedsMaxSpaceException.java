package pt.ulisboa.tecnico.cmov.airdesk.core.workspace.exception;

import android.content.Context;
import android.text.format.Formatter;

/**
 * Created by edgar on 30-03-2015.
 */
public class WorkspaceExceedsMaxSpaceException extends WorkspaceException {

    private static final String mMessage = "Workspace quota must be smaller than '%s'";

    public WorkspaceExceedsMaxSpaceException(Context context, long maxQuota) {
        super(String.format(mMessage, Formatter.formatFileSize(context, maxQuota)));
    }

}
