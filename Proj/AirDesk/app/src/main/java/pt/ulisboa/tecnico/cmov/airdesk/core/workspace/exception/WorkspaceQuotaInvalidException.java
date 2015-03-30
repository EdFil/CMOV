package pt.ulisboa.tecnico.cmov.airdesk.core.workspace.exception;

/**
 * Created by edgar on 30-03-2015.
 */
public class WorkspaceQuotaInvalidException extends WorkspaceException {

    private static final String mMessage = "Workspace quota is invalid, must be bigger than '0' and smaller than '%s'";

    public WorkspaceQuotaInvalidException() {
        super("Invalid quota");
    }

    public WorkspaceQuotaInvalidException(int maxQuota) {
        super(String.format(mMessage, maxQuota));
    }

}
