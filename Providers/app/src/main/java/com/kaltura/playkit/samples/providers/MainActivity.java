package com.kaltura.playkit.samples.providers;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.LinearLayout;

import com.kaltura.playkit.PKMediaConfig;
import com.kaltura.playkit.PKMediaEntry;
import com.kaltura.playkit.PlayKitManager;
import com.kaltura.playkit.Player;
import com.kaltura.playkit.backend.base.OnMediaLoadCompletion;
import com.kaltura.playkit.backend.ovp.KalturaOvpMediaProvider;
import com.kaltura.playkit.backend.ovp.SimpleOvpSessionProvider;
import com.kaltura.playkit.connect.ResultElement;
import com.kaltura.playkit.samples.basicpluginssetup.R;


public class MainActivity extends AppCompatActivity {

    //Put here your provider base url
    private static final String PROVIDER_BASE_URL = "your_provider_base_url";
    //Put here your partner id.
    private static final int PARTNER_ID = 0;
    //Put here your KS.
    private static final String KS = "your_ks";
    //Put here your entry id.
    private static final String ENTRY_ID = "your_entry_id";

    private Player player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Create instance of the player.
        player = PlayKitManager.loadPlayer(this, null);

        //Get the layout, where the player view will be placed.
        LinearLayout layout = (LinearLayout) findViewById(R.id.player_root);
        //Add player view to the layout.
        layout.addView(player.getView());

        //Create media provider and request media.
        createMediaProvider();
    }

    /**
     * Create ovp media provider, that will request for media entry.
     */
    private void createMediaProvider() {

        //Initialize provider.
        KalturaOvpMediaProvider mediaProvider = new KalturaOvpMediaProvider();

        //Initialize ovp session provider.
        SimpleOvpSessionProvider sessionProvider = new SimpleOvpSessionProvider(PROVIDER_BASE_URL, PARTNER_ID, KS);

        //Set entry id for the session provider.
        mediaProvider.setEntryId(ENTRY_ID);

        //Set session provider to media provider.
        mediaProvider.setSessionProvider(sessionProvider);

        //Load media from media provider.
        mediaProvider.load(new OnMediaLoadCompletion() {
            @Override
            public void onComplete(ResultElement<PKMediaEntry> response) {
                //When response received check if it was successful.
                if (response.isSuccess()) {
                    //If so, prepare player with received PKMediaEntry.
                    preparePlayer(response.getResponse());
                } else {
                    //If response was not successful print it to console with error message.
                    String error = "failed to fetch media data: " + (response.getError() != null ? response.getError().getMessage() : "");
                    Log.e(MainActivity.class.getSimpleName(), error);
                }
            }
        });
    }

    /**
     * Prepare player and start playback.
     * @param mediaEntry - media entry we received from media provider.
     */
    private void preparePlayer(final PKMediaEntry mediaEntry) {
        //The preparePlayer is called from another thread. So first be shure
        //that we are running on ui thread.
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //Initialize empty mediaConfig object.
                PKMediaConfig mediaConfig = new PKMediaConfig();

                //Set media entry we received from provider.
                mediaConfig.setMediaEntry(mediaEntry);

                //Prepare player with media configurations.
                player.prepare(mediaConfig);

                //Start playback.
                player.play();
            }
        });

    }

}