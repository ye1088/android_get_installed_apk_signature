package com.google.getsign;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Administrator on 2017/5/29 0029.
 */

public class MyAdapter extends BaseAdapter {
    List<ApplicationInfo> apps;
    MainActivity mainActivity;
    PackageManager manager;
    ApplicationInfo app_info;
    ViewHolder holder = null;
    public MyAdapter(List<ApplicationInfo> apps, MainActivity mainActivity, PackageManager manager) {
        this.apps = apps;
        this.mainActivity = mainActivity;
        this.manager = manager;
    }

    @Override
    public int getCount() {
        return apps.size();
    }

    @Override
    public Object getItem(int position) {
        return apps.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView==null){
            convertView = View.inflate(mainActivity,R.layout.adapter_view,null);

            holder = new ViewHolder();

        }
        holder.img_icon = (ImageView) convertView.findViewById(R.id.apk_icon);
        holder.tv_apkName = (TextView) convertView.findViewById(R.id.apk_name);
        holder.tv_packageName = (TextView) convertView.findViewById(R.id.apk_packageName);
        app_info = apps.get(position);
        holder.img_icon.setImageDrawable(app_info.loadIcon(manager));
        holder.tv_apkName.setText(app_info.loadLabel(manager));
        holder.tv_packageName.setText(app_info.packageName);

        return convertView;
    }

    class ViewHolder{
        public TextView tv_apkName;
        public TextView tv_packageName;
        public ImageView img_icon;

    }
}
