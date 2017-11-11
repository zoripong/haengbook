package kr.hs.emirim.uuuuri.haegbook.Model;

/**
 * Created by 유리 on 2017-11-10.
 */

public class FirebaseImage {

    public String imageComment;
    public String imageURL;
    public String date;

    public FirebaseImage() {

    }

    public FirebaseImage(String imageComment, String imageURL, String date) {
        this.imageComment = imageComment;
        this.imageURL = imageURL;
        this.date = date;
    }

    public String getImageComment() {
        return imageComment;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getDate() {
        return date;
    }
}
