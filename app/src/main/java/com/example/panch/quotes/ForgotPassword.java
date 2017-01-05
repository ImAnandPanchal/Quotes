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

public class ForgotPassword extends AppCompatActivity {

    EditText edEmail;
    Button btnSubmit;

    private String forgot_url="http://anandpanchal.cu.cc/Quotes_Application/forgot_password.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        btnSubmit=(Button)findViewById(R.id.btnFsubmit);
        edEmail = (EditText)findViewById(R.id.fEmail);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(edEmail.getText())) {
                    Toast.makeText(getApplicationContext(), "Enter Email ID", Toast.LENGTH_LONG).show();
                    return;
                }
                if(!edEmail.getText().toString().matches(login_config.validation_email))
                {
                    Toast.makeText(getApplicationContext(), "Enter valid Email ID", Toast.LENGTH_LONG).show();
                    return;
                }

                final ProgressDialog progressDialog = ProgressDialog.show(ForgotPassword.this, "Submitting", "Please wait...", false, false);

                StringRequest stringRequest = new StringRequest(Request.Method.POST, forgot_url ,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                progressDialog.dismiss();

                                if(response.equalsIgnoreCase("success"))
                                {
                                    Toast.makeText(getApplicationContext(),"Email has been sent",Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                                    finishAffinity();
                                }
                                else
                                {
                                    Toast.makeText(getApplicationContext(),"Email ID is not valid",Toast.LENGTH_LONG).show();
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
                RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(stringRequest);
            }
        });
    }
}
