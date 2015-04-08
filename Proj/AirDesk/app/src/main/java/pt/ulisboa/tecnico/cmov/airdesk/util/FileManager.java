package pt.ulisboa.tecnico.cmov.airdesk.util;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by edgar on 22-03-2015.
 */
public class FileManager {

    private static final String TAG = FileManager.class.getSimpleName();
    // Name used to internal storage folder for local workspaces
    public static String WORKSPACES_FOLDER_NAME = "default_workspace";

    public static boolean isWorkspaceNameAvailable(Context context, String workspaceName) {
        File rootFolder = context.getDir(WORKSPACES_FOLDER_NAME, Context.MODE_PRIVATE);
        File newFolder = new File(rootFolder, workspaceName); //Getting a folder within the dir.
        return !newFolder.exists();
    }


    public static boolean createFolder(Context context, String folderName){
        File rootFolder = context.getDir(WORKSPACES_FOLDER_NAME, Context.MODE_PRIVATE);
        File newFolder = new File(rootFolder, folderName); //Getting a folder within the dir.
        if(!newFolder.exists()) {
            return newFolder.mkdir();
        }
        return false;
    }

    public static void deleteFolder(Context context, String folderName) {
        File rootFolder = context.getDir(WORKSPACES_FOLDER_NAME, Context.MODE_PRIVATE);
        deleteDir(new File(rootFolder.toString(), folderName));
    }

    public static File fileNameToFile(Context context, String workspaceName, String fileName) {
        File rootFolder = context.getDir(WORKSPACES_FOLDER_NAME, Context.MODE_PRIVATE);
        return new File(rootFolder.toString() + "/" + workspaceName, fileName);
    }

    public static File createFile(Context context, String folderName, String fileName){
        File rootFolder = context.getDir(WORKSPACES_FOLDER_NAME, Context.MODE_PRIVATE);
        File newFile = new File(rootFolder.toString() + "/" + folderName, fileName); //Getting a file within the dir.
        try {
            newFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newFile;
    }

    public static void deleteFile(Context context, String folderName, String fileName){
        File rootFolder = context.getDir(WORKSPACES_FOLDER_NAME, Context.MODE_PRIVATE);
        File file = new File(rootFolder.toString() + "/" + folderName, fileName); //Getting a file within the dir.
        if(file.exists())
            file.delete();
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    public static long getAvailableSpace(Context context){
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }
}
