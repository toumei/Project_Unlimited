package com.example.user.text;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by user on 2017/8/2.;
 * Created by user on 2017/8/2.;
 * Created by user on 2017/8/2.;
 */

public class CheapListFragment extends Fragment {
    private ListView listView;
    private SearchView searchView;

    public static final String ACCESS_TOKEN = "access_token";
    private String access_token;
    private String cheapAPI = "http://163.13.127.98:8088/api/v1.1/find_cheapest";
    private String record = "衛生紙";
    private int DATA_COUNT=0;
    private ListData[] listData;
    private View view;

    public static CheapListFragment newInstance(String access_token)
    {
        Bundle bundle = new Bundle();
        bundle.putString(ACCESS_TOKEN, access_token);
        CheapListFragment cheapListFragment = new CheapListFragment();
        cheapListFragment.setArguments(bundle);
        return cheapListFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get access token
        Bundle bundle = getArguments();
        Log.d("CheapAccessTest", bundle.toString());
        if (bundle != null)
            access_token = bundle.getString(ACCESS_TOKEN);
        else{
            Log.d("test", "fail");
        }

        Log.d("access_token", access_token);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstenceState){

        //call api
        new CheapTask().execute(cheapAPI);

        View v = inflater.inflate(R.layout.cheaplistfrag,container,false);
        view = v;

        listView = (ListView)v.findViewById(R.id.listview4);
        //searchView = (SearchView) v.findViewById(R.id.menuSearch);



        //MyAdapter myAdapter = new MyAdapter();
        //listView.setAdapter(myAdapter);


        return v;
    }

    //api task
    //AsyncTask<傳入值型態, 更新進度型態, 結果型態>
    private class CheapTask extends AsyncTask<String,Void, Void>{

        Bitmap bitmap;

        @Override
        protected Void doInBackground(String... params) {
            try{

                // call api
                ArrayList<NameValuePair> sendlist = new ArrayList<NameValuePair>();
                sendlist.add(new BasicNameValuePair("search",record));
                JSONArray responseJSON = new JSONArray(API.CallAPI("GET",params[0],sendlist,access_token));
                DATA_COUNT=responseJSON.length();

                // json資料處理
                listData = new ListData[DATA_COUNT];
                for(int i=0; i<DATA_COUNT; i++){
                    JSONObject data = responseJSON.getJSONObject(i);
                    listData[i] = new ListData();
                    listData[i].picture_url = "https:" + data.getString("picture");
                    listData[i].price = "$" + data.getString("price") + "元";
                    listData[i].product = data.getString("product");
                    listData[i].source = data.getString("source");
                    listData[i].update_time = data.getString("update_time");
                    listData[i].item_url = "https:" + data.getString("url");

                    // 圖片處理
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
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            CheapListAdapter listAdapter = new CheapListAdapter();
            listView.setAdapter(listAdapter);
        }
    }


    private class CheapListAdapter extends BaseAdapter {

        //取得 ListView 列表 Item 的數量
        @Override
        public int getCount() {

            return DATA_COUNT;
        }

        //回傳Item的資料
        @Override
        public Object getItem(int position) {

            return listData[position];
        }

        //回傳Item的ID
        @Override
        public long getItemId(int position) {

            return position;
        }

        //回傳處理後的ListItem畫面
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if(convertView == null){
                holder = new ViewHolder();
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview, parent, false);
                holder.img = (ImageView) convertView.findViewById(R.id.img);
                holder.name = (TextView) convertView.findViewById(R.id.name);
                holder.price = (TextView) convertView.findViewById(R.id.price);
                holder.web = (TextView) convertView.findViewById(R.id.web);
                holder.url = (TextView) convertView.findViewById(R.id.url);
                holder.date = (TextView) convertView.findViewById((R.id.date));
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }



            //listview 內容設定
            holder.img.setImageBitmap(listData[position].picture);
            holder.name.setText(listData[position].product);
            holder.price.setText(listData[position].price);
            holder.web.setText(listData[position].source);
            holder.url.setText(listData[position].item_url);
            holder.date.setText(listData[position].update_time);

            return convertView;
        }

    }

    /*
    private class ImgAsyncTask extends AsyncTask<String,Void,Bitmap> {
        Bitmap bitmap;
        int position;

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                position = Integer.valueOf(params[1]);
                Log.d("picture_url check", params[0]);
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream in = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(in);
            }catch (Exception e){
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }

            listData[position].picture=bitmap;
            return bitmap;
        }
        /*
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            ImageView img = (ImageView)view.findViewById(R.id.img);
            img.setImageBitmap(bitmap);
        }
    }*/

    private class ListData {

        String picture_url;
        Bitmap picture;
        String price;
        String product;
        String source;
        String update_time;
        String item_url;

    }

    private static class ViewHolder{
        ImageView img;
        TextView name;
        TextView price;
        TextView web;
        TextView url;
        TextView date;
    }

}


