package in.lockerapplication.adNetworks;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Simon on 6/29/2015.
 */
public class AdUnit implements Parcelable {
    private Integer ad_type;
    private String ad_id;
    private String ad_mediator;
    public static final Integer DEFAULT = -1;
    public static final Integer BANNER = 0;
    public static final Integer INTERSTITIAL = 1;
    public static final Integer CUSTOM = 2;
    public static final Integer MEDIUM = 3;
    public static final Integer NATIVE = 4;
    public AdUnit(){
        this.ad_type = this.DEFAULT;
    }

    public AdUnit(Integer type, String ad_id, String ad_mediator){
        setAd_type(type);
        setAd_id(ad_id);
        setAd_mediator(ad_mediator);
    }

    public Integer getAd_type() {
        return ad_type;
    }

    public void setAd_type(Integer ad_type) {
        this.ad_type = ad_type;
    }

    public String getAd_id() {
        return ad_id;
    }

    public void setAd_id(String ad_id) {
        this.ad_id = ad_id;
    }

    public String getAd_mediator() {
        return ad_mediator;
    }

    public void setAd_mediator(String ad_mediator) {
        this.ad_mediator = ad_mediator;
    }

    protected AdUnit(Parcel in) {
        ad_type = in.readByte() == 0x00 ? null : in.readInt();
        ad_id = in.readString();
        ad_mediator = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (ad_type == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(ad_type);
        }
        dest.writeString(ad_id);
        dest.writeString(ad_mediator);
    }

    public static final Parcelable.Creator<AdUnit> CREATOR = new Parcelable.Creator<AdUnit>() {
        @Override
        public AdUnit createFromParcel(Parcel in) {
            return new AdUnit(in);
        }

        @Override
        public AdUnit[] newArray(int size) {
            return new AdUnit[size];
        }
    };
}
