package com.example.yao.tokentest;

import android.content.Entity;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.message.HeaderGroup;
import org.apache.http.message.HeaderValueParser;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView tv;
    private Button btn;
    private String access_token = "";
    private String tokenGet = "";
    private String method = "GET";
    private String record = "衛生紙";
    private TextView choice;
    private TextView avr;
    private TextView high;
    private TextView low;
    private BarChart chart;
    private ListView pro_List;
    private LinearLayout layout;
    private String tokenAPI ="http://163.13.127.98:8088/api/v1.0/token";
    private String pro_API = "http://163.13.127.98:8088/api/v1.0/find_cheapest";
    private String chartAPI = "http://163.13.127.98:8088/api/price_data";
    private String[] infoStr = {"img","name","price","web","url","date"};
    private int[] infoID = {R.id.img,R.id.name,R.id.price,R.id.web,R.id.url,R.id.date};
    private ListAdapter listAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layout = (LinearLayout)findViewById(R.id.info_layout);
        btn = (Button)findViewById(R.id.button);
        tv = (TextView)findViewById(R.id.tv_token);
        choice = (TextView)findViewById(R.id.title1);
        chart = (BarChart)findViewById(R.id.chart);
        avr = (TextView)findViewById(R.id.avrPrice);
        high = (TextView)findViewById(R.id.highest);
        low = (TextView)findViewById(R.id.low);
        pro_List = (ListView)findViewById(R.id.listView);

        //隱藏layout
        layout.setVisibility(View.INVISIBLE);
        //取得Token
        new TokenTask().execute(tokenAPI);

        btn.setOnClickListener(btnListener);
    }

    Button.OnClickListener btnListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            tv.setText("Bearer "+access_token);
            choice.setText("商品資訊: "+record);
            layout.setVisibility(View.VISIBLE);
            //執行
            new ChartTask().execute(chartAPI);
            new CheapestTask().execute(pro_API);
        }
    };

    //AsyncTask<傳入值型態, 更新進度型態, 結果型態>
    class TokenTask extends AsyncTask<String,Void,String>{

        //工作內容
        @Override
        protected String doInBackground(String... params) {
            final String basicAuth = "Basic "+ Base64.encodeToString("project:project".getBytes(),Base64.NO_WRAP);
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(params[0]);
                httpGet.setHeader("Authorization",basicAuth);
                HttpResponse httpResponse = httpClient.execute(httpGet);
                String responseString = EntityUtils.toString(httpResponse.getEntity(),"UTF-8");
                JSONObject responseJSON = new JSONObject(responseString);
                tokenGet = responseJSON.getString("token");
                Log.e("tokenTest",tokenGet);
            } catch (Exception e) {
                Log.e("tokenTest",tokenGet);
                e.printStackTrace();
            }
            return tokenGet;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            access_token = tokenGet;
            Log.e("setTest",access_token);
        }
    }

    private String CallAPI(String method, String params, ArrayList sendlist){
        String responseString = null;

        try {
            HttpClient httpClient = new DefaultHttpClient();

            if (method == "GET"){
                String stringEntity = URLEncodedUtils.format(sendlist,"UTF-8");
                params += "?"+stringEntity;
                HttpGet httpGet = new HttpGet(params);
                httpGet.setHeader("Authorization","Bearer "+access_token);
                Log.e("getAuth",access_token);
                HttpResponse httpResponse = httpClient.execute(httpGet);
                responseString = EntityUtils.toString(httpResponse.getEntity(),"UTF-8");
            }else if(method == "POST"){
                HttpPost httpPost = new HttpPost(params);
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(sendlist);

                entity.setContentEncoding("UTF-8");
                entity.setContentType("application/json");
                httpPost.setEntity(entity);

                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                responseString = EntityUtils.toString(httpEntity,"UTF-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Error", e.getMessage());
        }
        Log.e("APItest",responseString.toString());
        return responseString;
    }

    class ChartTask extends AsyncTask<String,Void,BarData>{
        BarData barData;
        int[] intKey;
        int[] intValue;
        int unit = 100;
        @Override
        protected BarData doInBackground(String... params) {
            try {
                //傳送資料
                ArrayList<NameValuePair> sendlist = new ArrayList<NameValuePair>();
                sendlist.add(new BasicNameValuePair("kind",record));
                sendlist.add(new BasicNameValuePair("unit",unit+""));
                JSONObject responseJSON = new JSONObject(CallAPI(method,params[0],sendlist));
                Log.e("responseTest",responseJSON.toString());
                //JSONObject dataJSON = responseJSON.getJSONObject("distribution");
                Log.e("dataTest",responseJSON.toString());
                final int dataCount = responseJSON.length();
                intKey = new int[dataCount];
                intValue = new int[dataCount];

                //尋訪JSONObject
                Iterator<String> stringIterator = responseJSON.keys();
                for (int i=0;i<dataCount;i++){
                    //取得key
                    String key = stringIterator.next().toString();
                    intKey[i] = Integer.parseInt(key);
                    String value = responseJSON.getString(key);
                    intValue[i] = Integer.parseInt(value);
                }
                //設定圖表最大值
                int Max = intKey[dataCount-1];
                List<BarEntry> chartdata = new ArrayList<>();
                for(int i=0;i<dataCount;i++){
                    chartdata.add(new BarEntry(intValue[i],intKey[i]/unit));
                }
                //設定長條數據
                BarDataSet dataSet = new BarDataSet(chartdata,"資料量");
                dataSet.setColor(getResources().getColor(R.color.chart_product));

                //設定X軸,以unit作單位
                List<String> chartLabels = new ArrayList<>();
                for(int i=0;i<=Max;i+=unit){
                    chartLabels.add(i+"-");
                }
                //建立長條圖
                barData = new BarData(chartLabels,dataSet);
                barData.setValueFormatter(new MyValueFormatter());
            }catch (Exception e){
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return barData;
        }

        @Override
        protected void onPostExecute(BarData barData) {
            super.onPostExecute(barData);
            chart.setDescription("");
            chart.setData(barData);
            chart.invalidate();
            //X,Y軸設定
            XAxis xAxis = chart.getXAxis();
            //xAxis.setCenterAxisLabels(true);
            xAxis.setDrawGridLines(false);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            chart.getAxisRight().setEnabled(false);
            YAxis yLAxis = chart.getAxisLeft();
            yLAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
            yLAxis.setDrawGridLines(true);
            yLAxis.setValueFormatter(new LargeValueFormatter());
        }
    }

    class MyValueFormatter implements ValueFormatter {
        private DecimalFormat mformat;
        public MyValueFormatter(){
            mformat = new DecimalFormat("###,###,##0");
            /* NumberFormat 是以每三位數加上逗號的格式化
                         * DecimalFormat 是可以自定義顯示的數字格式
                         * # : digit 不顯示零
                         * 0 : digit 會補零
                         * @ : 顯示幾位, ex. 12345[@@@]=12300
                         * format 用法可以參考以下網址
                         * http://developer.android.com/intl/zh-tw/reference/java/text/DecimalFormat.html
                        */
        }
        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return mformat.format(value);
        }
    }

    class CheapestTask extends AsyncTask<String,Void,ArrayList>{

        @Override
        protected ArrayList doInBackground(String... params) {
            ArrayList<HashMap> mapArrayList = new ArrayList<HashMap>();
            try {
                ArrayList<NameValuePair> sendlist = new ArrayList<NameValuePair>();
                sendlist.add(new BasicNameValuePair("search",record));
                JSONArray responseJSON = new JSONArray(CallAPI(method,params[0],sendlist));
                Log.e("Arraytest",responseJSON.toString());
                for(int i=0;i<responseJSON.length();i++){
                    JSONObject jsonObject = responseJSON.getJSONObject(i);
                    Log.e("ObjectTest",jsonObject.toString());
                    if(jsonObject!=null){
                        HashMap map = new HashMap();
                        map.put(infoStr[0],jsonObject.getString("imagePath"));
                        map.put(infoStr[1],jsonObject.getString("p_name"));
                        map.put(infoStr[2],jsonObject.getString("price"));
                        map.put(infoStr[3],jsonObject.getString("source"));
                        map.put(infoStr[4],jsonObject.getString("url"));
                        map.put(infoStr[5],jsonObject.getString("update_time"));
                        mapArrayList.add(map);
                        Log.e("mapTest",mapArrayList.toString());
                    }
                }
            }catch (Exception e){
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mapArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList list) {
            super.onPostExecute(list);
            listAdapter = new MyAdapter(MainActivity.this,list,R.layout.list_layout,infoStr,infoID);
            pro_List.setAdapter(listAdapter);
        }
    }
}
