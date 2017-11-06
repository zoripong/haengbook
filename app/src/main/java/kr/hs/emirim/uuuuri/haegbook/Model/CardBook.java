package kr.hs.emirim.uuuuri.haegbook.Model;

/**
 * Created by 유리 on 2017-11-06.
 */

public class CardBook {
    String period;
    String location;
    String title;
    String bookCode;
    String url;

    public CardBook(String period, String location, String title, String bookCode, String url) {
        this.period = period;
        this.location = location;
        this.title = title;
        this.bookCode = bookCode;
        this.url = url;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
