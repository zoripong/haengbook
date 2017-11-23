package kr.hs.emirim.uuuuri.haegbook.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 유리 on 2017-11-10.
 */

public class FirebaseImage implements Parcelable {

    private String key;
    public String imageComment;
    public String imageURI;
    public String date;
    public boolean isSelected;
    public FirebaseImage() {

    }

    public FirebaseImage(String imageComment, String imageURI, String date) {
        this.imageComment = imageComment;
        this.imageURI = imageURI;
        this.date = date;
    }

    public FirebaseImage(String key, String imageComment, String imageURI, String date) {
        this.key=key;
        this.imageComment = imageComment;
        this.imageURI = imageURI;
        this.date = date;
    }

    protected FirebaseImage(Parcel in) {
        key = in.readString();
        imageComment = in.readString();
        imageURI = in.readString();
        date = in.readString();
        isSelected = in.readByte() != 0;
    }

    public static final Creator<FirebaseImage> CREATOR = new Creator<FirebaseImage>() {
        @Override
        public FirebaseImage createFromParcel(Parcel in) {
            return new FirebaseImage(in);
        }

        @Override
        public FirebaseImage[] newArray(int size) {
            return new FirebaseImage[size];
        }
    };

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getImageComment() {
        return imageComment;
    }

    public String getImageURI() {
        return imageURI;
    }

    public String getDate() {
        return date;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(key);
        parcel.writeString(imageComment);
        parcel.writeString(imageURI);
        parcel.writeString(date);
        parcel.writeByte((byte) (isSelected ? 1 : 0));
    }
}