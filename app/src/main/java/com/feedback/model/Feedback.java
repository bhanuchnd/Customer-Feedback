
package com.feedback.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Feedback {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("customer_name")
    @Expose
    private String customerName;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("overall_rating")
    @Expose
    private int overallRating;
    @SerializedName("service_rating")
    @Expose
    private int serviceRating;
    @SerializedName("comments")
    @Expose
    private String comments;
    @SerializedName("service_on")
    @Expose
    private String serviceOn;
    @SerializedName("service_by")
    @Expose
    private String serviceBy;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getOverallRating() {
        return overallRating;
    }

    public void setOverallRating(int overallRating) {
        this.overallRating = overallRating;
    }

    public int getServiceRating() {
        return serviceRating;
    }

    public void setServiceRating(int serviceRating) {
        this.serviceRating = serviceRating;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getServiceOn() {
        return serviceOn;
    }

    public void setServiceOn(String serviceOn) {
        this.serviceOn = serviceOn;
    }

    public String getServiceBy() {
        return serviceBy;
    }

    public void setServiceBy(String serviceBy) {
        this.serviceBy = serviceBy;
    }

}
