package edu.westga.justinwalker.alert;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import edu.westga.justinwalker.alert.db.AlarmContract;
import edu.westga.justinwalker.alert.db.controller.DBAccess;
import edu.westga.justinwalker.alert.models.SharedConstants;

/**
 * Created by Family on 10/20/2014.
 */
public class ViewHistory extends Activity {
    private DBAccess dbAccess;
    private SimpleCursorAdapter dataAdapter;
    private SharedPreferences settings;
    private Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_history);

        this.dbAccess = new DBAccess(getBaseContext());

        this.settings = getSharedPreferences(SharedConstants.USER_PREFS, 0);
        this.editor = this.settings.edit();

        this.initializeFromSharedPreferences();

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
        getMenuInflater().inflate(R.menu.history_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_clear_history:
                this.dbAccess.deleteAllHistory();
                Intent intent = getIntent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                finish();
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initializeFromSharedPreferences() {
        LinearLayout background = (LinearLayout) this.findViewById(R.id.viewHistoryLayout);
        background.setBackgroundColor(settings.getInt("backgroundcolor", getResources().getColor(R.color.background_color)));
    }
}
