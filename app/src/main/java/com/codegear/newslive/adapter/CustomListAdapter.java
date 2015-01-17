package com.codegear.newslive.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.codegear.newslive.R;
import com.codegear.newslive.utils.LiveStream;
import com.codegear.newslive.utils.Video;

import java.text.SimpleDateFormat;
import java.util.List;


public class CustomListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<LiveStream> streamItems;
    private List<Video> vodItems;
    private int type = -1;

    public CustomListAdapter(Activity activity, List<LiveStream> streamItems, int type) {
        this.activity = activity;
        this.streamItems = streamItems;
        this.type = type;
        this.type = 1;
    }

    public CustomListAdapter(Activity activity, List<Video> vodItems) {
        this.activity = activity;
        this.vodItems = vodItems;
    }


    @Override
    public int getCount() {
        if(type == 1) {
            return streamItems.size();
        }
        else{
           return vodItems.size();
        }
    }

    @Override
    public Object getItem(int location) {
        if(type == 1){
            return streamItems.get(location);
        }
        else {
            return vodItems.get(location);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.stream_list_item, null);


        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView rightTitle = (TextView) convertView.findViewById(R.id.rightTitle);
        TextView liveLable = (TextView) convertView.findViewById(R.id.liveLabel);


        if (type == 1) {

            LiveStream m = streamItems.get(position);
            title.setText(m.getTitle());
            SimpleDateFormat ft = new SimpleDateFormat("hh:mm:ss");

            String date = ft.format(m.getDate());
            rightTitle.setText("Started at: " + date);

        } else {
            Video m = vodItems.get(position);
            liveLable.setVisibility(View.GONE);
            title.setText(m.getTitle());
            rightTitle.setText("Duration: "+m.getLength());
        }

        // title


        return convertView;
    }

}
