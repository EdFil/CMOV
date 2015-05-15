package pt.ulisboa.tecnico.cmov.airdesk.core.file;


import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import pt.ulisboa.tecnico.cmov.airdesk.R;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.ForeignWorkspace;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.Workspace;
import pt.ulisboa.tecnico.cmov.airdesk.manager.FileManager;
import pt.ulisboa.tecnico.cmov.airdesk.service.ReadFileService;
import pt.ulisboa.tecnico.cmov.airdesk.tasks.RequestTask;

/**
 * Created by Diogo on 10-May-15.
 */
public class RemoteFile extends MyFile {

    public RemoteFile(ForeignWorkspace workspace, JSONObject jsonObject) throws JSONException {
        super(
                workspace,
                jsonObject.getString(MyFile.NAME_KEY),
                jsonObject.getInt(MyFile.VERSION_KEY
                ));
        setFile(FileManager.getInstance().createTempFile(getWorkspace().getWorkspaceFolderName(), getName()));
    }

    @Override
    public void read(final TextView textView) {
        String ip = getWorkspace().getOwner().getDevice().getVirtIp();
        int port = Integer.parseInt(textView.getContext().getString(R.string.port));
        RequestTask task = new RequestTask(ip, port, ReadFileService.class, getWorkspace().getName(), getName());
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        try {
            String content = task.get().getString(MyFile.CONTENT_KEY);
            Log.d("RemoteFile", content);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void write(EditText editText, Workspace workspace) {

    }
}
