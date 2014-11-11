package edu.westga.justinwalker.alert;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import edu.westga.justinwalker.alert.models.SharedConstants;

public class MainActivity extends ActionBarActivity {
	
	private final int CREATE_ALARM = 1;
	private final int VIEW_ALARMS = 2;
    private final int VIEW_HISTORY = 3;
    private final int VIEW_SETTINGS = 4;
    private SharedPreferences settings;
    private Editor editor;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        this.settings = getSharedPreferences(SharedConstants.USER_PREFS, 0);
        this.editor = this.settings.edit();

        this.initializeFromSharedPreferences();

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

    private void initializeFromSharedPreferences() {
        FrameLayout background = (FrameLayout) this.findViewById(R.id.container);
        background.setBackgroundColor(settings.getInt("backgroundcolor", getResources().getColor(R.color.background_color)));
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.action_create_alarm:
			this.invokeActivityToCreateAlarm();
			return true;
		case R.id.action_view_alarms:
			this.invokeActivityToViewAlarms();
			return true;
        case R.id.action_alarm_history:
            this.invokeActivityToViewHistory();
            return true;
		case R.id.action_settings:
            this.invokeActivityToViewSettings();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.refreshActivity();
    }

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}
	
	private void invokeActivityToCreateAlarm() {
		Intent intent = new Intent(getApplicationContext(), CreateAlarm.class);
        intent.putExtra("edit", false);
		startActivityForResult(intent, this.CREATE_ALARM);
	}
	
	private void invokeActivityToViewAlarms() {
		Intent intent = new Intent(getApplicationContext(), ViewAlarms.class);
		startActivityForResult(intent, this.VIEW_ALARMS);
	}

    private void invokeActivityToViewHistory() {
        Intent intent = new Intent(getApplicationContext(), ViewHistory.class);
        startActivityForResult(intent, this.VIEW_HISTORY);
    }

    private void invokeActivityToViewSettings() {
        Intent intent = new Intent(getApplicationContext(), ViewSettings.class);
        startActivityForResult(intent, this.VIEW_SETTINGS);
    }

    private void refreshActivity() {
        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        startActivity(intent);
    }
}
