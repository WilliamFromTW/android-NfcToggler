package nfc.inmethod.nfctoggler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Icon;
import android.nfc.NfcAdapter;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;

import static java.lang.Thread.sleep;

public class QuickSettingsService extends TileService {
    private final String LOG_TAG = "QuickSettingsService";
    private static boolean running = false;
    private static Thread t;

    @Override
    public void onTileAdded() {
        Log.d(LOG_TAG, "onTileAdded");
    }

    @Override
    public void onTileRemoved() {
        Log.d(LOG_TAG, "onTileRemoved");
    }

    private void correctIconStatus() {
        Log.d(LOG_TAG, "correctIconStatus  current stat = " + Integer.toString(getQsTile().getState()));
        Icon icon;
        if (NfcController.checkNfcEnableStatus(this)) {
            icon = Icon.createWithResource(getApplicationContext(), R.drawable.ic_nfc_on);
            getQsTile().setState(Tile.STATE_ACTIVE);
        } else {
            icon = Icon.createWithResource(getApplicationContext(), R.drawable.ic_nfc_off);
            getQsTile().setState(Tile.STATE_INACTIVE);
        }
        getQsTile().setIcon(icon);
        getQsTile().updateTile();
    }

    @Override
    public void onClick() {
        Log.d(LOG_TAG, "onClick state = " + Integer.toString(getQsTile().getState()));
        Icon icon;
        if (NfcController.checkNfcEnableStatus(this)) {
            NfcController.enableNfc(false, this);
            icon = Icon.createWithResource(getApplicationContext(), R.drawable.ic_nfc_off);
            getQsTile().setState(Tile.STATE_INACTIVE);
        } else {
            NfcController.enableNfc(true, this);
            icon = Icon.createWithResource(getApplicationContext(), R.drawable.ic_nfc_on);
            getQsTile().setState(Tile.STATE_ACTIVE);
        }
        getQsTile().setIcon(icon);
        getQsTile().updateTile();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                SwitchingActivity.updateAllWidgets(ApplicationContextHelper.getContext(), R.layout.nfc_toggler_widget, NfcTogglerWidget.class);
            }
        });
        thread.start();
    }

    @Override
    public void onStartListening() {
        Log.d(LOG_TAG, "onStartListening");
        if (running) return;
        running = true;
        t = new Thread() {
            BroadcastReceiver mReceiver;

            @Override
            public void run() {

                super.run();

                mReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String action = intent.getAction();
                        if (NfcAdapter.ACTION_ADAPTER_STATE_CHANGED.equals(action)) {
                            getStatus(intent.getIntExtra(NfcAdapter.EXTRA_ADAPTER_STATE, NfcAdapter.STATE_OFF));
                        }
                    }

                    private void getStatus(int intExtra) {
                        switch (intExtra) {
                            case NfcAdapter.STATE_OFF:
                                correctIconStatus();
                                break;
                            case NfcAdapter.STATE_ON:
                                correctIconStatus();
                                break;
                            case NfcAdapter.STATE_TURNING_OFF:
                                break;
                            case NfcAdapter.STATE_TURNING_ON:
                                break;
                        }
                        String a = intExtra + "";
                        Log.d(LOG_TAG, a);
                        synchronized (t) {
                            if (t != null && t.getState() == State.WAITING) {
                                t.notify();
                            }
                        }
                    }
                };
                IntentFilter intentFilter = new IntentFilter("android.nfc.action.ADAPTER_STATE_CHANGED");
                registerReceiver(mReceiver, intentFilter);

                while (running) {
                    Log.i(LOG_TAG, "Service running!!");
                    synchronized (this) {
                        try {
                            wait();
//                        sleep(360000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                Log.i(LOG_TAG, "Service stop!!");
                unregisterReceiver(mReceiver);
            }
        };
        t.start();
    }

    @Override
    public void onStopListening() {
        running = false;
        Log.d(LOG_TAG, "onStopListening");
    }
}
