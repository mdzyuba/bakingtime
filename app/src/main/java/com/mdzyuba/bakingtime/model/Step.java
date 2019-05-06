package com.mdzyuba.bakingtime.model;

import androidx.annotation.Nullable;

public class Step {
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
}
