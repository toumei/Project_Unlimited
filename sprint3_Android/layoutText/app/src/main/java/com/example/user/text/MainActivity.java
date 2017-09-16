package com.example.user.text;

import android.app.SearchManager;
import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                    return new Fragment1();
                case 1:
                    return new Fragment2();
                case 2:
                    return new Fragment3();
                case 3:
                    return new Fragment4();
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
}
