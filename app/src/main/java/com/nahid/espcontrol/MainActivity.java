package com.nahid.espcontrol;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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
    CardView contron_btn;
    ImageView icon;
    TextView status_tv;
    int STATUS = 0;



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

        icon = findViewById(R.id.btn_image);
        contron_btn = findViewById(R.id.control_button);
        status_tv = findViewById(R.id.status_tv);



        contron_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (STATUS == 1) {
                    STATUS = 0;
                    icon.setImageResource(R.drawable.ic_off);
                    status_tv.setText("Status: OFF");
                    webaction("http://192.168.4.1/off");
                } else {
                    STATUS = 1;
                    icon.setImageResource(R.drawable.ic_on);
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
                                icon.setImageResource(R.drawable.ic_on);
                                status_tv.setText("Status: ON");
                            } else {
                                STATUS = 0;
                                icon.setImageResource(R.drawable.ic_off);
                                status_tv.setText("Status: OFF");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();


                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //statusTextView.setText("Error: " + error.getMessage());
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Wifi Not Connect")
                                .setCancelable(false)
                                .setMessage("Connect 'Espap' wifi then refrash or you can exit this app ")
                                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                }).setNeutralButton("Refrash", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        wedstatus("http://192.168.4.1/status");
                                    }
                                })
                                // A null listener allows the button to dismiss the dialog and take no further action.
                                .setIcon(R.drawable.wifi_disconnect)
                                .show();

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

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Wifi Not Connect")
                        .setCancelable(false)
                        .setMessage("Connect 'Espap' wifi then refrash or you can exit this app ")
                        .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).setNeutralButton("Refrash", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                wedstatus("http://192.168.4.1/status");
                            }
                        })
                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setIcon(R.drawable.wifi_disconnect)
                        .show();

            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

}
