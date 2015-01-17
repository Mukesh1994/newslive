package com.codegear.newslive;


import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.codegear.newslive.Fragments.LoginFragment;
import com.codegear.newslive.Fragments.RegisterFragment;
import com.codegear.newslive.utils.Const;
import com.codegear.newslive.utils.CustomRequest;
import com.codegear.newslive.utils.PreferenceStorage;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends Activity implements LoginFragment.OnFragmentInteractionListener, RegisterFragment.OnFragmentInteractionListener {


    FragmentManager fragmentManager;
    private String username, name, email, password;
    PreferenceStorage p;

    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        queue = Volley.newRequestQueue(this);
        p = new PreferenceStorage(getApplicationContext());

        if(p.isLoggedIn()){
            Intent i = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(i);
            finish();
        }


        fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.animator.enter_anim, R.animator.exit_anim);
        transaction.replace(R.id.fragment, new LoginFragment()).commit();


    }

    @Override
    public void onActionChange(View view) {
        FragmentTransaction transaction;
        fragmentManager.beginTransaction();
        switch (view.getId()) {
            case R.id.linetolgn:
                fragmentManager = getFragmentManager();
                transaction = fragmentManager.beginTransaction();
                transaction.setCustomAnimations(R.animator.enter_anim, R.animator.exit_anim);
                transaction.replace(R.id.fragment, new LoginFragment()).commit();
                break;
            case R.id.linetoreg:
                fragmentManager = getFragmentManager();
                transaction = fragmentManager.beginTransaction();
                transaction.setCustomAnimations(R.animator.enter_anim, R.animator.exit_anim);
                transaction.replace(R.id.fragment, new RegisterFragment()).commit();
                break;
            default:
                Log.e("FragmentChange", "Error in changing Fragment");
                break;
        }
    }

    @Override
    public void onActionPerform(EditText usernameV, EditText passwordV, final TextView errmsg) {

        errmsg.clearComposingText();

        final ProgressDialog pDialog = new ProgressDialog(this);

        pDialog.setMessage("Logging in...");
        pDialog.show();

        username = usernameV.getText().toString();
        password = passwordV.getText().toString();

        Map<String, String> params = new HashMap<String, String>();
        params.put("username", username);
        params.put("password", password);

        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, Const.LOGIN_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("Response: ", response.toString());
                pDialog.dismiss();
                if(response.optString("result", "false").equals("true")) {
                    p.createLoginSession(response.optString("name"), username, response.optString("email"));
                    Intent i = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(i);
                    finish();
                }
                else{
                    errmsg.setText(response.optString("err","Error"));
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError response) {
                Log.d("Response: ", response.toString());
                pDialog.dismiss();
            }
        });

        queue.add(jsObjRequest);

       // Intent i = new Intent(getApplicationContext(), MainActivity.class);
       // startActivity(i);
       // finish();

    }

    @Override
    public void onRegAction(EditText nameV, EditText emailV, EditText usernameV, EditText passwordV, final TextView errmsg, final TextView regsuc) {

        errmsg.clearComposingText();
        regsuc.clearComposingText();
        final ProgressDialog pDialog = new ProgressDialog(this);

        pDialog.setMessage("Registering...");
        pDialog.show();
        email = emailV.getText().toString();
        name = nameV.getText().toString();
        username = usernameV.getText().toString();
        password = passwordV.getText().toString();

        Map<String, String> params = new HashMap<String, String>();
        params.put("name",name);
        params.put("email",email);
        params.put("username", username);
        params.put("password", password);

        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, Const.REGISTER_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("Response: ", response.toString());
                pDialog.dismiss();
                if(response.optString("result", "false").equals("true")) {
                    regsuc.setText(response.optString("msg","Registration Successfull. You can login now!"));
                }
                else{
                    errmsg.setText(response.optString("err", "Error"));
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError response) {
                Log.d("Response: ", response.toString());
                pDialog.dismiss();
            }
        });

        queue.add(jsObjRequest);
    }
}
