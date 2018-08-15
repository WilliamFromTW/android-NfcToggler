package nfc.inmethod.nfctoggler;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.Toast;

import static java.lang.Thread.sleep;

public class SwitchingActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);

        if (NfcController.checkNfcEnableStatus(this)) {
            Toast.makeText(this, "Try to disable NFC", Toast.LENGTH_SHORT).show();
            NfcController.enableNfc(false, this);
        } else {
            Toast.makeText(this, "Try to enable NFC", Toast.LENGTH_SHORT).show();
            NfcController.enableNfc(true, this);
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                updateAllWidgets(ApplicationContextHelper.getContext(), R.layout.nfc_toggler_widget, NfcTogglerWidget.class);
            }
        });
        thread.start();

        finish();
    }

    public static void updateAllWidgets(final Context context,
                                        final int layoutResourceId,
                                        final Class<? extends AppWidgetProvider> appWidgetClass) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), layoutResourceId);

        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        final int[] appWidgetIds = manager.getAppWidgetIds(new ComponentName(context, appWidgetClass));

        for (int i = 0; i < appWidgetIds.length; ++i) {
            NfcTogglerWidget.Companion.updateAppWidget(context, manager, appWidgetIds[i]);
        }
    }
}
