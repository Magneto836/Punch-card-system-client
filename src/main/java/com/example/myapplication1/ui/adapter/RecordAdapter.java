package com.example.myapplication1.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication1.R;

import java.util.List;

import api.RecordPojo;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.RecordViewHolder> {

    private List<RecordPojo> recordList;

    // 构造函数，用于传递打卡记录列表
    public RecordAdapter(List<RecordPojo> recordList) {
        this.recordList = recordList;
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_record, parent, false);
        return new RecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        RecordPojo record = recordList.get(position);

        // 设置打卡日期
        holder.clockInDate.setText(record.getClockInDate());

        // 设置打卡时间
        holder.clockInTime.setText(record.getClockInTime());

        // 设置签退时间
        holder.clockOutTime.setText(record.getClockOutTime());

        // 设置位置信息
        holder.location.setText("(" + record.getLocationX() + ", " + record.getLocationY() + ")");

        // 根据 status 设置状态文本
        String statusText;
        switch (record.getStatus()) {
            case "absence":
                statusText = "迟到且早退";
                break;
            case "early":
                statusText = "早退";
                break;
            case "late":
                statusText = "迟到";
                break;
            default:
                statusText = "正常";
                break;
        }
        holder.status.setText(statusText);  // 将文字状态设置为显示内容
    }

    @Override
    public int getItemCount() {
        return recordList.size();  // 返回记录的数量
    }

    public static class RecordViewHolder extends RecyclerView.ViewHolder {
        TextView clockInTime, clockOutTime, location, status, clockInDate;

        public RecordViewHolder(@NonNull View itemView) {
            super(itemView);
            clockInTime = itemView.findViewById(R.id.clockInTime);
            clockOutTime = itemView.findViewById(R.id.clockOutTime);
            location = itemView.findViewById(R.id.location);
            status = itemView.findViewById(R.id.status);
            clockInDate = itemView.findViewById(R.id.clockInDate);
        }
    }
}
