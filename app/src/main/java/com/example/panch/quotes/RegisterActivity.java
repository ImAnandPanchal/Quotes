package com.example.panch.quotes;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText edEmail, edPass, edRePass;
    Button btnReg;
    final String reg_url = "http://anandpanchal.cu.cc/Quotes_Application/register.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edEmail = (EditText) findViewById(R.id.txtEmail);
        edPass = (EditText) findViewById(R.id.txtPassword);
        btnReg = (Button) findViewById(R.id.btnRegister);
        edRePass = (EditText) findViewById(R.id.txtRePassword);

        AdView adView = (AdView) findViewById(R.id.ad1);
        AdRequest adRequest = new AdRequest.Builder()
                //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                //.addTestDevice("9149FA4061BD71C20CEAEA62DA2E5A4D")
                .build();
        adView.loadAd(adRequest);


        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String email = edEmail.getText().toString().trim();
                final String password = edPass.getText().toString().trim();
                final String Re_password = edRePass.getText().toString().trim();

                if (email.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Enter email", Toast.LENGTH_LONG).show();
                    return;
                }
                if (!edEmail.getText().toString().matches(login_config.validation_email)) {
                    Toast.makeText(getApplicationContext(), "Enter valid Email ID", Toast.LENGTH_LONG).show();
                    return;
                }
                if (password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Enter password", Toast.LENGTH_LONG).show();
                    return;
                }

                final ProgressDialog progressDialog = ProgressDialog.show(RegisterActivity.this, "Registration", "Please wait...", false, false);

                StringRequest stringRequest = new StringRequest(Request.Method.POST, reg_url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                progressDialog.dismiss();
                                if (response.equalsIgnoreCase("success")) {
                                    Toast.makeText(getApplicationContext(), "Verification link is send on email", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                    finishAffinity();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Already register with this email", Toast.LENGTH_LONG).show();
                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {

                        Map<String, String> params = new HashMap<String, String>();

                        params.put("email", email);
                        params.put("password", password);

                        return params;

                    }
                };

                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(stringRequest);
            }

            ;
        });
    }
}
