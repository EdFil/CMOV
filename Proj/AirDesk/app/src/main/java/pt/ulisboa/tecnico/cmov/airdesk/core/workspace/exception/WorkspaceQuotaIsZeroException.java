package pt.ulisboa.tecnico.cmov.airdesk.core.workspace.exception;

/**
 * Created by edgar on 31-03-2015.
 */
public class WorkspaceQuotaIsZeroException extends WorkspaceException {

    private static final String mMessage = "Quota cannot be 0.";

    public WorkspaceQuotaIsZeroException(){
        super(mMessage);
    }

}
