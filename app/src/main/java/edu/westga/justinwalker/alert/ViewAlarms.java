package edu.westga.justinwalker.alert;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import edu.westga.justinwalker.alert.adapters.CustomAdapter;
import edu.westga.justinwalker.alert.db.AlarmContract.Alarms;
import edu.westga.justinwalker.alert.db.controller.DBAccess;
import edu.westga.justinwalker.alert.models.SharedConstants;
import edu.westga.justinwalker.alert.services.AlarmReceiverActivity;

public class ViewAlarms extends Activity {

    private final int CREATE_ALARM = 1;
    private DBAccess dbAccess;
    private String[] images, times, days, emails, ringtones, snooze;
    private SharedPreferences settings;
    private Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_alarms);

        this.dbAccess = new DBAccess(getBaseContext());

        this.settings = getSharedPreferences(SharedConstants.USER_PREFS, 0);
        this.editor = this.settings.edit();

        this.initializeFromSharedPreferences();

        Cursor cursor = this.dbAccess.fetchAllAlarms();
        cursor.moveToFirst();

        this.images = new String[cursor.getCount()];
        this.times = new String[cursor.getCount()];
        this.days = new String[cursor.getCount()];
        this.emails = new String[cursor.getCount()];
        this.ringtones = new String[cursor.getCount()];
        this.snooze = new String[cursor.getCount()];

        int counter = 0;

        if (cursor.moveToFirst()){
            do{
                this.images[counter] = cursor.getString(cursor.getColumnIndex(Alarms.ALARM_PICTURE));
                this.times[counter] = this.convertMillisecondsToTime(cursor.getString(cursor.getColumnIndex(Alarms.ALARM_TIME)));
                this.days[counter] = this.modifyRepeatingDaysString(cursor.getString(cursor.getColumnIndex(Alarms.ALARM_REPEAT)));
                this.emails[counter] = cursor.getString(cursor.getColumnIndex(Alarms.ALARM_EMAIL));
                this.ringtones[counter] = this.getRingtoneName(cursor.getString(cursor.getColumnIndex(Alarms.ALARM_RINGTONE)));
                this.snooze[counter] = this.getWhetherSnoozeEnabled(cursor.getInt(cursor.getColumnIndex(Alarms.ALARM_SNOOZE)));
                counter++;
            }while(cursor.moveToNext());
        }
        cursor.close();

        ListView listView = (ListView) findViewById(R.id.alarmListView);
        registerForContextMenu(listView);
        listView.setAdapter(new CustomAdapter(this, this.images, this.times, this.days, this.emails, this.ringtones, this.snooze));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.alarm_menu, menu);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle(R.string.context_menu);
        getMenuInflater().inflate(R.menu.view_alarm_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        ArrayList<Integer> alarmIDs = this.dbAccess.getAllAlarmIDs();
        switch (item.getItemId()) {
            case R.id.editAlarm:
                this.invokeActivityToEditAlarm(alarmIDs.get(info.position));
                //this.refreshActivity(); //Doesn't work here. Refreshes before activity finishes.
                return true;
            case R.id.deleteAlarm:
                this.deleteAlarm(alarmIDs.get(info.position));
                this.refreshActivity();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void initializeFromSharedPreferences() {
        LinearLayout background = (LinearLayout) this.findViewById(R.id.viewAlarmsLayout);
        background.setBackgroundColor(settings.getInt("backgroundcolor", getResources().getColor(R.color.background_color)));
    }

    private String convertMillisecondsToTime(String millisecondsString) {
        long milliseconds = Long.valueOf(millisecondsString).longValue();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);

        return new SimpleDateFormat("HH:mm").format(calendar.getTime());
    }

    private String getRingtoneName(String ringtone) {
        Uri ringtoneUri = Uri.parse(ringtone);
        Ringtone ringtoneTitle = RingtoneManager.getRingtone(this, ringtoneUri);
        String ringtoneName = ringtoneTitle.getTitle(this);
        ringtoneTitle.stop();

        return ringtoneName;
    }

    private String getWhetherSnoozeEnabled(int snooze) {
        if (snooze == SharedConstants.ALARM_FALSE) {
            return SharedConstants.DISABLED;
        }

        return SharedConstants.ENABLED;
    }

    private String modifyRepeatingDaysString(String repeating) {
        String everyday = getString(R.string.day_of_week_mon) + "," + getString(R.string.day_of_week_tues) + "," +
                getString(R.string.day_of_week_wed) + "," + getString(R.string.day_of_week_thurs) + "," +
                getString(R.string.day_of_week_fri) + "," + getString(R.string.day_of_week_sat) + "," + getString(R.string.day_of_week_sun);
        if(repeating.equals(SharedConstants.REPEATING_FALSE)) {
            return "";
        }
        else if(repeating.equals(everyday)) {
            return getString(R.string.everyday);
        }

        return repeating.replace(",", ", ");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_delete_alarms:
                this.deleteAllAlarms();
                this.refreshActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void refreshActivity() {
        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        startActivity(intent);
    }

    private void deleteAllAlarms() {
        ArrayList<Integer> alarmIDs = this.dbAccess.getAllAlarmIDs();

        for(int id: alarmIDs) {
            this.deleteAlarm(id);
        }
    }

    private void deleteAlarm(int alarmID) {
        Intent intent = new Intent(this, AlarmReceiverActivity.class);
        intent.putExtra("requestCode", alarmID);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, alarmID, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

        this.dbAccess.delete(alarmID);
    }

    private void invokeActivityToEditAlarm(int alarmID) {
        Intent intent = new Intent(getApplicationContext(), CreateAlarm.class);
        intent.putExtra("edit", true);
        intent.putExtra("requestCode", alarmID);
        startActivityForResult(intent, this.CREATE_ALARM);
        this.refreshActivity();
    }
}
