package com.feedback.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import com.feedback.R;
import com.feedback.model.Staff_;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shridhar on 15/6/17.
 */

public class ListAdapter extends BaseAdapter {
    Context mcontext;
    List<Staff_> staffDetails = new ArrayList<>();
    public ListAdapter(Context context,List<Staff_> staffDetails) {
        this.mcontext = context;
        this.staffDetails = staffDetails;
    }
    public static class ViewHolder {
        CheckBox checkBox;
    }

    @Override
    public int getCount() {
        return staffDetails.size();
    }

    @Override
    public Object getItem(int position) {
        return staffDetails.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        LayoutInflater inflater = (LayoutInflater)mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.check_list,null,false);
        holder.checkBox = (CheckBox)convertView.findViewById(R.id.checklist);
        holder.checkBox.setText(staffDetails.get(position).getName());
        return convertView;
    }

}
