package pt.ulisboa.tecnico.cmov.airdesk.core.workspace.exception;

/**
 * Created by edgar on 31-03-2015.
 */
public class WorkspaceRemoveOwnerException extends WorkspaceException {

    private static final String mMessage = "Cannot remove owner from users.";

    public WorkspaceRemoveOwnerException(){
        super(mMessage);
    }

}
