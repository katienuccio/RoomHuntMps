package com.KatieNuccioGmailCom.RoomHuntMps;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.KatieNuccioGmailCom.RoomHuntMps.estimote.BeaconID;
import com.KatieNuccioGmailCom.RoomHuntMps.estimote.EstimoteCloudBeaconDetails;
import com.KatieNuccioGmailCom.RoomHuntMps.estimote.EstimoteCloudBeaconDetailsFactory;
import com.KatieNuccioGmailCom.RoomHuntMps.estimote.NearestBeaconManager;
import com.KatieNuccioGmailCom.RoomHuntMps.estimote.ProximityContentManager;
import com.estimote.sdk.SystemRequirementsChecker;
import com.estimote.sdk.cloud.model.Color;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


//
// Running into any issues? Drop us an email to: contact@estimote.com
//

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final Map<Color, Integer> BACKGROUND_COLORS = new HashMap<>();

    static {
        BACKGROUND_COLORS.put(Color.ICY_MARSHMALLOW, android.graphics.Color.rgb(109, 170, 199));
        BACKGROUND_COLORS.put(Color.BLUEBERRY_PIE, android.graphics.Color.rgb(98, 84, 158));
        BACKGROUND_COLORS.put(Color.MINT_COCKTAIL, android.graphics.Color.rgb(155, 186, 160));
    }

    private static final int BACKGROUND_COLOR_NEUTRAL = android.graphics.Color.rgb(160, 169, 172);

    private ProximityContentManager proximityContentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        proximityContentManager = new ProximityContentManager(this,
                Arrays.asList(
                        new BeaconID("B9407F30-F5F8-466E-AFF9-25556B57FE6D", 2, 1),
                        new BeaconID("B9407F30-F5F8-466E-AFF9-25556B57FE6D", 2, 2),
                        new BeaconID("B9407F30-F5F8-466E-AFF9-25556B57FE6D", 2, 3)),
                new EstimoteCloudBeaconDetailsFactory());
        proximityContentManager.setListener(new ProximityContentManager.Listener() {
            @Override
            public void onContentChanged(Object content) {
                String text;
                String text2;
                Integer backgroundColor;
                NearestBeaconManager nearestBeaconManager = new NearestBeaconManager(proximityContentManager.getContext(), proximityContentManager.getBeaconIDs());
                if (content != null) {
                    EstimoteCloudBeaconDetails beaconDetails = (EstimoteCloudBeaconDetails) content;
                    text = "You're in " + beaconDetails.getBeaconName() + "'s range!";
                    text2 = "Distance to nearest beacon: " ;
                    backgroundColor = BACKGROUND_COLORS.get(beaconDetails.getBeaconColor());
                } else {
                    text = "No beacons in range.";
                    text2 = "FUCK " + content;
                    backgroundColor = null;
                }
                ((TextView) findViewById(R.id.textView)).setText(text);
                ((TextView) findViewById(R.id.textView2)).setText(text2);
                findViewById(R.id.relativeLayout).setBackgroundColor(
                        backgroundColor != null ? backgroundColor : BACKGROUND_COLOR_NEUTRAL);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!SystemRequirementsChecker.checkWithDefaultDialogs(this)) {
            Log.e(TAG, "Can't scan for beacons, some pre-conditions were not met");
            Log.e(TAG, "Read more about what's required at: http://estimote.github.io/Android-SDK/JavaDocs/com/estimote/sdk/SystemRequirementsChecker.html");
            Log.e(TAG, "If this is fixable, you should see a popup on the app's screen right now, asking to enable what's necessary");
        } else {
            Log.d(TAG, "Starting ProximityContentManager content updates");
            proximityContentManager.startContentUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "Stopping ProximityContentManager content updates");
        proximityContentManager.stopContentUpdates();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        proximityContentManager.destroy();
    }
}
