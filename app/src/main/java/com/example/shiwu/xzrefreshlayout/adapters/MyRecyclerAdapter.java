package com.example.shiwu.xzrefreshlayout.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.shiwu.xzrefreshlayout.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shiwu on 2017/8/8.
 */

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder> {
    private Context context;
    private LayoutInflater mInflater;

    public MyRecyclerAdapter(Context context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    private List<String> datas;

    public void setDatas(List<String> datas) {
        this.datas = datas;
        notifyDataSetChanged();
    }

    public void addData(List<String> data){
        if (data == null) {
            return;
        }
        if (datas == null) {
            datas = new ArrayList<>();
        }
        datas.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.view_item,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textView.setText(datas.get(position));
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.text_view);
        }
    }
}
