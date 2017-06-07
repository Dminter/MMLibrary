package com.zncm.library.adapter;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.malinskiy.materialicons.widget.IconTextView;
import com.zncm.library.R;
import com.zncm.library.data.BaseData;

import java.util.List;

public abstract class LibAdapter extends BaseAdapter {

    private List<? extends BaseData> items;
    private Context ctx;

    public LibAdapter(Context ctx) {
        this.ctx = ctx;
    }

    public void setItems(List<? extends BaseData> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (items != null) {
            return items.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (items != null) {
            return items.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        if (items != null) {
            return position;
        } else {
            return 0;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(ctx).inflate(
                    R.layout.cell_item, null);
            holder = new MyViewHolder();
            holder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            holder.ivIcon = (ImageView) convertView.findViewById(R.id.ivIcon);
            holder.tvContent = (TextView) convertView.findViewById(R.id.tvContent);
            holder.tvIcon = (ImageView) convertView.findViewById(R.id.tvIcon);
            holder.operate = (IconTextView) convertView.findViewById(R.id.operate);
            holder.btn = (Button) convertView.findViewById(R.id.btn);
            holder.rlBg = (RelativeLayout) convertView.findViewById(R.id.rlBg);
            convertView.setTag(holder);
        } else {
            holder = (MyViewHolder) convertView.getTag();
        }
        setData(position, holder);

        return convertView;
    }

    public abstract void setData(int position, MyViewHolder holder);

    public class MyViewHolder {
        public TextView tvTitle;
        public TextView tvContent;
        public ImageView tvIcon;
        public ImageView ivIcon;
        public IconTextView operate;
        public RelativeLayout rlBg;
        public Button btn;
    }
}