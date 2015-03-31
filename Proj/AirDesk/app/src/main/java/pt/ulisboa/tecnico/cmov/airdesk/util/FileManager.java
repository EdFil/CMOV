package pt.ulisboa.tecnico.cmov.airdesk.util;

import android.content.ContentProvider;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import pt.ulisboa.tecnico.cmov.airdesk.AirDeskActivity;

/**
 * Created by edgar on 22-03-2015.
 */
public class FileManager {

    private static final String TAG = FileManager.class.getSimpleName();

    public static boolean isWorkspaceNameAvailable(Context context, String workspaceName) {
        File rootFolder = context.getDir(AirDeskActivity.WORKSPACES_FOLDER_NAME, Context.MODE_PRIVATE);
        File newFolder = new File(rootFolder, workspaceName); //Getting a folder within the dir.
        return !newFolder.exists();
    }

    public static boolean createFolder(Context context, String folderName){
        File rootFolder = context.getDir(AirDeskActivity.WORKSPACES_FOLDER_NAME, Context.MODE_PRIVATE);
        File newFolder = new File(rootFolder, folderName); //Getting a folder within the dir.
        if(!newFolder.exists()) {
            return newFolder.mkdir();
        }
        return false;
    }

    public static void deleteFolder(Context context, String folderName){
        File rootFolder = context.getDir(AirDeskActivity.WORKSPACES_FOLDER_NAME, Context.MODE_PRIVATE);
        deleteDir(new File(rootFolder.toString(), folderName));
    }

    public static void createFile(Context context, String folderName, String fileName){
        File rootFolder = context.getDir(AirDeskActivity.WORKSPACES_FOLDER_NAME, Context.MODE_PRIVATE);
        File newFile = new File(rootFolder.toString() + "/" + folderName, fileName); //Getting a file within the dir.
        try {
            FileOutputStream out = new FileOutputStream(newFile); //Use the stream as usual to write into the file.

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void deleteFile(Context context, String folderName, String fileName){
        File rootFolder = context.getDir(AirDeskActivity.WORKSPACES_FOLDER_NAME, Context.MODE_PRIVATE);
        File file = new File(rootFolder.toString() + "/" + folderName, fileName); //Getting a file within the dir.
        if(file.exists())
            file.delete();
    }

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
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
