package edu.westga.justinwalker.alert.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.CalendarContract;
import android.text.format.DateFormat;
import android.widget.Toast;

import java.text.Format;

import edu.westga.justinwalker.alert.models.SharedConstants;
import edu.westga.justinwalker.alert.services.AlarmReceiverActivity;

/**
 * Created by Family on 11/11/2014.
 */
public class GenerateAlarm {

    private Cursor mCursor = null;
    private static final String[] COLS = new String[]
            {CalendarContract.Events.TITLE, CalendarContract.Events.DTSTART, CalendarContract.Events.OWNER_ACCOUNT, CalendarContract.Events.ACCOUNT_NAME};
    private static final int ALARM_TIME_INDEX = 1;

    public void createAlarmFromCalendar(Context context, String googleAccount) {
        String whereClause = CalendarContract.Events.DTSTART + ">? and " + CalendarContract.Events.ACCOUNT_NAME + "=? and " + CalendarContract.Events.OWNER_ACCOUNT + "=?";
        String[] args = {"" + System.currentTimeMillis(), googleAccount, googleAccount};
        //uri, columns, selections, string args, sortorder
        mCursor = context.getContentResolver().query(CalendarContract.Events.CONTENT_URI, COLS, whereClause, args, CalendarContract.Events.DTSTART + " ASC");
        mCursor.moveToFirst();

        if (mCursor.getCount() != 0) {
            Intent myIntent = new Intent(context, AlarmReceiverActivity.class);
            myIntent.putExtra("requestCode", SharedConstants.CALENDAR_INTENT_CODE);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, SharedConstants.CALENDAR_INTENT_CODE, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, mCursor.getLong(ALARM_TIME_INDEX), pendingIntent);

            Format df = DateFormat.getDateFormat(context);
            Format tf = DateFormat.getTimeFormat(context);

            Long time = mCursor.getLong(ALARM_TIME_INDEX);

            Toast.makeText(context, "Alarm set for: " + df.format(time) + " at " + tf.format(time), Toast.LENGTH_LONG).show();
        }

        mCursor.close();
    }
}
