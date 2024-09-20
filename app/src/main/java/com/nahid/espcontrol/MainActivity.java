package com.nahid.espcontrol;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {


    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    ImageView btn;
    TextView status_tv;
    int STATUS = 0;
    RelativeLayout progress_layout,main_layout;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        wedstatus("http://192.168.4.1/status");

        btn = findViewById(R.id.btn_image);
        status_tv = findViewById(R.id.status_tv);
        progress_layout = findViewById(R.id.progress_layout);
        main_layout = findViewById(R.id.main_layout);



        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (STATUS == 1) {
                    STATUS = 0;
                    btn.setImageResource(R.drawable.ic_off);
                    status_tv.setText("Status: OFF");
                    webaction("http://192.168.4.1/off");
                } else {
                    STATUS = 1;
                    btn.setImageResource(R.drawable.ic_on);
                    status_tv.setText("Status: ON");
                    webaction("http://192.168.4.1/on");
                }

            }
        });

    }

    private void wedstatus(String url) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                            if (status.equals("ON")) {
                                STATUS = 1;
                                btn.setImageResource(R.drawable.ic_on);
                                status_tv.setText("Status: ON");
                            } else {
                                STATUS = 0;
                                btn.setImageResource(R.drawable.ic_off);
                                status_tv.setText("Status: OFF");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                        }

                        progress_layout.setVisibility(View.GONE);
                        main_layout.setVisibility(View.VISIBLE);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //statusTextView.setText("Error: " + error.getMessage());
                        showDialog(MainActivity.this);

                    }
                });

        // Add the request to the RequestQueue.
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }



    private void webaction(String url){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progress_layout.setVisibility(View.GONE);
                        main_layout.setVisibility(View.VISIBLE);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                showDialog(MainActivity.this);

            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }


    public void showDialog(Activity activity){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.network_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        Button refash = dialog.findViewById(R.id.refash_btn);
        Button exit = dialog.findViewById(R.id.exit_btn);

        refash.setOnClickListener(view -> {
            main_layout.setVisibility(View.GONE);
            progress_layout.setVisibility(View.VISIBLE);
            wedstatus("http://192.168.4.1/status");
            dialog.dismiss();
        });

        exit.setOnClickListener(view -> finish());

    }

}
