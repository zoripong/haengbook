package kr.hs.emirim.uuuuri.haegbook.Model;

/**
 * Created by 유리 on 2017-11-10.
 */

public class FirebaseImage {

    public String imageComment;
    public String imageURI;
    public String date;

    public FirebaseImage() {

    }

    public FirebaseImage(String imageComment, String imageURI, String date) {
        this.imageComment = imageComment;
        this.imageURI = imageURI;
        this.date = date;
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
}
