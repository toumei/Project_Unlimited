package com.example.user.text.cheaplist;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListAdapter;
import com.example.user.text.API;
import com.example.user.text.MainActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

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
public class CheapListInteractor extends AsyncTask<Void,Integer, CheapListModel[]>{

    private int DATA_COUNT=0;
    private CheapListModel[] listData;
    private CheapListPresenter presenter;
    private ListAdapter listAdapter;
    private String access_token;
    private String cheapAPI = "http://163.13.127.98:8088/api/v1.2/find_cheapest";
    private String record = "衛生紙";
    Bitmap bitmap;
    private ImageLoader imageLoader;


    public CheapListInteractor(CheapListPresenter presenter, String access_token){
        this.access_token = access_token;
        this.presenter = presenter;
        imageLoader = ImageLoader.getInstance();

    }

    public CheapListInteractor(CheapListPresenter presenter, String access_token, String query){
        this.access_token = access_token;
        this.presenter = presenter;
        this.record = query;
        imageLoader = ImageLoader.getInstance();

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        presenter.showProgressDialog();
    }

    //call api
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
                publishProgress(Integer.valueOf(i));
                //取得資料
                JSONObject data = responseJSON.getJSONObject(i);
                listData[i] = new CheapListModel();
                listData[i].picture_url = "https:" + data.getString("picture");
                listData[i].price = "$" + data.getString("price") + "元";
                listData[i].product = data.getString("name");
                listData[i].source = data.getString("source");
                listData[i].update_time = data.getString("update_time");
                listData[i].item_url = "https:" + data.getString("url");


                //url轉換圖片
                bitmap = imageLoader.loadImageSync(listData[i].picture_url);
                listData[i].picture=bitmap;


                /*URL url = new URL(listData[i].picture_url);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream in = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(in);
                listData[i].picture=bitmap;*/
            }


            }catch (Exception e){
                e.printStackTrace();

            }
            return listData;
        }

    @Override
    protected void onProgressUpdate(Integer... values) {
        //super.onProgressUpdate(values);
        if(values[0]!=null)
            presenter.setProgressDialogMessage(values[0].intValue(), DATA_COUNT);
    }

    @Override
    protected void onPostExecute(CheapListModel[] cheapListModels) {
        super.onPostExecute(cheapListModels);
        Log.d("check","interactor finish");
        //Log.d("check", listData[1].picture_url);
        //傳入資料並建立Adapter
        listAdapter = new CheapListAdapter(DATA_COUNT, listData);


        presenter.dismissProgressDialog();

        //Set List View
        presenter.updateAdapter(listAdapter);

    }

}
