package pt.ulisboa.tecnico.cmov.airdesk.manager;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;
import java.io.IOException;

import pt.ulisboa.tecnico.cmov.airdesk.tasks.CreateFolderTask;
import pt.ulisboa.tecnico.cmov.airdesk.tasks.DeleteFolderTask;

/**
 * Created by edgar on 22-03-2015.
 */
public class FileManager {

    private static final String TAG = FileManager.class.getSimpleName();

    // ------------------------
    //     Singleton Stuff
    // ------------------------

    private static FileManager mInstance;


    public static synchronized FileManager getInstance() {
        return mInstance;
    }

    public static synchronized void initFileManager(Context context) {
        mInstance = new FileManager(context);
    }

    // ------------------------
    //      Manager Stuff
    // ------------------------

    // Name used to internal storage folder for local workspaces
    private String mWorkspaceFolderName = "workspace_name_placeholder";
    private Context mContext;

    private java.io.File mRootFolder;
    private java.io.File mCacheFolder;

    public FileManager(Context context) {
        mContext = context;
        mRootFolder = null;
        mCacheFolder = mContext.getCacheDir();
    }

    public void setWorkspacesFolderName(String workspacesFolderName) {
        mWorkspaceFolderName = workspacesFolderName;
        mRootFolder = mContext.getDir(mWorkspaceFolderName, Context.MODE_PRIVATE);
    }

    public File createLocalFile(String folderName, String fileName) {
        return createFile(mRootFolder, folderName, fileName);
    }

    public File createTempFile(String folderName, String fileName) {
        return createFile(mCacheFolder, folderName, fileName);
    }

    public void deleteLocalFile(String folderName, String fileName) {
        deleteFile(mRootFolder, folderName, fileName);
    }

    public void deleteTempFile(String folderName, String fileName) {
        deleteFile(mCacheFolder, folderName, fileName);
    }

    public File createLocalFolder(String folderName) {
        return createFolder(mRootFolder, folderName);
    }

    public File createTempFolder(String folderName) {
        return createFolder(mCacheFolder, folderName);
    }

    public void deleteLocalFolder(String folderName) {
        delete(new File(mRootFolder, folderName));
        new DeleteFolderTask().execute(mRootFolder.getName(), folderName);
    }

    public void deleteTempFolder(String folderName) {
        delete(new File(mCacheFolder, folderName));
    }

    public void deleteRootFolder() {
        delete(mRootFolder);
        delete(mCacheFolder);
    }

    public long getAvailableSpace(){
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    // -------------------------
    // -------------------------
    // -------------------------

    private File createFile(File rootFolder, String folderName, String fileName) {
        File createdFile = null;
        try {
            File workspaceFolder = new File(rootFolder, folderName);
            workspaceFolder.mkdir();
            createdFile = new File(workspaceFolder, fileName);
            if(!createdFile.exists())
                createdFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return createdFile;
        }
    }

    private void deleteFile(File rootFolder, String folderName, String fileName){
        File workspaceFolder = new File(rootFolder, folderName);
        File file = new File(workspaceFolder, fileName);
        delete(file);
    }

    private File createFolder(File rootFolder, String folderName){
        File newFolder = new File(rootFolder, folderName);
        newFolder.mkdir();
        // create folder in Dropbox
        new CreateFolderTask().execute(rootFolder.getName(), folderName);
        return newFolder;
    }

    private boolean delete(File file) {
        if (file != null && file.isDirectory()) {
            String[] children = file.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = delete(new File(file, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return file.delete();
    }

}
