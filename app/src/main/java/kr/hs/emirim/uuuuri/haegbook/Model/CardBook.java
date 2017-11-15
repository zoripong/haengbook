package kr.hs.emirim.uuuuri.haegbook.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 유리 on 2017-11-06.
 */

public class CardBook implements Parcelable{
    String period;
    String location;
    String title;
    String bookCode;
    String image;
    boolean isShowing;

    public CardBook(String period, String location, String title, String image) {
        this.period = period;
        this.location = location;
        this.title = title;
        this.image = image;
    }

    public CardBook(String period, String location, String title, String bookCode, String image, boolean isShowing) {
        this.period = period;
        this.location = location;
        this.title = title;
        this.bookCode = bookCode;
        this.image = image;
        this.isShowing = isShowing;
    }

    protected CardBook(Parcel in) {
        period = in.readString();
        location = in.readString();
        title = in.readString();
        bookCode = in.readString();
        image = in.readString();
        isShowing = in.readByte() != 0;
    }

    public static final Creator<CardBook> CREATOR = new Creator<CardBook>() {
        @Override
        public CardBook createFromParcel(Parcel in) {
            return new CardBook(in);
        }

        @Override
        public CardBook[] newArray(int size) {
            return new CardBook[size];
        }
    };

    @Override
    public String toString() {
        return "CardBook{" +
                "period='" + period + '\'' +
                ", location='" + location + '\'' +
                ", title='" + title + '\'' +
                ", bookCode='" + bookCode + '\'' +
                ", image='" + image + '\'' +
                '}';
    }

    public boolean isShowing() {
        return isShowing;
    }

    public void setShowing(boolean showing) {
        isShowing = showing;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBookCode() {
        return bookCode;
    }

    public void setBookCode(String bookCode) {
        this.bookCode = bookCode;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(period);
        parcel.writeString(location);
        parcel.writeString(title);
        parcel.writeString(bookCode);
        parcel.writeString(image);
        parcel.writeByte((byte) (isShowing ? 1 : 0));
    }
}
