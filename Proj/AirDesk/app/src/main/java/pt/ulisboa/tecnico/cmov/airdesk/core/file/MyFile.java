package pt.ulisboa.tecnico.cmov.airdesk.core.file;

/**
 * Created by edgar on 11-05-2015.
 */
public class MyFile {

    int mVersion;
    private boolean isLocked;
    private java.io.File mFile;

    public MyFile(java.io.File file) {
        mVersion = 0;
        mFile = file;
        isLocked = false;
    }

    public boolean isLocked() { return isLocked; }
    public java.io.File getFile() { return mFile; }


    public synchronized boolean open() {
        if(isLocked)
            return false;
        isLocked = true;
        return true;
    }

    public synchronized boolean close() {
        if(!isLocked)
            return false;
        isLocked = false;
        return true;
    }

}
