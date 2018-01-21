package com.example.panch.quotes;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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

import java.util.HashMap;
import java.util.Map;

public class ReSendVerificationLink extends AppCompatActivity {

    private EditText edEmail;
    private Button btn_Re_Send;

    private final String send_url = "http://anandpanchal.cu.cc/Quotes_Application/re_send_verification_link.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_re_send_verification_link);

        edEmail = (EditText) findViewById(R.id.txtEmail);
        btn_Re_Send = (Button) findViewById(R.id.btn_send);

        btn_Re_Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(edEmail.getText())) {
                    Toast.makeText(getApplicationContext(), "Enter email", Toast.LENGTH_LONG).show();
                    return;
                }
                if (!edEmail.getText().toString().matches(login_config.validation_email)) {
                    Toast.makeText(getApplicationContext(), "Enter valid Email ID", Toast.LENGTH_LONG).show();
                    return;
                }

                final ProgressDialog progressDialog = ProgressDialog.show(ReSendVerificationLink.this, "Send", "Please wait...", false, false);

                StringRequest stringRequest = new StringRequest(Request.Method.POST, send_url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                progressDialog.dismiss();
                                if (response.equalsIgnoreCase("success")) {
                                    //Toast.makeText(getApplicationContext(),"",Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                    finishAffinity();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Check your email id", Toast.LENGTH_LONG).show();
                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                progressDialog.dismiss();

                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {

                        Map<String, String> params = new HashMap<String, String>();

                        params.put("email", edEmail.getText().toString());
                        return params;

                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(stringRequest);
            }
        });
    }
}
