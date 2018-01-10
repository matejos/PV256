package cz.muni.fi.pv256.movio2.uco_422476;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Matej on 3.11.2017.
 */
public class Film implements Parcelable, ListItem {

    private Long mId;
    private long mReleaseDate;
    private String mCoverPath;
    private String mTitle;
    private String mBackdrop;
    private float mPopularity;
    private String mDescription;

    public Film(Long id, long releaseDate, String coverPath, String title, String backdrop, float popularity, String description) {
        mId = id;
        mReleaseDate = releaseDate;
        mCoverPath = coverPath;
        mTitle = title;
        mBackdrop = backdrop;
        mPopularity = popularity;
        mDescription = description;
    }

    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        mId = id;
    }

    public long getReleaseDate() {
        return mReleaseDate;
    }

    public void setReleaseDate(long releaseDate) {
        mReleaseDate = releaseDate;
    }

    public String getCoverPath() {
        return mCoverPath;
    }

    public void setCoverPath(String coverPath) {
        mCoverPath = coverPath;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getBackdrop() { return mBackdrop; }

    public void setBackdrop(String backdrop) { mBackdrop = backdrop; }

    public float getPopularity() { return mPopularity; }

    public void setPopularity(float popularity) { mPopularity = popularity; }

    public String getDescription() { return mDescription; }

    public void setDescription(String description) { mDescription = description; }

    @Override
    public int describeContents() {
        return 0;
    }

    public Film(Parcel in) {
        mId = in.readLong();
        mReleaseDate = in.readLong();
        mCoverPath = in.readString();
        mTitle = in.readString();
        mBackdrop = in.readString();
        mPopularity = in.readFloat();
        mDescription = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeLong(mReleaseDate);
        dest.writeString(mCoverPath);
        dest.writeString(mTitle);
        dest.writeString(mBackdrop);
        dest.writeFloat(mPopularity);
        dest.writeString(mDescription);
    }

    public static final Parcelable.Creator<Film> CREATOR = new Parcelable.Creator<Film>() {
        @Override
        public Film createFromParcel(Parcel source) {
            return new Film(source);
        }

        @Override
        public Film[] newArray(int size) {
            return new Film[size];
        }
    };


}
