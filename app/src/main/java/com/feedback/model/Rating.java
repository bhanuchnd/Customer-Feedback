package com.feedback.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Rating {

    @SerializedName("rating")
    @Expose
    private List<Rating_> rating = null;

    public List<Rating_> getRating() {
        return rating;
    }

    public void setRating(List<Rating_> rating) {
        this.rating = rating;
    }

}