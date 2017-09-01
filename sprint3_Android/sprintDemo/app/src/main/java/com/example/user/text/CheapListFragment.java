package com.example.user.text;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

/**
 * Created by user on 2017/8/2.;
 * Created by user on 2017/8/2.;
 * Created by user on 2017/8/2.;
 */

public class CheapListFragment extends Fragment {
    private ListView listView;
    private SearchView searchView;



    int[] imgs = {R.drawable.tissu,R.drawable.tissu,R.drawable.tissu,R.drawable.tissu,R.drawable.tissu,R.drawable.tissu,R.drawable.tissu,R.drawable.tissu,R.drawable.tissu,R.drawable.tissu,
                  R.drawable.bag,R.drawable.bag,R.drawable.bag,R.drawable.bag,R.drawable.bag,R.drawable.bag,R.drawable.bag,R.drawable.bag,R.drawable.bag,R.drawable.bag  };

    String[] names = {"衛生紙1","衛生紙2","衛生紙3","衛生紙4","衛生紙5","衛生紙6","衛生紙7","衛生紙8","衛生紙9","衛生紙0",
                       "背包1" ,"背包2" ,"背包3" ,"背包4" ,"背包5" ,"背包6" ,"背包7" ,"背包8" ,"背包9" ,"背包0"  };
    String[] price = {"1","1","1","1","1","1","1","1","1","1",
            "1","1","1","1","1","1","1","1","1","1"};
    String[] webs = {"蝦皮","Y拍","淘寶","蝦皮","Y拍","淘寶","蝦皮","Y拍","淘寶","蝦皮","Y拍","淘寶","蝦皮","Y拍","淘寶","蝦皮","Y拍","淘寶","蝦皮","Y拍"};




    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstenceState){

        View v = inflater.inflate(R.layout.cheaplistfrag,container,false);

        listView = (ListView)v.findViewById(R.id.listview4);
        searchView = (SearchView) v.findViewById(R.id.menuSearch);



        MyAdapter myAdapter = new MyAdapter();
        listView.setAdapter(myAdapter);


        return v;
    }


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


}


