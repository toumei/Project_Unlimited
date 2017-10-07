package com.example.user.text.cheaplist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.text.R;

/**
 * Created by toy9986619 on 2017/10/7.
 */

public class CheapListAdapter extends BaseAdapter {

    int DATA_COUNT;
    private CheapListModel[] model;

    public CheapListAdapter(int DATA_COUNT, CheapListModel[] model){
        this.DATA_COUNT = DATA_COUNT;
        this.model = model;
    }



        //取得 ListView 列表 Item 的數量
        @Override
        public int getCount() {

            return DATA_COUNT;
        }

        //回傳Item的資料
        @Override
        public Object getItem(int position) {

            return model[position];
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
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview, parent, false);
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
            holder.img.setImageBitmap(model[position].picture);
            holder.name.setText(model[position].product);
            holder.price.setText(model[position].price);
            holder.web.setText(model[position].source);
            holder.url.setText(model[position].item_url);
            holder.date.setText(model[position].update_time);

            return convertView;
        }

    public static class ViewHolder{
        ImageView img;
        TextView name;
        TextView price;
        TextView web;
        TextView url;
        TextView date;
    }
}
