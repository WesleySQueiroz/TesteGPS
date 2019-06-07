package com.example.wqueiroz.mapsteste;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class StartActivity extends AppCompatActivity {

    private Button btn_Maps;

    private String txtLatitude;

    private String txtLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        btn_Maps = (Button) findViewById(R.id.btn_Maps);

        pedirPermissoes();

        IniciarGPS iniciarGPS = new IniciarGPS();

        new Thread(iniciarGPS).start();

        btn_Maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(txtLatitude == null || txtLongitude == null){

                    configurarServico();

                    if (txtLongitude != null || txtLatitude != null){
                        Bundle bundle = new Bundle();
                        bundle.putString("latitude", txtLatitude);
                        bundle.putString("longitude", txtLongitude);
                        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                }
                else{
                    Bundle bundle = new Bundle();
                    bundle.putString("latitude", txtLatitude);
                    bundle.putString("longitude", txtLongitude);
                    Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
    }

    private void pedirPermissoes() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        else
            configurarServico();
    }

    public void configurarServico(){
        try {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    atualizar(location);
                }

                public void onStatusChanged(String provider, int status, Bundle extras) { }

                public void onProviderEnabled(String provider) { }

                public void onProviderDisabled(String provider) { }
            };
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }catch(SecurityException ex){
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void atualizar(Location location) {
        Double latPoint = location.getLatitude();
        Double lngPoint = location.getLongitude();

        txtLatitude = latPoint.toString();
        txtLongitude = lngPoint.toString();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    configurarServico();
                } else {
                    Toast.makeText(this, "Não vai funcionar!!!", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        pedirPermissoes();

        configurarServico();

    }

    public void VerificaGPS(){
        if(txtLatitude != null && txtLongitude != null ){
            btn_Maps.setEnabled(true);
        }
    }

    class IniciarGPS implements Runnable{

        @Override
        public void run() {
            if(txtLatitude != null && txtLongitude != null){

                btn_Maps.post(new Runnable() {
                    @Override
                    public void run() {
                        btn_Maps.setEnabled(true);
                    }
                });
            }
        }
    }


}


