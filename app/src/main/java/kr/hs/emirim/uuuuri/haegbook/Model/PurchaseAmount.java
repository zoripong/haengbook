package kr.hs.emirim.uuuuri.haegbook.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by doori on 2017-11-19.
 */

public class PurchaseAmount implements Parcelable {
    int purchaseType; //1 현재 쓴 2 남은 3 예정
    String typeName;
    String koreaAmount;
    String foreignAmount;

    public PurchaseAmount(int purchaseType, String typeName, String foreignAmount, String koreaAmount) {
        this.purchaseType = purchaseType;
        this.typeName = typeName;
        this.koreaAmount = koreaAmount;
        this.foreignAmount = foreignAmount;
    }

    @Override
    public String toString() {
        return "PurchaseAmount{" +
                "purchaseType=" + purchaseType +
                ", typeName='" + typeName + '\'' +
                ", koreaAmount='" + koreaAmount + '\'' +
                ", foreignAmount='" + foreignAmount + '\'' +
                '}';
    }

    protected PurchaseAmount(Parcel in) {
        purchaseType = in.readInt();
        typeName = in.readString();
        koreaAmount = in.readString();
        foreignAmount = in.readString();

    }
    public static final Creator<PurchaseAmount> CREATOR = new Creator<PurchaseAmount>() {
        @Override
        public PurchaseAmount createFromParcel(Parcel in) {
            return new PurchaseAmount(in);
        }

        @Override
        public PurchaseAmount[] newArray(int size) {
            return new PurchaseAmount[size];
        }
    };
    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getKoreaAmount() {
        return koreaAmount;
    }

    public void setKoreaAmount(String koreaAmount) {
        this.koreaAmount = koreaAmount;
    }

    public String getForeignAmount() {
        return foreignAmount;
    }

    public void setForeignAmount(String foreignAmount) {
        this.foreignAmount = foreignAmount;
    }

    public int getPurchaseType() {
        return purchaseType;
    }

    public void setPurchaseType(int purchaseType) {
        this.purchaseType = purchaseType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(purchaseType);
        parcel.writeString(typeName);
        parcel.writeString(koreaAmount);
        parcel.writeString(foreignAmount);

    }
}
