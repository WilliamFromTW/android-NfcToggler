package nfc.inmethod.nfctoggler;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.Toast;

public class SwitchingActivity  extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);

        if(NfcController.checkNfcEnableStatus(this))
          NfcController.enableNfc(false,this);
        else
            NfcController.enableNfc(true,this);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "NFC Status = "+NfcController.checkNfcEnableStatus( this), Toast.LENGTH_SHORT).show();
        updateAllWidgets(this,R.layout.new_app_widget,NfcTogglerWidget.class);

        finish();
    }

    public static void updateAllWidgets(final Context context,
                                        final int layoutResourceId,
                                        final Class< ? extends AppWidgetProvider> appWidgetClass)
    {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), layoutResourceId);

        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        final int[] appWidgetIds = manager.getAppWidgetIds(new ComponentName(context, appWidgetClass));

        for (int i = 0; i < appWidgetIds.length; ++i)
        {
            NfcTogglerWidget.Companion.updateAppWidget(context,manager,appWidgetIds[i]);
        }
    }
}
