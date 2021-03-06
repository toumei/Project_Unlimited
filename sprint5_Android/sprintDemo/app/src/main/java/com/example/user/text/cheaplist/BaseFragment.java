package com.example.user.text.cheaplist;

import android.support.v4.app.Fragment;

/**
 * Created by toy9986619 on 2017/10/18.
 */

public abstract class BaseFragment extends Fragment{
    //是否可被看見
    protected boolean isVisible;
    //標誌fragment是否初始完成
    protected boolean isPrepared = false;

    /**
     * 实现Fragment数据的缓加载
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInVisible();
        }
    }
    protected void onInVisible() {
    }
    protected void onVisible() {
        //載入
        loadData();
    }
    protected abstract void loadData();

}
