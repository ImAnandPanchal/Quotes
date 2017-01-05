package com.example.panch.quotes;

import android.app.LauncherActivity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class DisplayActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /*ads*/
    private InterstitialAd interstitialAd;
    //private InterstitialAd getInterstitialAd;

    Button btnNext,btnPrv;

    TextView txt_email;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<list_item> list_items;

    private int pageCount=1;
    int cat=0;
    private int login_status=0;
    private Timer myTimer;
    private AdRequest adRequest;

    private android.os.Handler handler=new android.os.Handler();


    private static final String get_data_url="http://anandpanchal.cu.cc/Quotes_Application/cat_data.php?page=";
    //private static final String get_data_url="http://192.168.0.105/Quotes_Application/cat_data.php?page=";
    private static final String cat_url="&cat=";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                 //       .setAction("Action", null).show();
                final SharedPreferences sharedPreferences=getSharedPreferences(login_config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

                if(sharedPreferences.getBoolean(login_config.LOGGEDIN_SHARED_PREF,false)) {

                    startActivity(new Intent(getApplicationContext(), newPost.class));
                }
                else
                {
                    Snackbar snackbar = Snackbar.make(view,
                            "Please Login to Post", Snackbar.LENGTH_LONG);
                    snackbar.setAction("Login", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                        }
                    });
                    snackbar.setActionTextColor(Color.RED);
                    snackbar.show();

                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //MobileAds.initialize(getApplicationContext(),"ca-app-pub-5496466424105479~4086850148");
        AdView adView=(AdView)findViewById(R.id.ad1);
        adRequest=new AdRequest.Builder()
                //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                //.addTestDevice("9149FA4061BD71C20CEAEA62DA2E5A4D")
                .build();
        adView.loadAd(adRequest);

        interstitialAd =new InterstitialAd(DisplayActivity.this);
        interstitialAd.setAdUnitId(getString(R.string.Interstitial_ads));
        //interstitialAd.loadAd(adRequest);


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                interstitialAd.loadAd(adRequest);
            }
        },30000);

        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();

                if(interstitialAd.isLoaded())
                {
                    interstitialAd.show();
                }
            }
        });


        Menu menu=navigationView.getMenu();

        View header =navigationView.getHeaderView(0);
        txt_email=(TextView)header.findViewById(R.id.txt_email);

        final SharedPreferences sharedPreferences=getSharedPreferences(login_config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        if(sharedPreferences.getBoolean(login_config.LOGGEDIN_SHARED_PREF,false)) {

            //sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            String email = sharedPreferences.getString(login_config.EMAIL_SHARED_PREF,"Not Available");
            txt_email.setText(email);
            login_status=1;
            menu.findItem(R.id.nav_logout).setTitle("Logout");
            navigationView.setNavigationItemSelectedListener(this);
        }
        else
        {
            menu.findItem(R.id.nav_logout).setTitle("Login");
            login_status=0;
            navigationView.setNavigationItemSelectedListener(this);
        }



        btnNext=(Button)findViewById(R.id.btnNext);
        btnPrv=(Button)findViewById(R.id.btnPrv);
        /*recycler view*/

        recyclerView=(RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadRecyclerViewData(0);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pageCount++;
                //Log.d("cat",String.valueOf(cat));

                if(cat==0)
                {
                    loadRecyclerViewData(0);
                }
                else
                {
                    loadRecyclerViewData(cat);
                }

                btnPrv.setEnabled(true);
            }
        });

        btnPrv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pageCount--;
                if(cat==0) {
                    loadRecyclerViewData(0);
                }
                else
                {
                    loadRecyclerViewData(cat);
                }
                if(pageCount==1)
                {
                    btnPrv.setEnabled(false);
                }
                btnNext.setEnabled(true);
            }
        });

    }

    private void loadRecyclerViewData(int cat)
    {
        Log.d("count",String.valueOf(pageCount));
        list_items=new ArrayList<>();
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Loging Data...");
        progressDialog.show();

        StringRequest stringRequest=new StringRequest(Request.Method.POST,
                get_data_url+String.valueOf(pageCount)+cat_url+String.valueOf(cat), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                if(!response.isEmpty()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("DATA");

                        if((jsonArray.length())!=0) {

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject o = jsonArray.getJSONObject(i);
                                list_item item = new list_item(
                                        o.getString("Msg")
                                );

                                list_items.add(item);
                            }

                            adapter = new MyAdapter(list_items, getApplicationContext());

                            recyclerView.setAdapter(adapter);
                        }
                        else
                        {
                            pageCount--;
                            btnNext.setEnabled(false);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        },
                new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.display, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_attitude) {

            chkbtnEnabled();
            pageCount=1;
            cat=1;
            loadRecyclerViewData(cat);

        } else if (id == R.id.nav_flirt) {

            chkbtnEnabled();
            pageCount=1;
            cat=2;
            loadRecyclerViewData(cat);

        } else if (id == R.id.nav_friendship) {

            chkbtnEnabled();
            pageCount=1;
            cat=3;
            loadRecyclerViewData(cat);

        } else if (id == R.id.nav_funny) {

            chkbtnEnabled();
            pageCount=1;
            cat=4;
            loadRecyclerViewData(cat);

        } else if (id == R.id.nav_inspirational) {

            chkbtnEnabled();
            pageCount=1;
            cat=5;
            loadRecyclerViewData(cat);

        } else if (id == R.id.nav_love) {

            chkbtnEnabled();
            pageCount=1;
            cat=6;
            loadRecyclerViewData(cat);

        } else if (id == R.id.nav_sad) {

            chkbtnEnabled();
            pageCount=1;
            cat=7;
            loadRecyclerViewData(cat);

        } else if (id == R.id.nav_shayari) {

            chkbtnEnabled();
            pageCount=1;
            cat=8;
            loadRecyclerViewData(cat);

        } else if (id == R.id.nav_truthful_words) {

            chkbtnEnabled();
            pageCount=1;
            cat=9;
            loadRecyclerViewData(cat);

        } else if (id == R.id.nav_well_said) {

            chkbtnEnabled();
            pageCount=1;
            cat=10;
            loadRecyclerViewData(cat);

        } else if (id == R.id.nav_all){

            chkbtnEnabled();
            pageCount=1;
            cat=0;
            loadRecyclerViewData(cat);

        }
        else if(id == R.id.nav_feedback)
        {
            startActivity(new Intent(getApplicationContext(),FeedBack.class));
        }
        else if(id == R.id.nav_about)
        {
            startActivity(new Intent(getApplicationContext(),About.class));
        }
        else if(id == R.id.nav_logout)
        {
            if(login_status==1) {
                logout();
            }
            else {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void chkbtnEnabled()
    {
        if(!btnNext.isEnabled())
        {
            btnNext.setEnabled(true);
            btnPrv.setEnabled(false);
        }
    }


    public void logout()
    {
        final SharedPreferences sharedPreferences=getSharedPreferences(login_config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        if(sharedPreferences.getBoolean(login_config.LOGGEDIN_SHARED_PREF,false)) {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Are you sure you want to logout?");
            alertDialogBuilder.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {

                            //Getting out sharedpreferences
                            //SharedPreferences preferences = getSharedPreferences(login_config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                            //Getting editor
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            //Puting the value false for loggedin
                            editor.putBoolean(login_config.LOGGEDIN_SHARED_PREF, false);

                            //Putting blank value to email
                            editor.putString(login_config.EMAIL_SHARED_PREF, "");

                            //Saving the sharedpreferences
                            editor.commit();

                            //Starting login activity
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                            finishAffinity();
                        }
                    });

            alertDialogBuilder.setNegativeButton("No",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {

                        }
                    });

            //Showing the alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    @Override
    protected void onResume() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                interstitialAd.loadAd(adRequest);
            }
        },300000);
        super.onResume();
    }
}
