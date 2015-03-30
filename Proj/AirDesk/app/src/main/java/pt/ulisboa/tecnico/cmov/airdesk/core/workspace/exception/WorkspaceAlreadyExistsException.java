package pt.ulisboa.tecnico.cmov.airdesk.core.workspace.exception;

/**
 * Created by edgar on 30-03-2015.
 */
public class WorkspaceAlreadyExistsException extends WorkspaceException {

    private static final String mMessage = "Workspace name already exists";

    public WorkspaceAlreadyExistsException() {
        super(mMessage);
    }

}
