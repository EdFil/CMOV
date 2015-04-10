package pt.ulisboa.tecnico.cmov.airdesk.core.workspace.exception;

/**
 * Created by edgar on 30-03-2015.
 */
public class WorkspaceException extends RuntimeException {

    private static final String mMessage = "Workspace error";

    WorkspaceException() {
        super(mMessage);
    }

    public WorkspaceException(String message) {
        super(message);
    }

}
