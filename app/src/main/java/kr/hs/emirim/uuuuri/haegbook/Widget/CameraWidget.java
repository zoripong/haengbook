package kr.hs.emirim.uuuuri.haegbook.Widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import kr.hs.emirim.uuuuri.haegbook.Activity.TakePicturesActivity;
import kr.hs.emirim.uuuuri.haegbook.R;


public class CameraWidget extends AppWidgetProvider {
    private static final String SHOW_DIALOG_ACTION = "kr.hs.emirim.uuuuri.haegbook.widgetshowdialog";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.camera_widget);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);


        Intent intent=new Intent(context, TakePicturesActivity.class);
        PendingIntent pe= PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.button, pe);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);

    }

    @Override
    public void onReceive(final Context context, Intent intent) {
    // If the intent is the one that signals
    // to launch the modal popup activity
    // we launch the activity
    if(intent.getAction().equals(SHOW_DIALOG_ACTION)) {

    Intent i = new Intent(context, TakePicturesActivity.class);
    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    context.startActivity(i);

    }

    super.onReceive(context, intent);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
//        for (int appWidgetId : appWidgetIds) {
//            updateAppWidget(context, appWidgetManager, appWidgetId);
//        }

        prepareWidget(context);
        super.onUpdate(context, appWidgetManager, appWidgetIds);

    }

    private void prepareWidget(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        ComponentName thisWidget =
        new ComponentName(context, CameraWidget.class);

        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        for (int widgetId : allWidgetIds) {

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
        R.layout.camera_widget);

        // Create intent that launches the
        // modal popup activity
        Intent intent = new Intent(context, CameraWidget.class);
        intent.setAction(SHOW_DIALOG_ACTION);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
        context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Attach the onclick listener to
        // the widget button
        remoteViews.setOnClickPendingIntent(R.id.button,
        pendingIntent);

        appWidgetManager.updateAppWidget(widgetId, remoteViews);

        }

    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

}


