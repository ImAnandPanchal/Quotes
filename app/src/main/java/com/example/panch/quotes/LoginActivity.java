package com.example.panch.quotes;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private Button btnLogin,btnSkip;
    private TextView tvRegister,tvReSendVerificationLink,tvForgotPassword;
    private EditText txtEmail,txtPassword;

    private boolean loggedIn=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tvRegister = (TextView)findViewById(R.id.tvRegister);
        txtEmail = (EditText)findViewById(R.id.txtEmail);
        txtPassword = (EditText)findViewById(R.id.txtPassword);
        btnLogin = (Button)findViewById(R.id.btnLogin);
        btnSkip = (Button)findViewById(R.id.btnSkip);
        tvReSendVerificationLink=(TextView) findViewById(R.id.tvRe_send_verification_link);
        tvForgotPassword=(TextView)findViewById(R.id.tvForgotPassword);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                final String email=txtEmail.getText().toString().trim();
                final String password=txtPassword.getText().toString().trim();

                if(email.isEmpty())
                {
                    Toast.makeText(getApplicationContext(),"Enter email",Toast.LENGTH_LONG).show();
                    return;
                }
                if(password.isEmpty())
                {
                    Toast.makeText(getApplicationContext(),"Enter password",Toast.LENGTH_LONG).show();
                    return;
                }

                final ProgressDialog progressDialog=ProgressDialog.show(LoginActivity.this,"Login","Please wait...",false,false);

                StringRequest stringRequest=new StringRequest(Request.Method.POST, login_config.LOGIN_URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                progressDialog.dismiss();
                                if(response.equalsIgnoreCase(login_config.LOGIN_SUCCESS))
                                {
                                    SharedPreferences sharedPreferences= LoginActivity.this.getSharedPreferences(login_config.SHARED_PREF_NAME,Context.MODE_PRIVATE);

                                    SharedPreferences.Editor editor=sharedPreferences.edit();
                                    editor.putBoolean(login_config.LOGGEDIN_SHARED_PREF,true);
                                    editor.putString(login_config.EMAIL_SHARED_PREF,email);

                                    editor.commit();

                                    startActivity(new Intent(getApplicationContext(),DisplayActivity.class));
                                    finishAffinity();
                                }
                                else
                                {
                                    if(response.equalsIgnoreCase("wrong")) {
                                        Toast.makeText(getApplicationContext(), "Invalid username or password", Toast.LENGTH_LONG).show();

                                    }
                                    else
                                    {
                                        Toast.makeText(getApplicationContext(),"Your Email ID not Verified..",Toast.LENGTH_LONG).show();
                                        tvReSendVerificationLink.setVisibility(View.VISIBLE);
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
                        }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {

                        Map<String, String> params =new HashMap<String, String>();

                        params.put(login_config.KEY_EMAIL,email);
                        params.put(login_config.KEY_PASSWORD,password);

                        return params;

                    }
                };

                RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(stringRequest);
            }
        });

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(),DisplayActivity.class));
                finishAffinity();
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
            }
        });

        tvReSendVerificationLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),ReSendVerificationLink.class));
            }
        });

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences=getSharedPreferences(login_config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        loggedIn =sharedPreferences.getBoolean(login_config.LOGGEDIN_SHARED_PREF,false);

        if(loggedIn)
        {
            startActivity(new Intent(getApplicationContext(),DisplayActivity.class));
        }
    }

}
