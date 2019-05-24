package com.mdzyuba.bakingtime.model;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "step",
        foreignKeys = @ForeignKey(entity = Recipe.class,
                                  parentColumns = "id",
                                  childColumns = "recipeId",
                                  onDelete = ForeignKey.CASCADE),
        indices = { @Index(value = {"recipeId", "id"}, unique = true)})
public class Step {

    @PrimaryKey(autoGenerate = true)
    private Integer pk;

    @NonNull
    private Integer id;

    @NonNull
    private Integer recipeId;

    @Nullable
    private String shortDescription;

    @Nullable
    private String description;

    @Nullable
    private String videoURL;

    @Nullable
    private String thumbnailURL;

    public Step(@NonNull Integer id, @NonNull Integer recipeId, @Nullable String shortDescription,
                @Nullable String description, @Nullable String videoURL,
                @Nullable String thumbnailURL) {
        this.id = id;
        this.recipeId = recipeId;
        this.shortDescription = shortDescription;
        this.description = description;
        this.videoURL = videoURL;
        this.thumbnailURL = thumbnailURL;
    }

    @NonNull
    public Integer getId() {
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

    @NonNull
    public Integer getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(@NonNull Integer recipeId) {
        this.recipeId = recipeId;
    }

    @NonNull
    public Integer getPk() {
        return pk;
    }

    public void setPk(@NonNull Integer pk) {
        this.pk = pk;
    }

    @Override
    public String toString() {
        return "Step{" + "id=" + id + ", shortDescription='" + shortDescription + '\'' +
               ", description='" + description + '\'' + ", videoURL='" + videoURL + '\'' +
               ", thumbnailURL='" + thumbnailURL + '\'' + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Step)) return false;
        Step step = (Step) o;
        return Objects.equals(pk, step.pk) && id.equals(step.id) &&
               recipeId.equals(step.recipeId) &&
               Objects.equals(shortDescription, step.shortDescription) &&
               Objects.equals(description, step.description) &&
               Objects.equals(videoURL, step.videoURL) &&
               Objects.equals(thumbnailURL, step.thumbnailURL);
    }

    @Override
    public int hashCode() {
        return Objects
                .hash(pk, id, recipeId, shortDescription, description, videoURL, thumbnailURL);
    }
}
