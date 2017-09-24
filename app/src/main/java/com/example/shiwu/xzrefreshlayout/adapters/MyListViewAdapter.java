package com.example.shiwu.xzrefreshlayout.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.shiwu.xzrefreshlayout.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shiwu on 2017/8/10.
 */

public class MyListViewAdapter extends BaseAdapter {
    private Context context;
    private List<String> data;
    private LayoutInflater mInflater;

    public MyListViewAdapter(Context context, List<String> data) {
        this.context = context;
        this.data = data;
        mInflater = LayoutInflater.from(context);
    }

    public MyListViewAdapter(Context context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.view_item,null);
            holder = new ViewHolder();
            holder.textView = (TextView) convertView.findViewById(R.id.text_view);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.textView.setText(data.get(position));
        return convertView;
    }

    public void setDatas(List<String> datas) {
        this.data = datas;
        notifyDataSetChanged();
    }

    public void loadData(List<String> data){
        if (data == null) {
            return;
        }
        if (this.data == null) {
            this.data = new ArrayList<>();
        }
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public class ViewHolder{
        TextView textView;
    }
}
