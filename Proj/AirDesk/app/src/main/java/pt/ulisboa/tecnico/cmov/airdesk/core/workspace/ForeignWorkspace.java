package pt.ulisboa.tecnico.cmov.airdesk.core.workspace;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.core.file.MyFile;
import pt.ulisboa.tecnico.cmov.airdesk.core.user.User;

public class ForeignWorkspace extends Workspace {

    public ForeignWorkspace(String name, User owner, long quota, boolean isPrivate, Collection<String> tags, Collection<User> users){
        this(-1, name, owner, quota, isPrivate, tags, users, Collections.EMPTY_LIST);
    }

    public ForeignWorkspace(long workspaceId, String name, User owner, long quota, boolean isPrivate, Collection<String> tags, Collection<User> users, Collection<MyFile> files) {
        super(workspaceId, name, owner, quota, isPrivate, tags, users, files);
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
