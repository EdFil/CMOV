package pt.ulisboa.tecnico.cmov.lab2_ex4.note;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by edgar on 02-03-2015.
 */
public class Note implements Parcelable{

    public final static String NOTE_MESSAGE = Note.class.getName();

    private String mTitle;
    private String mDescription;
    private Bitmap mImage;

    public Note(String title, String description){
        this(title, description, null);
    }

    public Note(String title, String description, Bitmap image){
        mTitle = title;
        mDescription = description;
        mImage = image;
    }

    public String getTitle(){ return mTitle; }
    public String getDescription() { return mDescription; }
    public Bitmap getImage() { return mImage; }







    //---------------------
    //Stuff from Parcelable
    //---------------------

    private Note(Parcel in){
        mTitle = in.readString();
        mDescription = in.readString();
        mImage = in.readParcelable(Bitmap.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mDescription);
        dest.writeParcelable(mImage, 0);
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
