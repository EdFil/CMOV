package pt.ulisboa.tecnico.cmov.airdesk.core.file;

import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.Workspace;

/**
 * Created by edgar on 11-05-2015.
 */
public abstract class MyFile {

    public static final String VERSION_KEY = "version";
    public static final String NAME_KEY = "name";
    public static final String CONTENT_KEY = "content";

    private int mVersion;
    private boolean isLocked;
    private Workspace mWorkspace;
    private String mName;
    private File mFile;

    public MyFile(Workspace workspace, String name, int version) {
        mWorkspace = workspace;
        mVersion = version;
        mName = name;
        isLocked = false;
    }

    public int getVersion() { return mVersion; }
    public boolean isLocked() { return isLocked; }
    public Workspace getWorkspace() { return mWorkspace; }
    public String getName() { return mName; }
    public File getFile() { return mFile; }

    public void setLock(boolean lock) { isLocked = lock; }
    public void setFile(File file) { mFile = file; }

    public abstract void read(TextView textView);
    public abstract void write(String content) throws JSONException;

    public abstract void lock();
    public abstract void unlock();

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(NAME_KEY, getFile().getName());
            jsonObject.put(VERSION_KEY, getVersion());
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            return jsonObject;
        }

    }
}
