package com.aistream.greenqube.mvp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by PhuDepTraj on 5/18/2018.
 */

public class Favorite {
    @SerializedName("ids")
    @Expose
    private List<Integer> ids = null;

    public List<Integer> getIds() {
        return ids;
    }

    public void setIds(List<Integer> ids) {
        this.ids = ids;
    }
}
