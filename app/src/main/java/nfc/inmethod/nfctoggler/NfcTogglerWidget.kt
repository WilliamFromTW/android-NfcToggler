package nfc.inmethod.nfctoggler

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.widget.RemoteViews

/**
 * Implementation of App Widget functionality.
 */
class NfcTogglerWidget : AppWidgetProvider() {

    public override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    public override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    public override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    companion object {


        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {

            val widgetText = context.getString(R.string.appwidget_text)
            val views = RemoteViews(context.getPackageName(), R.layout.nfc_toggler_widget)
            views.setTextViewText(R.id.appwidget_text, widgetText)
            val intent = Intent(context, NfcWidgetSwitchingActivity::class.java)
            //intent.putExtra("appWidgetId", appWidgetId);
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

            views.setOnClickPendingIntent(R.id.appwidget_text, pendingIntent)

            if (NfcController.checkNfcEnableStatus(context)) {
                views.setTextColor(R.id.appwidget_text, Color.BLUE);
                views.setTextViewText(R.id.appwidget_text, "NFC")
            } else {
                views.setTextColor(R.id.appwidget_text, Color.RED);
                views.setTextViewText(R.id.appwidget_text, "NFC")
            }
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}

