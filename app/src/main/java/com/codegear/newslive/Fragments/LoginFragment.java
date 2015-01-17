package com.codegear.newslive.Fragments;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.codegear.newslive.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {


    TextView textView,errmsg;
    Button lgnbtn;
    EditText usernameView, passwordView;

    OnFragmentInteractionListener mListener;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);


        textView = (TextView) view.findViewById(R.id.linetoreg);
        lgnbtn = (Button) view.findViewById(R.id.btnlgn);
        usernameView = (EditText) view.findViewById(R.id.lgnusername);
        passwordView = (EditText) view.findViewById(R.id.lgnpassword);
        errmsg = (TextView) view.findViewById(R.id.lgn_errmsg);


        textView.setOnClickListener(this);
        lgnbtn.setOnClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        if (mListener != null) {
            switch (view.getId()) {
                case R.id.btnlgn:
                    mListener.onActionPerform(usernameView, passwordView,errmsg);
                    break;
                case R.id.linetoreg:
                    mListener.onActionChange(view);
                    break;
            }
        }
    }

//Callback Interface

    public interface OnFragmentInteractionListener {

        public void onActionChange(View view);

        public void onActionPerform(EditText usernameV, EditText passwordV, TextView errmsg );
    }


}
