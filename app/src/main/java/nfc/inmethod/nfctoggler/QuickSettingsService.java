package nfc.inmethod.nfctoggler;

import android.graphics.drawable.Icon;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;

public class QuickSettingsService  extends TileService {
    private final String LOG_TAG = "QuickSettingsService";

    @Override
    public void onTileAdded() {
        Log.d(LOG_TAG, "onTileAdded");
    }

    @Override
    public void onTileRemoved() {
        Log.d(LOG_TAG, "onTileRemoved");
    }

    @Override
    public void onClick() {
        Log.d(LOG_TAG, "onClick state = " + Integer.toString(getQsTile().getState()));
        Icon icon;
        if (NfcController.checkNfcEnableStatus(this)) {
            NfcController.enableNfc(false,this);
            icon =  Icon.createWithResource(getApplicationContext(), R.drawable.ic_nfc_off);
            getQsTile().setState(Tile.STATE_INACTIVE);
        } else {
            NfcController.enableNfc(true,this);
            icon = Icon.createWithResource(getApplicationContext(), R.drawable.ic_nfc_on);
            getQsTile().setState(Tile.STATE_ACTIVE);
        }
        getQsTile().setIcon(icon);
        getQsTile().updateTile();
    }

    @Override
    public void onStartListening () {
        Log.d(LOG_TAG, "onStartListening");
    }

    @Override
    public void onStopListening () {
        Log.d(LOG_TAG, "onStopListening");
    }
}
