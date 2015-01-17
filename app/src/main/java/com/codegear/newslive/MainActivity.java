package com.codegear.newslive;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.codegear.newslive.Fragments.LiveFragment;
import com.codegear.newslive.Fragments.OwnFragment;
import com.codegear.newslive.Fragments.RecordedFragment;
import com.codegear.newslive.adapter.CustomListAdapter;
import com.codegear.newslive.drawer.NavDrawerItem;
import com.codegear.newslive.drawer.NavDrawerListAdapter;
import com.codegear.newslive.utils.Const;
import com.codegear.newslive.utils.LiveStream;
import com.codegear.newslive.utils.PreferenceStorage;
import com.codegear.newslive.utils.Video;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity implements LiveFragment.LiveStreamList, RecordedFragment.VODStreamList, OwnFragment.OWNStreamList{
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    RequestQueue queue;


    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
    private PreferenceStorage p;

    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;
    private Fragment liveFragment,recordFragment,ownFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        p = new PreferenceStorage(getApplicationContext());
        queue = Volley.newRequestQueue(this);

        mTitle = mDrawerTitle = getTitle();

        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        navDrawerItems = new ArrayList<NavDrawerItem>();
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));

        liveFragment = new LiveFragment();
        recordFragment = new RecordedFragment();
        ownFragment = new OwnFragment();

        navMenuIcons.recycle();

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        adapter = new NavDrawerListAdapter(getApplicationContext(),navDrawerItems);
        mDrawerList.setAdapter(adapter);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer,
                R.string.app_name,
                R.string.app_name
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            displayView(0);
        }
    }



    /**
     * Slide menu item click listener
     */
    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
            displayView(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_stream:
                Intent s = new Intent(getApplicationContext(),StreamActivity.class);
                startActivity(s);
                return true;
            case R.id.action_settings:
                Intent i = new Intent(getApplicationContext(),SettingsActivity.class);
                startActivity(i);
                return true;
            case R.id.action_logout:
                p.logoutUser();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* *
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        menu.findItem(R.id.action_logout).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Diplaying fragment view for selected nav drawer list item
     */
    private void displayView(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = liveFragment;
                break;
            case 1:
                fragment = recordFragment;
                break;
            case 2:
                fragment = ownFragment;
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();

            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(navMenuTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public void onLiveRefresh(final ListView listView) {

        final List<LiveStream> movieList = new ArrayList<LiveStream>();
        final CustomListAdapter adapter = new CustomListAdapter(this, movieList,1);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                listView.setAdapter(adapter);

                Map<String, String> params = new HashMap<String, String>();

                // Creating volley request obj
                JsonArrayRequest streamReq = new JsonArrayRequest(Const.LIVE_URL,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                Log.d("Volley Response", response.toString());



                               if(response.length() > 0){
                                   // Parsing json
                                   for (int i = 0; i < response.length(); i++) {
                                       try {

                                           JSONObject obj = response.getJSONObject(i);
                                           LiveStream movie = new LiveStream();
                                           movie.setTitle(obj.getString("title"));
                                           movie.setStream(obj.getString("stream"));
                                           movie.setUser(obj.getString("user"));

                                           SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                           Date t = null;
                                           try {
                                               t = ft.parse(obj.getString("started_at"));
                                           } catch (ParseException e) {
                                               e.printStackTrace();
                                           }

                                           movie.setDate(t);

                                           movieList.add(movie);

                                       } catch (JSONException e) {
                                           e.printStackTrace();
                                       }

                                   }
                                   TextView emptyList = (TextView) findViewById(R.id.empty);
                                   emptyList.setText("");
                               }
                                else{
                                   TextView emptyList = (TextView) findViewById(R.id.empty);
                                   emptyList.setText("No Live Streams Right Now");
                               }
                                adapter.notifyDataSetChanged();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d("Volley", "Error: " + error.getMessage());

                    }
                });


                queue.add(streamReq);
            }
        });


    }

    @Override
    public void onVODRefresh(final ListView listView) {


        final List<Video> movieList = new ArrayList<Video>();
        final CustomListAdapter adapter = new CustomListAdapter(this, movieList);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                listView.setAdapter(adapter);

                Map<String, String> params = new HashMap<String, String>();

                // Creating volley request obj
                JsonArrayRequest streamReq = new JsonArrayRequest(Const.VOD_URL,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                Log.d("Volley Response", response.toString());

                                // Parsing json
                               if(response.length() > 0){
                                   for (int i = 0; i < response.length(); i++) {
                                       try {

                                           JSONObject obj = response.getJSONObject(i);
                                           Video movie = new Video();
                                           movie.setTitle(obj.getString("title"));
                                           movie.setStream(obj.getString("stream"));
                                           movie.setUser(obj.getString("user"));
                                           movie.setLength(obj.getString("length"));

                                           movieList.add(movie);

                                       } catch (JSONException e) {
                                           e.printStackTrace();
                                       }

                                   }
                                   TextView emptyList = (TextView) findViewById(R.id.empty);
                                   emptyList.setText("");
                               }
                                else{
                                   TextView emptyList = (TextView) findViewById(R.id.empty);
                                   emptyList.setText("No User has posted any videos, yet!");
                               }

                                adapter.notifyDataSetChanged();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d("Volley", "Error: " + error.getMessage());

                    }
                });


                queue.add(streamReq);
            }
        });


    }

    @Override
    public void onOWNRefresh(final ListView listView) {
        final List<Video> movieList = new ArrayList<Video>();
        final CustomListAdapter adapter = new CustomListAdapter(this, movieList);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                listView.setAdapter(adapter);

                JsonArrayRequest streamReq = new JsonArrayRequest(Const.VOD_URL,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                Log.d("Volley Response", response.toString());

                                // Parsing json
                                if(response.length() > 0){
                                    for (int i = 0; i < response.length(); i++) {
                                        try {

                                            JSONObject obj = response.getJSONObject(i);
                                            if(obj.getString("user").equals(p.getUserDetails().get(PreferenceStorage.KEY_USERNAME))) {
                                                Video movie = new Video();
                                                movie.setTitle(obj.getString("title"));
                                                movie.setStream(obj.getString("stream"));
                                                movie.setUser(obj.getString("user"));
                                                movie.setLength(obj.getString("length"));
                                                movieList.add(movie);
                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    TextView emptyList = (TextView) findViewById(R.id.empty);
                                    emptyList.setText("");
                                }
                                else{
                                    TextView emptyList = (TextView) findViewById(R.id.empty);
                                    emptyList.setText("You have not posted any video!");
                                }


                                adapter.notifyDataSetChanged();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d("Volley", "Error: " + error.getMessage());

                    }
                });
                queue.add(streamReq);
            }
        });

    }

    @Override
    public void onOWNItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Video v = (Video) adapterView.getItemAtPosition(i);
        Intent it = new Intent(getApplicationContext(), PlayStreamActivity.class);
        it.putExtra("stream", v.getStream() );
        it.putExtra("title",v.getTitle());
        it.putExtra("app", "VOD" );
        startActivity(it);
    }

    @Override
    public void onLiveItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        LiveStream v = (LiveStream) adapterView.getItemAtPosition(i);
        Intent it = new Intent(getApplicationContext(), PlayStreamActivity.class);
        it.putExtra("stream", v.getStream() );
        it.putExtra("title",v.getTitle());
        it.putExtra("app", "LIVE");
        startActivity(it);
    }

    @Override
    public void onVODItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Video v = (Video) adapterView.getItemAtPosition(i);
        Intent it = new Intent(getApplicationContext(), PlayStreamActivity.class);
        it.putExtra("stream", v.getStream() );
        it.putExtra("title",v.getTitle());
        it.putExtra("app","VOD" );
        startActivity(it);
    }

}
