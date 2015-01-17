package com.codegear.newslive.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.codegear.newslive.R;

public class RecordedFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {


    View rootView;
    VODStreamList mListener;
    SwipeRefreshLayout swipeRefreshLayout;
    ListView lv;

    public RecordedFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_stream_list, container, false);
        lv = (ListView) rootView.findViewById(R.id.streamlist);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refreshLayout);
        swipeRefreshLayout.setColorSchemeColors(R.color.blue, R.color.purple, R.color.green, R.color.orange);
        swipeRefreshLayout.setOnRefreshListener(this);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (VODStreamList) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement LiveStreamListListener");
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mListener.onVODRefresh(lv);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mListener.onVODItemClick(adapterView,view,i,l);
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onRefresh() {
        if (mListener != null) {
            mListener.onVODRefresh(lv);
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }, 3000);
        }
    }


    public interface VODStreamList {

        public void onVODRefresh(ListView view);
        public void onVODItemClick(AdapterView<?> adapterView, View view, int i, long l);
    }


}
