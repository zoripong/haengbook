package kr.hs.emirim.uuuuri.haegbook.Model;

import java.io.Serializable;

public class GalleryImage implements Serializable {

    private String imgPath;
    private boolean selected;

    public GalleryImage(String imgPath, boolean selected) {
        this.imgPath = imgPath;
        this.selected = selected;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public String toString() {
        return "GalleryImage{" +
                "imgPath='" + imgPath + '\'' +
                ", selected=" + selected +
                '}';
    }
}
