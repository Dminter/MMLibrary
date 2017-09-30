package com.zncm.library.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zncm.library.R;
import com.zncm.library.data.BaseData;
import com.zncm.library.utils.XUtil;

import java.util.List;

public abstract class LibAdapter extends RecyclerView.Adapter<MyViewHolder> {

    MyViewHolder holder;
    private List<? extends BaseData> items;
    private Context ctx;

    public LibAdapter(Context ctx) {
        this.ctx = ctx;
    }

    public void setItems(List<? extends BaseData> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void setItems(List<? extends BaseData> items, RecyclerView mRecyclerView) {
        this.items = items;
        notifyDataSetChanged();
        mRecyclerView.getAdapter().notifyDataSetChanged();
    }


//    @Override
//    public int getCount() {
//        if (items != null) {
//            return items.size();
//        }
//        return 0;
//    }
//
//    @Override
//    public Object getItem(int position) {
//        if (items != null) {
//            return items.get(position);
//        }
//        return null;
//    }
//
//    @Override
//    public long getItemId(int position) {
//        if (items != null) {
//            return position;
//        } else {
//            return 0;
//        }
//    }
//
//    @Override
//    public View getView(int position, View itemView, ViewGroup parent) {
//        MyViewHolder holder;
//        if (itemView == null) {
//            itemView = LayoutInflater.from(ctx).inflate(
//                    R.layout.cell_item, null);
//            holder = new MyViewHolder();
//            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
//            ivIcon = (ImageView) itemView.findViewById(R.id.ivIcon);
//            tvContent = (TextView) itemView.findViewById(R.id.tvContent);
//            tvIcon = (ImageView) itemView.findViewById(R.id.tvIcon);
//            operate = (IconTextView) itemView.findViewById(R.id.operate);
//            btn = (Button) itemView.findViewById(R.id.btn);
//            rlBg = (RelativeLayout) itemView.findViewById(R.id.rlBg);
//            itemView.setTag(holder);
//        } else {
//            holder = (MyViewHolder) itemView.getTag();
//        }
//        setData(position, holder);
//
//        return itemView;
//    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_item, parent, false);
        holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.rlBg.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                OnItemLongClickListener(position, holder);
                return true;
            }
        });
        holder.rlBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnItemClickListener(position, holder);
            }
        });
        setData(position, holder);
    }

    @Override
    public int getItemCount() {
        if (XUtil.listNotNull(items)) {
            return items.size();
        } else {
            return 0;
        }

    }

    public abstract void setData(int position, MyViewHolder holder);

    public abstract void OnItemLongClickListener(int position, MyViewHolder holder);

    public abstract void OnItemClickListener(int position, MyViewHolder holder);


}


