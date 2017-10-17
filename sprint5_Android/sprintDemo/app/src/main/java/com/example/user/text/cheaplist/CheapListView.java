package com.example.user.text.cheaplist;

import android.app.ProgressDialog;
import android.widget.ListAdapter;

/**
 * Created by toy9986619 on 2017/10/7.
 */

public interface CheapListView {

    void setAdapter(ListAdapter listAdapter);
    ProgressDialog initProgressDialog();
    void showProgressDialog();
    void setProgressDialogMessage(int now, int DATA_COUNT);
    void dismissProgressDialog();
}
