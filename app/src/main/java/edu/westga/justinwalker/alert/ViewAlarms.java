package edu.westga.justinwalker.alert;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import edu.westga.justinwalker.alert.adapters.CustomAdapter;
import edu.westga.justinwalker.alert.db.AlarmContract.Alarms;
import edu.westga.justinwalker.alert.db.controller.DBAccess;
import edu.westga.justinwalker.alert.models.SharedConstants;
import edu.westga.justinwalker.alert.services.AlarmReceiverActivity;

public class ViewAlarms extends Activity {

    private DBAccess dbAccess;
    private SimpleCursorAdapter dataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_alarms);

        this.dbAccess = new DBAccess(getBaseContext());

        //displayListView();

        Cursor cursor = this.dbAccess.fetchAllAlarms();
        cursor.moveToFirst();

        String[] images = new String[cursor.getCount()];
        String[] times = new String[cursor.getCount()];
        String[] days = new String[cursor.getCount()];
        String[] emails = new String[cursor.getCount()];
        String[] ringtones = new String[cursor.getCount()];
        String[] snooze = new String[cursor.getCount()];



        int counter = 0;

        if (cursor.moveToFirst()){
            do{
                images[counter] = cursor.getString(cursor.getColumnIndex(Alarms.ALARM_PICTURE));
                times[counter] = this.convertMillisecondsToTime(cursor.getString(cursor.getColumnIndex(Alarms.ALARM_TIME)));
                days[counter] = cursor.getString(cursor.getColumnIndex(Alarms.ALARM_DATE));
                emails[counter] = cursor.getString(cursor.getColumnIndex(Alarms.ALARM_EMAIL));
                ringtones[counter] = this.getRingtoneName(cursor.getString(cursor.getColumnIndex(Alarms.ALARM_RINGTONE)));
                snooze[counter] = this.getWhetherSnoozeEnabled(cursor.getInt(cursor.getColumnIndex(Alarms.ALARM_SNOOZE)));
                counter++;
            }while(cursor.moveToNext());
        }
        cursor.close();


        ListView listView = (ListView) findViewById(R.id.alarmListView);
        registerForContextMenu(listView);
        listView.setAdapter(new CustomAdapter(this, images, times, days, emails, ringtones, snooze));
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
        switch (item.getItemId()) {
            case R.id.editAlarm:
                Toast.makeText(getApplicationContext(), "Edit Alarm", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.deleteAlarm:
                ArrayList<Integer> alarmIDs = this.dbAccess.getAllAlarmIDs();
                this.deleteAlarm(alarmIDs.get(info.position));
                this.refreshActivity();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
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

        return ringtoneTitle.getTitle(this);
    }

    private String getWhetherSnoozeEnabled(int snooze) {
        if (snooze == SharedConstants.ALARM_FALSE) {
            return SharedConstants.DISABLED;
        }

        return SharedConstants.ENABLED;
    }

    private void displayListView() {
        Cursor cursor = this.dbAccess.fetchAllAlarms();

        String columns[] = new String[] {
                Alarms.ALARM_NAME,
                Alarms.ALARM_TIME,
        };

        int[] viewsToBoundTo = new int[] {
                //R.id.firstLine,
                //R.id.secondLine,
        };

        this.dataAdapter = new SimpleCursorAdapter(this, R.layout.alarm_details_layout, cursor, columns, viewsToBoundTo, 0);

        ListView listView = (ListView) findViewById(R.id.alarmListView);

        listView.setAdapter(dataAdapter);
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
}
