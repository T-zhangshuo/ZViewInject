package com.zhangshuo.zviewinject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zhangshuo.annotation.ViewInject;
import com.zhangshuo.zapi.ViewInjector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangshuo on 2018/3/20.
 */

public class ArrayAdapter extends BaseAdapter {
    private List<String> arrayList = new ArrayList<>();
    private Context context;

    public ArrayAdapter( Context context,List<String> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_main, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.textView.setText(arrayList.get(position));
        return convertView;
    }

    class ViewHolder {
        @ViewInject(R.id.item_txt)
        TextView textView;

        ViewHolder(View itemView) {
            ViewInjector.inject(this, itemView);
        }
    }
}
