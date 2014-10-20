package edu.westga.justinwalker.alert;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import edu.westga.justinwalker.alert.db.AlarmContract.Alarms;
import edu.westga.justinwalker.alert.db.controller.DBAccess;

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
        // getMenuInflater().inflate(R.menu.create_alarm, menu);
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
}
