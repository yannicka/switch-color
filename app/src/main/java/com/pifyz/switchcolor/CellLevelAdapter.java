package com.pifyz.switchcolor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CellLevelAdapter extends BaseAdapter {
    private LayoutInflater mInflater;

    public CellLevelAdapter(Context c) {
        mInflater = LayoutInflater.from(c);
    }

    public int getCount() {
        return MenuActivity.grids.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        RecordHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.cell_level, parent, false);

            holder = new RecordHolder();
            holder.text = convertView.findViewById(R.id.txt_level);
            holder.medal_img = convertView.findViewById(R.id.medal_img);
            convertView.setTag(holder);
        } else {
            holder = (RecordHolder) convertView.getTag();
        }

        holder.text.setText(String.valueOf(position + 1));

        switch (MenuActivity.prefs.getString("medals", "").charAt(position)) {
            case '3':
                holder.medal_img.setBackgroundResource(R.drawable.stars_3);
                break;

            case '2':
                holder.medal_img.setBackgroundResource(R.drawable.stars_2);
                break;

            case '1':
                holder.medal_img.setBackgroundResource(R.drawable.stars_1);
                break;

            default:
                holder.medal_img.setBackgroundResource(R.drawable.stars_0);
        }

        if (MenuActivity.prefs.getString("levels", "").charAt(position) == '1') {
            holder.text.setBackgroundColor(0xFF42C49C);
        } else {
            holder.text.setBackgroundColor(0xFF8C5D67);
        }

        return convertView;
    }

    public static class RecordHolder {
        public TextView text;
        public ImageView medal_img;
    }
}
