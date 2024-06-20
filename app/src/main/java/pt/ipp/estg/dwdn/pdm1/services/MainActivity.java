package pt.ipp.estg.dwdn.pdm1.services;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Button btnService;
    private OdometerService odometerService;
    private boolean bound = false;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            OdometerService.OdometerBinder odometerBinder = (OdometerService.OdometerBinder) service;
            odometerService = odometerBinder.getOdometer();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnService = findViewById(R.id.button);
        btnService.setOnClickListener(this::onClick);
        displayDistance();
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.button:
                Intent intent = new Intent(this, ExampleService.class);
                intent.putExtra(ExampleService.EXTRA_MESSAGE, getResources().getString(R.string.response));
                startService(intent);
                break;
        }
    }
    protected void onStart(){
        super.onStart();
        Intent intent = new Intent(this, OdometerService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }
    protected void onStop(){
        super.onStop();
        if(bound){
            unbindService(serviceConnection);
            bound = false;
        }
    }
    private void displayDistance(){
        final TextView distanceView = findViewById(R.id.txt_distance);
        final Handler handler = new Handler(Looper.myLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                double distance = 0.0;
                if(bound && odometerService != null){
                    distance = odometerService.getDistance();
                }
                String distanceStr = String.format(Locale.getDefault(), "%15, .2f KM", distance);
                distanceView.setText(distanceStr);
                handler.postDelayed(this, 2000);
            }
        });
    }
}