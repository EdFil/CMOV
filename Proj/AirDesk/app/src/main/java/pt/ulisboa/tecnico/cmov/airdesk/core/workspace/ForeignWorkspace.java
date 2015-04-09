package pt.ulisboa.tecnico.cmov.airdesk.core.workspace;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import pt.ulisboa.tecnico.cmov.airdesk.core.tag.Tag;
import pt.ulisboa.tecnico.cmov.airdesk.core.user.User;

public class ForeignWorkspace extends Workspace {
    public ForeignWorkspace(long workspaceId, String name, User owner, long quota, boolean isPrivate, Collection<Tag> tags, Collection<User> users, Collection<File> files, WorkspaceManager workspaceManager) {
        super(workspaceId, name, owner, quota, isPrivate, tags, users, files, workspaceManager);
    }
}
