package pt.ulisboa.tecnico.cmov.airdesk.core.workspace.exception;

/**
 * Created by edgar on 31-03-2015.
 */
public class WorkspacePublicNoTagsException extends WorkspaceException {

    private static final String mMessage = "Public workspaces needs 1 tag at least.";

    public WorkspacePublicNoTagsException() {
        super(mMessage);
    }

}
