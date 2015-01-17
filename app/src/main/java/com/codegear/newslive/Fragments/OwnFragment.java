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

public class OwnFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {


    View rootView;
    OWNStreamList mListener;
    SwipeRefreshLayout swipeRefreshLayout;
    ListView lv;

    public OwnFragment() {
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
            mListener = (OWNStreamList) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement LiveStreamListListener");
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mListener.onOWNRefresh(lv);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mListener.onOWNItemClick(adapterView,view,i,l);
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
            mListener.onOWNRefresh(lv);
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }, 3000);
        }
    }


    public interface OWNStreamList {

        public void onOWNRefresh(ListView view);
        public void onOWNItemClick(AdapterView<?> adapterView, View view, int i, long l);
    }


}
