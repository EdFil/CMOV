package pt.ulisboa.tecnico.cmov.airdesk.core.workspace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import pt.ulisboa.tecnico.cmov.airdesk.core.file.RemoteFile;
import pt.ulisboa.tecnico.cmov.airdesk.core.user.User;
import pt.ulisboa.tecnico.cmov.airdesk.manager.WorkspaceManager;

public class ForeignWorkspace extends Workspace {

    private List<RemoteFile> mFiles;

    public ForeignWorkspace(String name, User owner, long quota, boolean isPrivate){
        super(-1, name, owner, quota, isPrivate, Collections.EMPTY_LIST, Collections.EMPTY_LIST);
        long uuid;
        do {
            uuid = new Random().nextLong();
        } while (WorkspaceManager.getInstance().getForeignWorkspaceWithId(uuid) != null);
        setFiles(Collections.EMPTY_LIST);
        setDatabaseId(uuid);
    }


    public void addFile(RemoteFile file) {
        mFiles.add(file);
    }

    public void removeFile(RemoteFile file) {
        mFiles.remove(file);
    }

    public RemoteFile getFileByName(String name) {
        for(RemoteFile file : mFiles)
            if(file.getFile().getName().equals(name))
                return file;
        return null;
    }

    public List<RemoteFile> getFiles() {
        return mFiles;
    }

    public void setFiles(Collection<RemoteFile> files) {
        mFiles = new ArrayList<>(files);
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
