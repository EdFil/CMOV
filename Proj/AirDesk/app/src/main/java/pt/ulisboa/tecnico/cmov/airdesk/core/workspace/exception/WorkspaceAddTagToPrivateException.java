package pt.ulisboa.tecnico.cmov.airdesk.core.workspace.exception;

/**
 * Created by edgar on 31-03-2015.
 */
public class WorkspaceAddTagToPrivateException extends WorkspaceException {

    private static final String mMessage = "Cannot add tags to a private workspace";

    public WorkspaceAddTagToPrivateException(){
        super(mMessage);
    }

}
