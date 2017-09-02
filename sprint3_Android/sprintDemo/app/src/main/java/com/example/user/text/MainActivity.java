package com.example.user.text;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.LargeValueFormatter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity  {
    private ListView listView;

    private String[] sptions = {"所有","蝦皮","Y拍","淘寶"};
    private Spinner spin;
    private LinearLayout layout;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private String sel;
    private SearchView searchview;
    private BarChart chart;
    public List<String> key,value;

    //from call api
    //private TextView tv;
    //private Button btn;
    private String access_token = "";
    private String tokenGet = "";
    private String method = "GET";
    private String record = "衛生紙";
    //private TextView choice;
    //private TextView avr;
    //private TextView high;
    //private TextView low;
    //private com.github.mikephil.charting.charts.BarChart chart;
    //private ListView pro_List;
    //private LinearLayout layout;
    private String tokenAPI ="http://163.13.127.98:8088/api/auth";
    private String pro_API = "http://163.13.127.98:8088/api/v1.0/find_cheapest";
    private String chartAPI = "http://163.13.127.98:8088/api/price_data";
    private String[] infoStr = {"img","name","price","web","url","date"};
    private int[] infoID = {R.id.img,R.id.name,R.id.price,R.id.web,R.id.url,R.id.date};
    private ListAdapter listAdapter;

    private final static String TOKEN_MESSAGE = "get access token";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //取得Token
        new TokenTask().execute(tokenAPI);

    }

    //設定view
    private void setAPPView(){
        //tab畫面隱藏
        layout = (LinearLayout) findViewById(R.id.FragmentLayout);
        // layout.setVisibility(View.INVISIBLE);

        //为该SearchView组件设置事件监听器
        //searchview.setOnQueryTextListener(this);

        //設定ViewPager
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new CustomAdapter(getSupportFragmentManager(),getApplicationContext()));
        //設定Tablayout
        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
        });


        //設定Spinner下拉是選單
        spin = (Spinner) findViewById(R.id.spin);
        ArrayAdapter<String> adaptersptions = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, sptions);
        adaptersptions.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adaptersptions);
        spin.setSelection(0,true);
        spin.setOnItemSelectedListener(spinadapter);
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    //Spinner監聽
    private Spinner.OnItemSelectedListener spinadapter = new Spinner.OnItemSelectedListener(){

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            //選到的選項
            sel = parent.getSelectedItem().toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            //沒選擇
        }
    };

    //設定Menu Search
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search,menu);

        MenuItem item = menu.findItem(R.id.menuSearch);

        //Get SearchView & set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
       // SearchView searchView = (SearchView) item.getActionView();
        SearchView searchView = (SearchView) menu.findItem(R.id.menuSearch).getActionView();

        //假設可以搜尋
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        //设置该SearchView显示搜索按钮
        searchView.setSubmitButtonEnabled(true);
        //讓icon還原
        searchView.setIconifiedByDefault(true);
        //为该SearchView组件设置事件监听器
        searchView.setSubmitButtonEnabled(true); //不要有submit 的按鈕


        return true;
    }



    //設定Fragment
    private class CustomAdapter extends FragmentPagerAdapter {

        private String fragments[] = {"價格分佈","明日價格","匯率走勢","最便宜清單"};
        public CustomAdapter(FragmentManager supportFragmentManager, Context applicationContext) {
            super(supportFragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return PriceChartFragment.newInstance(access_token);
                case 1:
                    return new PriceGuessFragment();
                case 2:
                    return new RateFragment();
                case 3:
                    return new CheapListFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return fragments.length;
        }
        public CharSequence getPageTitle (int position){
            return fragments[position] ;
        }
    }


    private class BarChart {
    }

    private class BarData {
    }

    //AsyncTask<傳入值型態, 更新進度型態, 結果型態>
    class TokenTask extends AsyncTask<String,Void,String> {

        //工作內容
        @Override
        protected String doInBackground(String... params) {
            //final String basicAuth = "Basic "+ Base64.encodeToString("project:project".getBytes(),Base64.NO_WRAP);
            try {
                String json ="";

                HttpClient httpClient = new DefaultHttpClient();

                HttpPost httpPost = new HttpPost(params[0]);
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("username", "project");
                jsonObject.accumulate("password", "project");

                json = jsonObject.toString();
                StringEntity se = new StringEntity(json);
                httpPost.setEntity(se);
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");
                HttpResponse httpResponse = httpClient.execute(httpPost);
                /*HttpGet httpGet = new HttpGet(params[0]);
                httpGet.setHeader("Authorization",basicAuth);
                HttpResponse httpResponse = httpClient.execute(httpGet);*/
                String responseString = EntityUtils.toString(httpResponse.getEntity(),"UTF-8");
                JSONObject responseJSON = new JSONObject(responseString);
                tokenGet = responseJSON.getString("access_token");
                Log.d("tokenTest",tokenGet);
            } catch (Exception e) {
                Log.d("tokenTest",tokenGet);
                e.printStackTrace();
            }
            return tokenGet;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            access_token = tokenGet;

            Log.d("setTest",access_token);
            registerReceiver(tokenBroadcast, new IntentFilter(TOKEN_MESSAGE));
            Intent intent = new Intent();
            intent.setAction(TOKEN_MESSAGE);
            sendBroadcast(intent);
        }
    }

    private BroadcastReceiver tokenBroadcast = new BroadcastReceiver() {
        private final static String TOKEN_MESSAGE = "get access token";
        @Override
        public void onReceive(Context context, Intent mIntent) {

            if(TOKEN_MESSAGE.equals(mIntent.getAction())){
                setAPPView();
            }

        }
    };


}
