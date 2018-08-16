package nfc.inmethod.nfctoggler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class BootUpReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        /* 同一個接收者可以收多個不同行為的廣播
           所以可以判斷收進來的行為為何，再做不同的動作 */
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            /* 收到廣播後開啟目的Service */
            Intent startServiceIntent = new Intent(context, BootUpService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(startServiceIntent);

            } else {
                context.startService(startServiceIntent);
            }
        }
    }
}
