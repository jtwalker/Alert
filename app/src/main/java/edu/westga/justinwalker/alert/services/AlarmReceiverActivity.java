package edu.westga.justinwalker.alert.services;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.format.Time;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import edu.westga.justinwalker.alert.R;
import edu.westga.justinwalker.alert.db.controller.DBAccess;
import edu.westga.justinwalker.alert.models.SharedConstants;

public class AlarmReceiverActivity extends Activity {
	
	private final int MINUTE_IN_MILLISECOND = 60000;
	private DBAccess dbAccess;
	private int requestCode;
	private final int SNOOZE_TIME_IN_MINUTE = 1;
	//private Ringtone ringtone;
	private boolean silencePressed;
	private SharedPreferences settings;
	private Editor editor;
	private MediaPlayer ringtone;
    private Uri ringtoneUri;

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.active_alarm_layout);
		
		this.dbAccess = new DBAccess(getApplicationContext());
        this.requestCode = getIntent().getExtras().getInt("requestCode");
		
		this.settings = getSharedPreferences(SharedConstants.USER_PREFS, 0);
		this.editor = this.settings.edit();
		
		this.initializeClickables();
        //this.initializeFromSharedPreferences();
        this.initializeFromDatabase();
		
		this.ringtone = MediaPlayer.create(this, this.ringtoneUri);

		this.silencePressed = this.settings.getBoolean("silenced", false);
		
		if (!this.silencePressed) {
			this.ringtone.start();
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		this.ringtone.stop();
        this.ringtone.release();
		this.editor.putBoolean("silenced", this.silencePressed);
		this.editor.commit();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		/*if(settings.contains("dismiss") && this.dbAccess.getRepeating(this.requestCode) == SharedConstants.ALARM_FALSE) {
			this.dbAccess.delete(this.requestCode);
			editor.remove("dismiss");
			editor.commit();
		}*/
	}

	/**
	 * 
	 */
	private void initializeClickables() {
		Button dismissButton = (Button) this.findViewById(R.id.dismissAlarmButton);
		Button snoozeButton = (Button) this.findViewById(R.id.snoozeAlarmButton);
		Button silenceButton = (Button) this.findViewById(R.id.silenceAlarmButton);
		
		dismissButton.setOnClickListener(this.inputClickListener);
		snoozeButton.setOnClickListener(this.inputClickListener);
		silenceButton.setOnClickListener(this.inputClickListener);
	}

    /**
     *
     */
    private void initializeFromSharedPreferences() {
        if(this.settings.contains("image")) {
            String picturePath = this.settings.getString("image", "");
            ImageView imageView = (ImageView) this.findViewById(R.id.alarmEventImageView);
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        }
    }

    /**
     *
     */
    private void initializeFromDatabase() {
        String picturePath = this.dbAccess.getAlarmImage(this.requestCode);
        if (picturePath != null && !picturePath.equals("")) {
            ImageView imageView = (ImageView) findViewById(R.id.alarmEventImageView);
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        }

        String ringtone = this.dbAccess.getAlarmRingtone(this.requestCode);
        if (ringtone != null && !ringtone.equals("")) {
            this.ringtoneUri = Uri.parse(ringtone);
        }
    }
	
	/**
	 * 
	 */
	private void playRingtone(Uri uri) {
		//Empty for now
	}
	
	/**
	 * 
	 */
	private void snooze() {
		Time timeOfAlarm = new Time();
		timeOfAlarm.setToNow();
		long snoozeTime = timeOfAlarm.toMillis(false) + (this.SNOOZE_TIME_IN_MINUTE * this.MINUTE_IN_MILLISECOND);
		
		Intent intent = new Intent(this, AlarmReceiverActivity.class);
		intent.putExtra("requestCode", requestCode);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, this.requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager alarmManager = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, snoozeTime, pendingIntent);
	}
	
	/**
	 * 
	 */
	private OnClickListener inputClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.dismissAlarmButton:
				silencePressed = false;
				//Image removal from settings
				editor.putBoolean("dismiss", true);
				editor.commit();
				//Email
				finish();
				break;
			case R.id.silenceAlarmButton:
				ringtone.stop();
				silencePressed = true;
				break;
			case R.id.snoozeAlarmButton:
				ringtone.stop();
				silencePressed = false;
				snooze();
				finish();
				break;
			default:
				Toast.makeText(getApplicationContext(), "Clicked", Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

}
