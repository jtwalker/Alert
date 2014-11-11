package edu.westga.justinwalker.alert.services;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.text.format.Time;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import edu.westga.justinwalker.alert.R;
import edu.westga.justinwalker.alert.alarm.GenerateAlarm;
import edu.westga.justinwalker.alert.db.controller.DBAccess;
import edu.westga.justinwalker.alert.mailer.Mailer;
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
    private String alarmTime;
    private String repeatingAlarm;
    private String userEmailAddress;

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.active_alarm_layout);

		
		this.dbAccess = new DBAccess(getApplicationContext());
        this.requestCode = getIntent().getExtras().getInt("requestCode");
		
		this.settings = getSharedPreferences(SharedConstants.USER_PREFS, 0);
		this.editor = this.settings.edit();
		
		this.initializeClickables();
        this.initializeFromSharedPreferences();

        if(requestCode != SharedConstants.CALENDAR_INTENT_CODE) {
            this.fireNotification();
            this.initializeFromDatabase();
            this.checkShouldSnoozeBeEnabled();
        }
        else {
            this.initializeCalendarAlarm();
        }
		
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
		/*if(settings.contains("dismiss") && this.dbAccess.getRepeating(this.requestCode).equals(SharedConstants.REPEATING_FALSE)) {
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
        LinearLayout background = (LinearLayout) this.findViewById(R.id.activeAlarmMainLinearLayout);
        background.setBackgroundColor(settings.getInt("backgroundcolor", getResources().getColor(R.color.background_color)));
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

       this.alarmTime = this.dbAccess.getAlarmTime(this.requestCode);
       this.repeatingAlarm = this.dbAccess.getRepeating(this.requestCode);
       this.userEmailAddress = this.dbAccess.getAlarmEmail(this.requestCode);
    }

    /**
     *
     */
    private void initializeCalendarAlarm() {
        this.ringtoneUri = Settings.System.DEFAULT_RINGTONE_URI;
        this.userEmailAddress = ""; //Since the database has to be ignored
        this.repeatingAlarm = SharedConstants.REPEATING_FALSE;
        ImageView imageView = (ImageView) findViewById(R.id.alarmEventImageView);
        imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.calendar));
    }

    /**
     *
     */
    private void checkShouldSnoozeBeEnabled() {
        Button snooze = (Button) this.findViewById(R.id.snoozeAlarmButton);
        int snoozeEnabled = this.dbAccess.getSnooze(this.requestCode);

        if(snoozeEnabled == SharedConstants.ALARM_FALSE) {
            snooze.setVisibility(View.GONE);
        }
    }

    /**
     *
     */
    private void fireNotification() {
        Time alarmTime = new Time();
        alarmTime.setToNow();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("ALARM!!")
                        .setContentText(alarmTime.hour + ":" + alarmTime.minute)
                        .setLargeIcon(BitmapFactory.decodeFile(this.dbAccess.getAlarmImage(this.requestCode), options))
                        .setVibrate(new long[]{0, 1000, 200, 250, 150, 150, 75, 150, 75, 150});

        int mNotificationId = 001;
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
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
		intent.putExtra("requestCode", this.requestCode);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, this.requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager alarmManager = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, snoozeTime, pendingIntent);
	}

    /**
     *
     */
    private void checkAndSetRepeatingAlarm() {
        if(!this.repeatingAlarm.equals(SharedConstants.REPEATING_FALSE)) {
            long timeOfAlarm = Long.valueOf(this.alarmTime).longValue();

            Intent intent = new Intent(this, AlarmReceiverActivity.class);
            intent.putExtra("requestCode", this.requestCode);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, this.requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, timeOfAlarm + TimeUnit.DAYS.toMillis(this.findHowManyDaysUntilNextAlarm()), pendingIntent);
        }
    }

    /**
     *
     */
    private void sendEmail(String message) {
        Mailer mailer = new Mailer();
        if(!this.userEmailAddress.equals("")) {
            mailer.sendMail(this.userEmailAddress, SharedConstants.ALERT_UPDATE, message);
        }
    }

    /**
     *
     */
    private void updateHistory(String actionTaken) {
        Calendar cal = Calendar.getInstance();

        String timeString = new SimpleDateFormat("HH:mm").format(cal.getTime());
        String dateString = new SimpleDateFormat("MMM dd").format(cal.getTime());

        this.dbAccess.insertHistory(this.requestCode, timeString, dateString, actionTaken);
    }

    /**
     *
     */
    private void checkForCalendarAlarm() {
        if(this.requestCode == SharedConstants.CALENDAR_INTENT_CODE) {
            GenerateAlarm alarmGenerator = new GenerateAlarm();
            alarmGenerator.createAlarmFromCalendar(this, this.settings.getString("syncemail", ""));
        }
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
				editor.putBoolean("dismiss", true);
				editor.commit();
				sendEmail(SharedConstants.DISMISS_PRESSED);
                updateHistory(SharedConstants.DISMISS_PRESSED);
                checkAndSetRepeatingAlarm();
                checkForCalendarAlarm();
				finish();
				break;
			case R.id.silenceAlarmButton:
				ringtone.stop();
				silencePressed = true;
				break;
			case R.id.snoozeAlarmButton:
				ringtone.stop();
				silencePressed = false;
                sendEmail(SharedConstants.SNOOZE_PRESSED);
                updateHistory(SharedConstants.SNOOZE_PRESSED);
				snooze();
				finish();
				break;
			default:
				Toast.makeText(getApplicationContext(), "Clicked", Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

    /**
     * Gets the difference in the days of the week. For example, if I pass in Monday and today is Tuesday, -1 should be returned.
     */
    private int getDayDifference(String dayOfAlarm) {
        int dayDifference = 0;
        Time now = new Time();
        now.setToNow();
        dayDifference = this.convertDayIntoInteger(dayOfAlarm) - now.weekDay;

        return dayDifference;
    }

    /**
     * This will return an integer value for the dayOfAlarm String
     */
    private int convertDayIntoInteger(String dayOfAlarm) {
        int dayNumber = 0;

        if(dayOfAlarm.equals(getApplicationContext().getString(R.string.day_of_week_mon))) {
            dayNumber = 1;
        }
        else if(dayOfAlarm.equals(getApplicationContext().getString(R.string.day_of_week_tues))) {
            dayNumber = 2;
        }
        else if(dayOfAlarm.equals(getApplicationContext().getString(R.string.day_of_week_wed))) {
            dayNumber = 3;
        }
        else if(dayOfAlarm.equals(getApplicationContext().getString(R.string.day_of_week_thurs))) {
            dayNumber = 4;
        }
        else if(dayOfAlarm.equals(getApplicationContext().getString(R.string.day_of_week_fri))) {
            dayNumber = 5;
        }
        else if(dayOfAlarm.equals(getApplicationContext().getString(R.string.day_of_week_sat))) {
            dayNumber = 6;
        }

        return dayNumber;
    }

    /**
     *
     */
    private int findHowManyDaysUntilNextAlarm() {
        String[] repeatingDays = this.repeatingAlarm.split(",");
        ArrayList<Integer> dayDifferenceForAlarms = new ArrayList<Integer>();

        for(String day: repeatingDays) {
            dayDifferenceForAlarms.add(this.getDayDifference(day));
        }

        return this.findLowestNonNegativeValue(dayDifferenceForAlarms);
    }

    /**
     * Find lowest non negative in the list. If there isn't a non negative, add 7 (number of days in a week) to all the values.
     * then return the lowest.
     */
    private int findLowestNonNegativeValue(ArrayList<Integer> dayDifferences) {
        int lowestNonNegativeValue = 999;
        int greatestNegativeValue = -999;

        for(int i = 0; i < dayDifferences.size(); i++) {
            if(dayDifferences.get(i) >= 0 && (dayDifferences.get(i) < lowestNonNegativeValue)) {
                lowestNonNegativeValue = dayDifferences.get(i);
            }
            else if(dayDifferences.get(i) < 0 && (dayDifferences.get(i) > greatestNegativeValue)) {
                greatestNegativeValue = dayDifferences.get(i);
            }
        }

        //This if statement is not in create alarm because 0 can exist there. In this class, it cannot.
        int daysInAWeek = 7;
        if(lowestNonNegativeValue == 0) {
            return daysInAWeek;
        }

        if(lowestNonNegativeValue != 999) {
            return lowestNonNegativeValue;
        }

        return greatestNegativeValue + daysInAWeek;
    }
}
