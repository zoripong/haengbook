package kr.hs.emirim.uuuuri.haegbook.Model;

/**
 * Created by 유리 on 2017-11-10.
 */

public class FirebaseImage {

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
}