package pt.ulisboa.tecnico.cmov.airdesk.core.workspace;

import android.content.Context;

import java.util.HashSet;

import pt.ulisboa.tecnico.cmov.airdesk.core.MyFile;
import pt.ulisboa.tecnico.cmov.airdesk.core.user.User;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.exception.WorkspaceAlreadyExistsException;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.exception.WorkspaceNameIsEmptyException;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.exception.WorkspacePublicNoTagsException;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.exception.WorkspaceQuotaInvalidException;
import pt.ulisboa.tecnico.cmov.airdesk.database.AirDeskDbHelper;
import pt.ulisboa.tecnico.cmov.airdesk.util.FileManager;

public class Workspace {

    private String mName;
    private User mOwner;
    private int mQuota;
    private boolean mIsPrivate;
    private HashSet<String> mTags;
    private HashSet<MyFile> mFiles;
    private HashSet<User> mUser;

    public Workspace(Context context, String name, int quota, boolean isPrivate, String[] tags){
        if(name.isEmpty()) {
            throw new WorkspaceNameIsEmptyException();
        }
        if(!FileManager.isWorkspaceNameAvailable(context, name))
            throw new WorkspaceAlreadyExistsException();
        long maxSize = FileManager.getAvailableSpace(context);
        if(quota <= 0 || quota * 1024 * 1024 >= maxSize) {
            throw new WorkspaceQuotaInvalidException(context, maxSize);
        }
        if(!isPrivate)
            if(tags == null || tags.length == 0)
                throw new WorkspacePublicNoTagsException();
        mName = name;
        mQuota = quota;
        mIsPrivate = isPrivate;
        mTags = new HashSet<String>();
        mFiles = new HashSet<MyFile>();
        mUser = new HashSet<User>();
    }

    public Workspace(String workspaceName) {
        mName = workspaceName;
    }

    public String getName() { return mName; }
    public int getQuota() { return mQuota; }
    public boolean isPrivate() { return mIsPrivate; }
    public String[] getTags() {
        String[] myArray = new String[mTags.size()];
        return mTags.toArray(myArray);
    }


    public void setName(String name) { mName = name; }
    public void setQuota(int quota) { mQuota = quota; }
    public void setIsPrivate(boolean isPrivate) { mIsPrivate = isPrivate; }
    public void setTags(String... tags) {
        if(tags != null){
            for(int i = 0; i < tags.length; i++){
                ;
            }
        }
    }
    public void addTag(String tag) { mTags.add(tag); }
    public void removeTag(String tag) { mTags.remove(tag); }


}
