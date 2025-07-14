package com.example.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class LrcAdapter extends RecyclerView.Adapter<LrcAdapter.LrcViewHolder> {
    private List<LrcLine> lrcList;
    private int highlightPosition = -1;
    private Context context;

    public LrcAdapter(List<LrcLine> lrcList, Context context) {
        this.lrcList = lrcList;
        this.context = context;
    }

    public void setHighlightPosition(int position) {
        int old = highlightPosition;
        highlightPosition = position;
        if (old != -1) notifyItemChanged(old);
        if (position != -1) notifyItemChanged(position);
    }

    @NonNull
    @Override
    public LrcViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new LrcViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LrcViewHolder holder, int position) {
        holder.textView.setText(lrcList.get(position).text);
        if (position == highlightPosition) {
            holder.textView.setTextColor(Color.parseColor("#FF4081")); // 高亮色
            holder.textView.setTextSize(20);
        } else {
            holder.textView.setTextColor(Color.BLACK);
            holder.textView.setTextSize(16);
        }
    }

    @Override
    public int getItemCount() {
        return lrcList.size();
    }

    static class LrcViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public LrcViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }
} 