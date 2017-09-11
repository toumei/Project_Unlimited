package com.example.user.text;

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

        //listView = (ListView)v.findViewById(R.id.listview4);
        //searchView = (SearchView) v.findViewById(R.id.menuSearch);



        //MyAdapter myAdapter = new MyAdapter();
        //listView.setAdapter(myAdapter);


        return v;
    }

    //api task
    //AsyncTask<傳入值型態, 更新進度型態, 結果型態>
    class CheapTask extends AsyncTask<String,Void, Void>{

        @Override
        protected Void doInBackground(String... params) {
            try{
                ArrayList<NameValuePair> sendlist = new ArrayList<NameValuePair>();
                sendlist.add(new BasicNameValuePair("search",record));
                JSONArray responseJSON = new JSONArray(API.CallAPI("GET",params[0],sendlist,access_token));
                Log.d("Arraytest",responseJSON.toString());

            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

    /*
    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {

            return imgs.length;
        }

        @Override
        public Object getItem(int position) {

            return null;
        }

        @Override
        public long getItemId(int position) {

            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup parent) {
            view = getLayoutInflater(null).inflate(R.layout.listview, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.img);
            TextView textView_name = (TextView) view.findViewById(R.id.name);
            TextView textView_price = (TextView) view.findViewById(R.id.price);
            TextView textView_web = (TextView) view.findViewById(R.id.web);

            imageView.setImageResource(imgs[i]);
            textView_name.setText(names[i]);
            textView_price.setText(price[i]);
            textView_web.setText(webs[i]);

            return view;
        }

    }
    */


}


