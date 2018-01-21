package com.example.panch.quotes;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class DisplayPost extends AppCompatActivity {

    private TextView txt_msg;
    private ClipboardManager clipboardManager;
    private ClipData clipData;
    private Button btnCopy, btnShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_post);

        txt_msg = (TextView) findViewById(R.id.txtMsgData);

        txt_msg.setText(getIntent().getStringExtra("msg"));
        btnCopy = (Button) findViewById(R.id.btnCopy);
        btnShare = (Button) findViewById(R.id.btnShare);

        AdView adView = (AdView) findViewById(R.id.ad1);
        AdRequest adRequest = new AdRequest.Builder()
                //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                //.addTestDevice("9149FA4061BD71C20CEAEA62DA2E5A4D")
                .build();
        adView.loadAd(adRequest);

        clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clipData = ClipData.newPlainText("msg", txt_msg.getText());
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(getApplicationContext(), "Text Copied", Toast.LENGTH_SHORT).show();
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.putExtra(Intent.EXTRA_TEXT, txt_msg.getText());
                sharingIntent.setType("text/plain");
                startActivity(sharingIntent.createChooser(sharingIntent, "Share via"));
            }
        });

    }
}
