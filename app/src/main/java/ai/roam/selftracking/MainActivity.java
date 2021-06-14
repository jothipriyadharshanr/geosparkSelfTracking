package ai.roam.selftracking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.geospark.lib.GeoSpark;
import com.geospark.lib.GeoSparkTrackingMode;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private RadioGroup mRadioGroup;
    private Button btnStartTracking, btnStopTracking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GeoSpark.disableBatteryOptimization();
        btnStartTracking = findViewById(R.id.btnStartTracking);
        btnStopTracking = findViewById(R.id.btnStopTracking);
        btnStartTracking.setOnClickListener(this);
        btnStopTracking.setOnClickListener(this);
        GeoSpark.notificationOpenedHandler(getIntent());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnStartTracking:
                tracking();
                break;
            case R.id.btnStopTracking:
                stopTracking();
                break;
        }
    }

    private void tracking() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            checkPermissionsQ();
        } else {
            checkPermissions();
        }
    }

    private void checkPermissions() {
        if (!GeoSpark.checkLocationPermission()) {
            GeoSpark.requestLocationPermission(this);
        } else if (!GeoSpark.checkLocationServices()) {
            GeoSpark.requestLocationServices(this);
        } else {
            startTracking();
        }
    }

    private void checkPermissionsQ() {
        if (!GeoSpark.checkLocationPermission()) {
            GeoSpark.requestLocationPermission(this);
        } else if (!GeoSpark.checkBackgroundLocationPermission()) {
            GeoSpark.requestBackgroundLocationPermission(this);
        } else if (!GeoSpark.checkLocationServices()) {
            GeoSpark.requestLocationServices(this);
        } else {
            startTracking();
        }
    }

    private void startTracking() {
        GeoSparkTrackingMode geoSparkTrackingMode = new GeoSparkTrackingMode.Builder(30)
                    .setDesiredAccuracy(GeoSparkTrackingMode.DesiredAccuracy.HIGH)
                    .build();
        GeoSpark.startSelfTracking(geoSparkTrackingMode);
        trackingStatus();
    }

    private void stopTracking() {
        GeoSpark.stopTracking();
        trackingStatus();
    }

    private void trackingStatus() {
        if (GeoSpark.isLocationTracking()) {
            startService(new Intent(this, ForegroundService.class));
            btnStartTracking.setBackground(getResources().getDrawable(R.drawable.bg_button_disable));
            btnStopTracking.setBackground(getResources().getDrawable(R.drawable.bg_button_enable));
            btnStartTracking.setEnabled(false);
            btnStopTracking.setEnabled(true);
        } else {
            stopService(new Intent(this, ForegroundService.class));
            btnStartTracking.setBackground(getResources().getDrawable(R.drawable.bg_button_enable));
            btnStopTracking.setBackground(getResources().getDrawable(R.drawable.bg_button_disable));
            btnStartTracking.setEnabled(true);
            btnStopTracking.setEnabled(false);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case GeoSpark.REQUEST_CODE_LOCATION_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    tracking();
                } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, "Location permission required", Toast.LENGTH_SHORT).show();
                }
                break;
            case GeoSpark.REQUEST_CODE_BACKGROUND_LOCATION_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    tracking();
                } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, "Background Location permission required", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GeoSpark.REQUEST_CODE_LOCATION_ENABLED) {
            tracking();
        }
    }
}