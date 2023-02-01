package com.aistream.greenqube.customs;

/**
 * Created by Administrator on 5/11/2017.
 */

public interface ItemTouchHelperCallback {
    boolean onMove(int fromPos,int toPos);
    void onItemDelete(int pos);
}
