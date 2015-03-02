package pt.ulisboa.tecnico.cmov.lab2_ex4.note;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by edgar on 02-03-2015.
 */
public class Note implements Parcelable{

    public final static String NOTE_MESSAGE = Note.class.getName();

    private String mTitle;
    private String mDescription;

    public Note(String title, String description){
        mTitle = title;
        mDescription = description;
    }

    public String getTitle(){ return mTitle; }
    public String getDescription() { return mDescription; }







    //---------------------
    //Stuff from Parcelable
    //---------------------

    private Note(Parcel in){
        mTitle = in.readString();
        mDescription = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mDescription);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Note> CREATOR = new Parcelable.Creator<Note>() {
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        public Note[] newArray(int size) {
            return new Note[size];
        }
    };
}
