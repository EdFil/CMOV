package pt.ulisboa.tecnico.cmov.airdesk.core.file;


import android.os.AsyncTask;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import pt.ulisboa.tecnico.cmov.airdesk.R;
import pt.ulisboa.tecnico.cmov.airdesk.core.file.exception.FileException;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.ForeignWorkspace;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.Workspace;
import pt.ulisboa.tecnico.cmov.airdesk.manager.FileManager;
import pt.ulisboa.tecnico.cmov.airdesk.manager.WorkspaceManager;
import pt.ulisboa.tecnico.cmov.airdesk.service.LockFileService;
import pt.ulisboa.tecnico.cmov.airdesk.service.ReadFileService;
import pt.ulisboa.tecnico.cmov.airdesk.service.UnlockFileService;
import pt.ulisboa.tecnico.cmov.airdesk.service.WriteFileService;
import pt.ulisboa.tecnico.cmov.airdesk.tasks.RequestTask;
import pt.ulisboa.tecnico.cmov.airdesk.util.Constants;

/**
 * Created by Diogo on 10-May-15.
 */
public class RemoteFile extends MyFile {

    public RemoteFile(Workspace workspace, String name) {
        super(workspace, name, 0);
        setFile(FileManager.getInstance().createTempFile(getWorkspace().getWorkspaceFolderName(), getName()));
    }

    public RemoteFile(ForeignWorkspace workspace, JSONObject jsonObject) throws JSONException {
        super(
                workspace,
                jsonObject.getString(MyFile.NAME_KEY),
                jsonObject.getInt(MyFile.VERSION_KEY
                ));
        setFile(FileManager.getInstance().createTempFile(getWorkspace().getWorkspaceFolderName(), getName()));
    }

    @Override
    public void read(TextView textView) {
        String ip = getWorkspace().getOwner().getDevice().getVirtIp();
        int port = Integer.parseInt(textView.getContext().getString(R.string.port));
        RequestTask task = new RequestTask(ip, port, ReadFileService.class, getWorkspace().getName(), getName());
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        try {
            textView.setText(task.get().getString(MyFile.CONTENT_KEY));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void write(EditText editText) {
        String ip = getWorkspace().getOwner().getDevice().getVirtIp();
        int port = Integer.parseInt(editText.getContext().getString(R.string.port));
        RequestTask task = new RequestTask(ip, port, WriteFileService.class, getWorkspace().getName(), getName(), editText.getText().toString());
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        try {
            JSONObject response = task.get();

            // Check if has error
            if (response.has(Constants.ERROR_KEY)) {
                throw new FileException(response.getString(Constants.ERROR_KEY));
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void lock() throws FileException {
        String ip = getWorkspace().getOwner().getDevice().getVirtIp();
        int port = Integer.parseInt(WorkspaceManager.getInstance().getContext().getString(R.string.port));
        RequestTask task = new RequestTask(ip, port, LockFileService.class, getWorkspace().getName(), getName());
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        try {
            JSONObject response = task.get();

            // Check if has error
            if (response.has(Constants.ERROR_KEY)) {
                throw new FileException(response.getString(Constants.ERROR_KEY));
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unlock() {
        String ip = getWorkspace().getOwner().getDevice().getVirtIp();
        int port = Integer.parseInt(WorkspaceManager.getInstance().getContext().getString(R.string.port));
        RequestTask task = new RequestTask(ip, port, UnlockFileService.class, getWorkspace().getName(), getName());
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
