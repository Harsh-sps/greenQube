package com.aistream.greenqube.mvp.model;

import java.util.List;

/**
 * Created by PhuDepTraj on 6/26/2018.
 */

public class UpdateFavoriteNew {
//    @SerializedName("ids")
//    @Expose
    private List<SyncFavorites> ids = null;

    public List<SyncFavorites> getIds() {
        return ids;
    }

    public void setIds(List<SyncFavorites> ids) {
        this.ids = ids;
    }

}
