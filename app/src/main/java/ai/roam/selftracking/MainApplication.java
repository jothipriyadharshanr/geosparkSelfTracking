package ai.roam.selftracking;

import android.app.Application;

import com.geospark.lib.GeoSpark;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        GeoSpark.initialize(this, "e0f11152ba91ff4608142dae83c315484f78b3d776d4c2239a2ee0c7f57c2864");
    }
}
