package kr.hs.emirim.uuuuri.haegbook.Model;

/**
 * Created by 유리 on 2017-11-06.
 */

public class CardBook {
    String period;
    String location;
    String title;
    String bookCode;
    String image;
    public CardBook(String period, String location, String title, String image) {
        this.period = period;
        this.location = location;
        this.title = title;
        this.image = image;
    }

    public CardBook(String period, String location, String title, String bookCode, String image) {
        this.period = period;
        this.location = location;
        this.title = title;
        this.bookCode = bookCode;
        this.image = image;
    }

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
}
