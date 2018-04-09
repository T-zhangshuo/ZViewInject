package com.zhangshuo.zviewinject;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.ListView;
import android.widget.TextView;

import com.zhangshuo.zvanno.ViewInject;
import com.zhangshuo.zvapi.ViewInjector;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity {
    @ViewInject( value = R.id.main_txt,clickEvent = "addList")
    TextView textView;

    @ViewInject(R.id.main_listview)
    ListView listView;

    private ArrayAdapter arrayAdapter;
    private List<String> dataList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewInjector.inject(this);
        arrayAdapter=new ArrayAdapter(this,dataList);
        listView.setAdapter(arrayAdapter);
        //使用注解
//        textView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

    }

    public void addList(){
        dataList.add("test测试"+dataList.size());
        arrayAdapter.notifyDataSetChanged();
    }
}
