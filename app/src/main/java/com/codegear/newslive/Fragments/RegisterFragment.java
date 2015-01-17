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
public class RegisterFragment extends Fragment implements View.OnClickListener {


    TextView textView,regsuc,regerr;
    Button regbtn;
    EditText nameView, emailView, usernameView, passwordView;
    OnFragmentInteractionListener mListener;

    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        textView = (TextView) view.findViewById(R.id.linetolgn);
        regbtn = (Button) view.findViewById(R.id.btnreg);
        nameView = (EditText) view.findViewById(R.id.regname);
        usernameView = (EditText) view.findViewById(R.id.regusername);
        emailView = (EditText) view.findViewById(R.id.regemail);
        passwordView = (EditText) view.findViewById(R.id.regpassword);
        regsuc = (TextView) view.findViewById(R.id.reg_success);
        regerr = (TextView) view.findViewById(R.id.reg_errmsg);

        regbtn.setOnClickListener(this);

        textView.setOnClickListener(this);
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
            switch (view.getId()){
                case R.id.btnreg:
                    mListener.onRegAction(nameView, emailView, usernameView, passwordView, regerr, regsuc);
                    break;
                case R.id.linetolgn:
                    mListener.onActionChange(view);
                    break;
            }
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and nameView
        public void onActionChange(View view);

        public void onRegAction(EditText nameV, EditText emailV, EditText usernameV, EditText passwordV, TextView errmsg, TextView regsuc);
    }


}
