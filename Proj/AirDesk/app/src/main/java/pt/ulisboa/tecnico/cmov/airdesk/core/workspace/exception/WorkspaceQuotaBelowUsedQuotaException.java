package pt.ulisboa.tecnico.cmov.airdesk.core.workspace.exception;

/**
 * Created by edgar on 30-03-2015.
 */
public class WorkspaceQuotaBelowUsedQuotaException extends WorkspaceException {

    private static final String mMessage = "Workspace quota cannot be below %s.";

    public WorkspaceQuotaBelowUsedQuotaException(long value) {
        super(String.format(mMessage, value));
    }

}
