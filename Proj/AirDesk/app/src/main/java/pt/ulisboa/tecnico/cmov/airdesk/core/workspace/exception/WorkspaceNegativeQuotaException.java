package pt.ulisboa.tecnico.cmov.airdesk.core.workspace.exception;

/**
 * Created by edgar on 31-03-2015.
 */
public class WorkspaceNegativeQuotaException extends WorkspaceException {

    private static final String mMessage = "Quota cannot be empty.";

    public WorkspaceNegativeQuotaException(){
        super(mMessage);
    }

}
