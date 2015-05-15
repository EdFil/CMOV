package pt.ulisboa.tecnico.cmov.airdesk.core.file;

import android.widget.EditText;
import android.widget.TextView;

import pt.ulisboa.tecnico.cmov.airdesk.core.file.exception.FileExceedsAvailableSpaceException;
import pt.ulisboa.tecnico.cmov.airdesk.core.file.exception.FileExceedsMaxQuotaException;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.LocalWorkspace;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.Workspace;
import pt.ulisboa.tecnico.cmov.airdesk.manager.FileManager;
import pt.ulisboa.tecnico.cmov.airdesk.manager.WorkspaceManager;
import pt.ulisboa.tecnico.cmov.airdesk.tasks.ReadFileTask;
import pt.ulisboa.tecnico.cmov.airdesk.tasks.WriteFileTask;

/**
 * Created by Diogo on 10-May-15.
 */
public class LocalFile extends MyFile {

    public LocalFile(Workspace workspace, String name, int version) {
        super(workspace, name, version);
        setFile(FileManager.getInstance().createLocalFile(getWorkspace().getWorkspaceFolderName(), getName()));
    }

    @Override
    public void read(TextView textView) {
        new ReadFileTask(textView).execute(getFile());
    }

    @Override
    public void write(EditText editText, Workspace workspace) {
        long bytesToUse = editText.length() - editText.length() + ((LocalWorkspace)workspace).getUsedQuota();
        long usableSpace = WorkspaceManager.getInstance().getSpaceAvailableInternalStorage();

        if(bytesToUse > usableSpace) {
            throw new FileExceedsAvailableSpaceException(bytesToUse, usableSpace);
        }
        if(bytesToUse > workspace.getMaxQuota()) {
            throw new FileExceedsMaxQuotaException(bytesToUse, workspace.getMaxQuota());
        }

        new WriteFileTask(editText, workspace).execute(getFile());
    }

}