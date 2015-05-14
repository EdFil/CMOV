package pt.ulisboa.tecnico.cmov.airdesk.core.file;

import java.io.File;

import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.Workspace;
import pt.ulisboa.tecnico.cmov.airdesk.manager.FileManager;

/**
 * Created by Diogo on 10-May-15.
 */
public class LocalFile extends MyFile {

    public LocalFile(Workspace workspace, String name, int version) {
        super(workspace, name, version);
    }

    @Override
    public File getFile() {
        return FileManager.getInstance().createLocalFile(getWorkspace().getWorkspaceFolderName(), getName());
    }

}