package pt.ulisboa.tecnico.cmov.airdesk.core.workspace;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.core.tag.Tag;
import pt.ulisboa.tecnico.cmov.airdesk.core.user.User;

public class ForeignWorkspace extends Workspace {
    public ForeignWorkspace(long workspaceId, String name, User owner, long quota, boolean isPrivate, Collection<Tag> tags, Collection<User> users, Collection<File> files, WorkspaceManager workspaceManager) {
        super(workspaceId, name, owner, quota, isPrivate, tags, users, files, workspaceManager);
    }

    public int countWorkspaceWithName(List<ForeignWorkspace> foreignWorkspaces, String workspaceName) {
        int count = 0;
        for(ForeignWorkspace foreignWorkspace : foreignWorkspaces)
            if(foreignWorkspace.getName().equals(workspaceName))
                count++;
        return count;
    }

    // TODO : CONSTTRUCTORS - example: when searching and adding a foreign workspace
}
