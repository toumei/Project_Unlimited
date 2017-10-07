package com.example.user.text.cheaplist;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ListAdapter;
import com.example.user.text.API;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by toy9986619 on 2017/10/7.
 */

//api task
//AsyncTask<傳入值型態, 更新進度型態, 結果型態>
public class CheapListInteractor extends AsyncTask<Void,Void, CheapListModel[]>{

    private int DATA_COUNT=0;
    private CheapListModel[] listData;
    private CheapListPresenter presenter;
    private ListAdapter listAdapter;
    private String access_token;
    private String cheapAPI = "http://163.13.127.98:8088/api/v1.1/find_cheapest";
    private String record = "衛生紙";
    Bitmap bitmap;


    public CheapListInteractor(CheapListPresenter presenter, String access_token){
        this.access_token = access_token;
        this.presenter = presenter;

    }

    @Override
    protected CheapListModel[] doInBackground(Void... params) {
        try{

            // call api
            ArrayList<NameValuePair> sendlist = new ArrayList<NameValuePair>();
            sendlist.add(new BasicNameValuePair("search",record));
            JSONArray responseJSON = new JSONArray(API.CallAPI("GET",cheapAPI,sendlist,access_token));
            DATA_COUNT=responseJSON.length();

            // json資料處理
            listData = new CheapListModel[DATA_COUNT];
            for(int i=0; i<DATA_COUNT; i++){
                //取得資料
                JSONObject data = responseJSON.getJSONObject(i);
                listData[i] = new CheapListModel();
                listData[i].picture_url = "https:" + data.getString("picture");
                listData[i].price = "$" + data.getString("price") + "元";
                listData[i].product = data.getString("product");
                listData[i].source = data.getString("source");
                listData[i].update_time = data.getString("update_time");
                listData[i].item_url = "https:" + data.getString("url");

                //url轉換圖片
                URL url = new URL(listData[i].picture_url);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream in = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(in);
                listData[i].picture=bitmap;
            }


            }catch (Exception e){
                e.printStackTrace();


            }
            return listData;
        }

    @Override
    protected void onPostExecute(CheapListModel[] cheapListModels) {
        super.onPostExecute(cheapListModels);

        //Set List View
        listAdapter = new CheapListAdapter(DATA_COUNT, listData);
        presenter.updateAdapter(listAdapter);
    }

}
