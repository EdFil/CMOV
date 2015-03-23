package pt.ulisboa.tecnico.cmov.cmov_proj.core.workspace;

import java.util.HashSet;

import pt.ulisboa.tecnico.cmov.cmov_proj.core.MyFile;
import pt.ulisboa.tecnico.cmov.cmov_proj.core.user.User;

public class Workspace {

    private String mName;
    private User mOwner;
    private int mQuota;
    private boolean mIsPrivate;
    private HashSet<String> mTags;
    private HashSet<MyFile> mFiles;
    private HashSet<User> mUser;

    public Workspace(String name, int quota){
        this(name, quota, false);
    }

    public Workspace(String name, int quota, boolean isPrivate){
        mName = name;
        mQuota = quota;
        mIsPrivate = isPrivate;
        mTags = new HashSet<String>();
        mFiles = new HashSet<MyFile>();
        mUser = new HashSet<User>();
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
    public void setTags(HashSet<String> tags) { mTags = tags; }
    public void addTag(String tag) { mTags.add(tag); }
    public void removeTag(String tag) { mTags.remove(tag); }


}
