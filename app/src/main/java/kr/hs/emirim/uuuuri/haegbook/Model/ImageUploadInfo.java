package kr.hs.emirim.uuuuri.haegbook.Model;

/**
 * Created by 유리 on 2017-11-10.
 */

public class ImageUploadInfo {

    public String imageComent;
    public String imageURL;
    public String date;

    public ImageUploadInfo() {

    }

    public ImageUploadInfo(String imageComent, String imageURL, String date) {
        this.imageComent = imageComent;
        this.imageURL = imageURL;
        this.date = date;
    }

    public String getImageComent() {
        return imageComent;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getDate() {
        return date;
    }
}
