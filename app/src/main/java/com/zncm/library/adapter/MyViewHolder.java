package com.zncm.library.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.malinskiy.materialicons.widget.IconTextView;
import com.zncm.library.R;

public class MyViewHolder extends RecyclerView.ViewHolder {
    public TextView tvTitle;
    public TextView tvContent;
    public ImageView ivIcon;
    public CardView mCardView;
    public Button btn;

    public MyViewHolder(View itemView) {
        super(itemView);
        if (itemView != null) {
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            ivIcon = (ImageView) itemView.findViewById(R.id.ivIcon);
            tvContent = (TextView) itemView.findViewById(R.id.tvContent);
            btn = (Button) itemView.findViewById(R.id.btn);
            mCardView = (CardView) itemView.findViewById(R.id.mCardView);
        }
    }
}
