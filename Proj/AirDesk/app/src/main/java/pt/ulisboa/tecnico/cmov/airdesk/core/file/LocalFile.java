package pt.ulisboa.tecnico.cmov.airdesk.core.file;

import android.util.Log;
import android.widget.TextView;

import pt.ulisboa.tecnico.cmov.airdesk.core.file.exception.FileAlreadyBeeingEditedException;
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
    public void write(String content) {
        Log.d("LocalFile", String.valueOf(getFile().length()));
        long bytesToUse = content.length() - getFile().length() + ((LocalWorkspace)getWorkspace()).getUsedQuota();
        long usableSpace = WorkspaceManager.getInstance().getSpaceAvailableInternalStorage();

        if(bytesToUse > usableSpace) {
            throw new FileExceedsAvailableSpaceException(bytesToUse, usableSpace);
        }
        if(bytesToUse > getWorkspace().getMaxQuota()) {
            throw new FileExceedsMaxQuotaException(bytesToUse, getWorkspace().getMaxQuota());
        }

        new WriteFileTask(content).execute(getFile());
    }

    @Override
    public void lock() {
        if(isLocked())
            throw new FileAlreadyBeeingEditedException(getName());
        setLock(true);

    }

    @Override
    public void unlock() {
        setLock(false);
    }

}