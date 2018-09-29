package com.example.dell.benchtest;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class DataArrayAdapter extends RecyclerView.Adapter<DataArrayAdapter.ViewHolder>{

    private List<ReadData.DataArrayBean> mDataArrayList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View DataArrayView;
        TextView SensorName,SensorValue,SensorUnit;
        public ViewHolder(View view) {
            super(view);
            DataArrayView = view;
            SensorName = (TextView) view.findViewById(R.id.SensorName);
            SensorValue= (TextView) view.findViewById(R.id.SensorValue);
            SensorUnit= (TextView) view.findViewById(R.id.SensorUnit);
        }
    }

    public DataArrayAdapter(List<ReadData.DataArrayBean> DataArrayList) {
        mDataArrayList = DataArrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dataarray, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ReadData.DataArrayBean dataarraybean = mDataArrayList.get(position);
        holder.SensorName.setText(dataarraybean.getSensorName());
        holder.SensorValue.setText(dataarraybean.getSensorValue());
        holder.SensorUnit.setText(dataarraybean.getUnit());
    }

    @Override
    public int getItemCount() {
        return mDataArrayList.size();
    }

}