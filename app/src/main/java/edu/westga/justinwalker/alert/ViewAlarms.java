package edu.westga.justinwalker.alert;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.util.ArrayList;

import edu.westga.justinwalker.alert.db.AlarmContract.Alarms;
import edu.westga.justinwalker.alert.db.controller.DBAccess;
import edu.westga.justinwalker.alert.services.AlarmReceiverActivity;

public class ViewAlarms extends Activity {

    private DBAccess dbAccess;
    private SimpleCursorAdapter dataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_alarms);

        this.dbAccess = new DBAccess(getBaseContext());

        displayListView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.alarm_menu, menu);
        return true;
    }

    private void displayListView() {
        Cursor cursor = this.dbAccess.fetchAllAlarms();

        String columns[] = new String[] {
                Alarms.ALARM_NAME,
                Alarms.ALARM_TIME,
        };

        int[] viewsToBoundTo = new int[] {
                R.id.firstLine,
                R.id.secondLine,
        };

        this.dataAdapter = new SimpleCursorAdapter(this, R.layout.alarm_details_layout, cursor, columns, viewsToBoundTo, 0);

        ListView listView = (ListView) findViewById(R.id.alarmListView);

        listView.setAdapter(dataAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_delete_alarms:
                invokeCommandToDeleteAlarms();
                Intent intent = getIntent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                finish();
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void invokeCommandToDeleteAlarms() {
        ArrayList<Integer> alarmIDs = this.dbAccess.getAllAlarmIDs();

        for(int id: alarmIDs) {
            Intent intent = new Intent(this, AlarmReceiverActivity.class);
            intent.putExtra("requestCode", id);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
        }

        this.dbAccess.deleteAllAlarms();
    }
}
