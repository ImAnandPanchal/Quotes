package com.example.panch.quotes;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

public class newPost extends AppCompatActivity {

    Spinner spinner;
    private String email = "", cat = "";
    Button btnPost;
    EditText ed_msg;

    private String post_url = "http://anandpanchal.cu.cc/Quotes_Application/post.php";

    ArrayAdapter<CharSequence> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        final SharedPreferences sharedPreferences = getSharedPreferences(login_config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        if (sharedPreferences.getBoolean(login_config.LOGGEDIN_SHARED_PREF, false)) {

            email = sharedPreferences.getString(login_config.EMAIL_SHARED_PREF, "Not Available");

        }

        spinner = (Spinner) findViewById(R.id.cat_menu);
        arrayAdapter = ArrayAdapter.createFromResource(this, R.array.cat_array, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        btnPost = (Button) findViewById(R.id.btnPost);
        ed_msg = (EditText) findViewById(R.id.txtMsg);

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(ed_msg.getText())) {
                    Toast.makeText(getApplicationContext(), "Enter Message..", Toast.LENGTH_LONG).show();
                    return;
                }

                //Toast.makeText(getApplicationContext(),String.valueOf(spinner.getSelectedItemId()),Toast.LENGTH_LONG).show();
                if (spinner.getSelectedItemId() == 0) {
                    Toast.makeText(getApplicationContext(), "Select Category..", Toast.LENGTH_LONG).show();
                    return;
                }
                //Toast.makeText(getApplicationContext(),ed_msg.getText().toString(),Toast.LENGTH_LONG).show();

                final ProgressDialog progressDialog = ProgressDialog.show(newPost.this, "Post", "Please wait...", false, false);

                StringRequest stringRequest = new StringRequest(Request.Method.POST, post_url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                progressDialog.dismiss();
                                if (response.equalsIgnoreCase("success")) {
                                    //Toast.makeText(getApplicationContext(),"",Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(getApplicationContext(), DisplayActivity.class));
                                    finishAffinity();
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

                        params.put("email", email);
                        params.put("msg", (ed_msg.getText().toString()));
                        params.put("cat", (String.valueOf(spinner.getSelectedItemId())));

                        return params;

                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(stringRequest);

            }
        });

    }

}
