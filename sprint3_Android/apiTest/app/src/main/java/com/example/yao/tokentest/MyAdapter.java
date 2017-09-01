package com.example.yao.tokentest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class MyAdapter extends BaseAdapter {
    private static LayoutInflater inflater = null;
    private List<?extends Map<String,?>> data;
    private int resource;
    private String[] from;
    private int[] to;
    private View view;

    private static class ViewHolder{
        ImageView img;
        TextView name;
        TextView price;
        TextView web;
        TextView url;
        TextView date;
    }

    MyAdapter(Context context, List<?extends Map<String,?>> data, int resource, String[] from, int[] to){
        super();
        inflater = LayoutInflater.from(context);
        this.data = data;
        this.resource = resource;
        this.from = from;
        this.to = to;
        Log.e("dataTest",data.toString());
    }
    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Map<String,?> getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null){
            convertView = inflater.inflate(R.layout.list_layout,parent,false);
            view = convertView;
            holder = new ViewHolder();
            holder.img = (ImageView)convertView.findViewById(to[0]);
            holder.name = (TextView)convertView.findViewById(to[1]);
            holder.price = (TextView)convertView.findViewById(to[2]);
            holder.web = (TextView)convertView.findViewById(to[3]);
            holder.url = (TextView)convertView.findViewById(to[4]);
            holder.date = (TextView)convertView.findViewById(to[5]);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        String imgurl = "https:"+getItem(position).get(from[0]).toString();
        String p_name = getItem(position).get(from[1]).toString();
        String p_price = "$ "+getItem(position).get(from[2]).toString()+" 元";
        String p_web = getItem(position).get(from[3]).toString();
        String p_url = getItem(position).get(from[4]).toString();
        String p_date = "資料更新時間 : "+getItem(position).get(from[5]).toString();
        if(p_web.equals("淘寶")){
            p_url = "https:"+p_url;
        }
        //圖片url轉bitmap
        new ImgAsyncTask().execute(imgurl);
        //TextView超連結
        SpannableString sp = new SpannableString(p_name);
        sp.setSpan(new URLSpan(p_url),0,p_name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        holder.name.setText(sp);
        holder.name.setMovementMethod(LinkMovementMethod.getInstance());
        holder.price.setText(p_price);
        holder.web.setText(p_web);
        holder.url.setText(p_url);
        holder.date.setText(p_date);

        return convertView;
    }

    private class ImgAsyncTask extends AsyncTask<String,Void,Bitmap> {
        Bitmap bitmap;

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
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
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            ImageView img = (ImageView)view.findViewById(R.id.img);
            img.setImageBitmap(bitmap);
        }
    }
}
