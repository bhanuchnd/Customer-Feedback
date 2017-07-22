package com.feedback.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Staff {

    @SerializedName("staff")
    @Expose
    private List<Staff_> staff = null;

    public List<Staff_> getStaff() {
        return staff;
    }

    public void setStaff(List<Staff_> staff) {
        this.staff = staff;
    }

}