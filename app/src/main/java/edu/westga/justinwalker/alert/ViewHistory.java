package edu.westga.justinwalker.alert;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import edu.westga.justinwalker.alert.db.AlarmContract;
import edu.westga.justinwalker.alert.db.controller.DBAccess;

/**
 * Created by Family on 10/20/2014.
 */
public class ViewHistory extends Activity {
    private DBAccess dbAccess;
    private SimpleCursorAdapter dataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_history);

        this.dbAccess = new DBAccess(getBaseContext());

        displayListView();
    }

    private void displayListView() {
        Cursor cursor = this.dbAccess.fetchAllHistory();

        String columns[] = new String[] {
                AlarmContract.Alarms.HISTORY_TIME,
                AlarmContract.Alarms.HISTORY_DATE,
                AlarmContract.Alarms.HISTORY_ACTION,
                AlarmContract.Alarms.ALARM_ID,
        };

        int[] viewsToBoundTo = new int[] {
                R.id.historyTimeView,
                R.id.historyDateView,
                R.id.historyActionView,
                R.id.historyAlarmNumberView,
        };

        this.dataAdapter = new SimpleCursorAdapter(this, R.layout.history_details_layout, cursor, columns, viewsToBoundTo, 0);

        ListView listView = (ListView) findViewById(R.id.historyListView);

        listView.setAdapter(dataAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.create_alarm, menu);
        return true;
    }
}
