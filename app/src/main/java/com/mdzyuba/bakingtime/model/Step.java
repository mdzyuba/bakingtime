package com.mdzyuba.bakingtime.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

public class Step implements Parcelable {
    private int id;
    @Nullable
    private String shortDescription;
    @Nullable
    private String description;
    // TODO: convert to an URL
    // optional
    @Nullable
    private String videoURL;
    // optional
    @Nullable
    private String thumbnailURL;

    public Step(int id, @Nullable String shortDescription, @Nullable String description,
                @Nullable String videoURL, @Nullable String thumbnailURL) {
        this.id = id;
        this.shortDescription = shortDescription;
        this.description = description;
        this.videoURL = videoURL;
        this.thumbnailURL = thumbnailURL;
    }

    public int getId() {
        return id;
    }

    @Nullable
    public String getShortDescription() {
        return shortDescription;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    @Nullable
    public String getVideoURL() {
        return videoURL;
    }

    @Nullable
    public String getThumbnailURL() {
        return thumbnailURL;
    }

    @Override
    public String toString() {
        return "Step{" + "id=" + id + ", shortDescription='" + shortDescription + '\'' +
               ", description='" + description + '\'' + ", videoURL='" + videoURL + '\'' +
               ", thumbnailURL='" + thumbnailURL + '\'' + '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.shortDescription);
        dest.writeString(this.description);
        dest.writeString(this.videoURL);
        dest.writeString(this.thumbnailURL);
    }

    protected Step(Parcel in) {
        this.id = in.readInt();
        this.shortDescription = in.readString();
        this.description = in.readString();
        this.videoURL = in.readString();
        this.thumbnailURL = in.readString();
    }

    public static final Creator<Step> CREATOR = new Creator<Step>() {
        @Override
        public Step createFromParcel(Parcel source) {
            return new Step(source);
        }

        @Override
        public Step[] newArray(int size) {
            return new Step[size];
        }
    };
}
