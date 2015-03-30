package pt.ulisboa.tecnico.cmov.airdesk.core.workspace.exception;

/**
 * Created by edgar on 30-03-2015.
 */
public class WorkspaceNameIsEmptyException extends WorkspaceException {

    private static final String mMessage = "Workspace name cannot be empty";

    public WorkspaceNameIsEmptyException() {
        super(mMessage);
    }

}
