package nfc.inmethod.nfctoggler;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class BootUpService extends Service {
    private final String TAG = "BootUpService";
    private static boolean running = false;
    private static Thread t;
    private static final String ANDROID_CHANNEL_ID = "nfc.inmethod.nfctoggler.Channel";
    private static final int NOTIFICATION_ID = 555;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (running) return START_STICKY;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder builder = new Notification.Builder(this, ANDROID_CHANNEL_ID)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("BootUpService Running")
                    .setAutoCancel(true);
            Notification notification = builder.build();
            startForeground(NOTIFICATION_ID, notification);
        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("BootUpService is Running...")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);
            Notification notification = builder.build();
            startForeground(NOTIFICATION_ID, notification);
        }
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
                                NfcWidgetSwitchingActivity.updateAllWidgets(ApplicationContextHelper.getContext(), R.layout.nfc_toggler_widget, NfcTogglerWidget.class);
                                break;
                            case NfcAdapter.STATE_ON:
                                NfcWidgetSwitchingActivity.updateAllWidgets(ApplicationContextHelper.getContext(), R.layout.nfc_toggler_widget, NfcTogglerWidget.class);
                                break;
                            case NfcAdapter.STATE_TURNING_OFF:
                                break;
                            case NfcAdapter.STATE_TURNING_ON:
                                break;
                        }
                        synchronized (t) {
                            if (t != null && t.getState() == State.WAITING) {
                                t.notify();
                            }
                        }
                    }
                };
                IntentFilter intentFilter = new IntentFilter("android.nfc.action.ADAPTER_STATE_CHANGED");
                registerReceiver(mReceiver, intentFilter);
                // first
                NfcWidgetSwitchingActivity.updateAllWidgets(ApplicationContextHelper.getContext(), R.layout.nfc_toggler_widget, NfcTogglerWidget.class);

                while (running) {
                    Log.i(TAG, "Service running!!");
                    try {
                        synchronized (this) {
                            wait();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                unregisterReceiver(mReceiver);
                Log.i(TAG, "Service stop!!");
            }
        };
        t.start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        running = false;
    }
}
