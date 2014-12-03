package edu.westga.justinwalker.alert;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.text.format.Time;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import edu.westga.justinwalker.alert.db.controller.DBAccess;
import edu.westga.justinwalker.alert.models.SharedConstants;
import edu.westga.justinwalker.alert.services.AlarmReceiverActivity;

public class CreateAlarm extends FragmentActivity {

    private final int RINGTONE_REQUEST_CODE = 0;
	private final int IMAGE_REQUEST_CODE = 1;
	private DBAccess dbAccess;
    private Intent ringtonePicker;
    private Intent imagePicker;
    private String alarmImage;
    private SharedPreferences settings;
    private Editor editor;
    private String alarmRingtone;
    private String repeatingAlarm;
    private String alarmName;
    private String alarmEmail;
    private int alarmEmailEnabled, alarmSnoozeEnabled;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.create_alarm);

        this.dbAccess = new DBAccess(getBaseContext());

        this.settings = getSharedPreferences(SharedConstants.USER_PREFS, 0);
        this.editor = this.settings.edit();

        if(getIntent().getExtras().getBoolean("edit")) {
            this.initializeFromDatabase();
            Toast.makeText(getApplicationContext(), "Ooops", Toast.LENGTH_SHORT).show();
        }
        else {
            this.initializeFromSharedPreferences();
        }

		this.initializeClickables();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.create_alarm, menu);
		return true;
	}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.setSharedPreferences();
    }

    /**
     *
     */
    private void setSharedPreferences() {
        EditText alarmEmailText = (EditText) this.findViewById(R.id.alarmEmailText);
        this.editor.putString("email", alarmEmailText.getText().toString());
        this.editor.commit();
    }

	/**
	 * 
	 */
	private void initializeClickables() {
		Button createButton = (Button) this.findViewById(R.id.createAlarmButton);
        ImageView imageView = (ImageView) this.findViewById(R.id.alarmImage);
        TextView ringtoneView = (TextView) this.findViewById(R.id.ringtone);
        Switch repeatingSwitch = (Switch) this.findViewById(R.id.repeatingSwitch);
        Switch emailSwitch = (Switch) this.findViewById(R.id.emailSwitch);

		createButton.setOnClickListener(this.inputClickListener);
        imageView.setOnClickListener(this.inputClickListener);
        ringtoneView.setOnClickListener(this.inputClickListener);
        repeatingSwitch.setOnClickListener(this.inputClickListener);
        emailSwitch.setOnClickListener(this.inputClickListener);
	}

    /**
     *
     */
    private void initializeFromSharedPreferences() {
        if(this.settings.contains("email")) {
            this.alarmEmail = this.settings.getString("email", "");
        }

        if(this.settings.contains("image")) {
            String picturePath = this.settings.getString("image", "");
            this.alarmImage = this.settings.getString("image", "");
            ImageView imageView = (ImageView) this.findViewById(R.id.alarmImage);
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        }


        Ringtone tempRingtone;

        if(this.settings.contains("ringtone")) {
            this.alarmRingtone = this.settings.getString("ringtone", "");
            Uri uri = Uri.parse(this.settings.getString("ringtone", ""));
            tempRingtone = RingtoneManager.getRingtone(this, uri);
        }
        else {
            tempRingtone = RingtoneManager.getRingtone(this, Settings.System.DEFAULT_RINGTONE_URI);
            this.alarmRingtone = Settings.System.DEFAULT_RINGTONE_URI.toString();
        }

        String ringtoneName = tempRingtone.getTitle(this);
        TextView ringtoneView = (TextView) this.findViewById(R.id.ringtone);
        ringtoneView.setText(ringtoneName);
        tempRingtone.stop();

        LinearLayout background = (LinearLayout) this.findViewById(R.id.createAlarmLayout);
        background.setBackgroundColor(settings.getInt("backgroundcolor", getResources().getColor(R.color.background_color)));
    }

    private void initializeFromDatabase() {
        int requestCode = getIntent().getExtras().getInt("requestCode");
        EditText alarmName = (EditText) this.findViewById(R.id.alarmNameText);
        ImageView imageView = (ImageView) this.findViewById(R.id.alarmImage);
        TextView ringtoneView = (TextView) this.findViewById(R.id.ringtone);
        Switch repeatingSwitch = (Switch) this.findViewById(R.id.repeatingSwitch);
        Switch snoozeSwitch = (Switch) this.findViewById(R.id.snoozeSwitch);
        Switch emailSwitch = (Switch) this.findViewById(R.id.emailSwitch);
        EditText emailText = (EditText) this.findViewById(R.id.alarmEmailText);

        alarmName.setText(this.dbAccess.getAlarmName(requestCode));

        this.alarmImage = this.dbAccess.getAlarmImage(requestCode);
        imageView.setImageBitmap(BitmapFactory.decodeFile(this.alarmImage));

        this.alarmRingtone = this.dbAccess.getAlarmRingtone(requestCode);
        Uri uri = Uri.parse(this.alarmRingtone);
        Ringtone tempRingtone = RingtoneManager.getRingtone(this, uri);
        String ringtoneName = tempRingtone.getTitle(this);
        ringtoneView.setText(ringtoneName);
        tempRingtone.stop();

        if(!this.dbAccess.getRepeating(requestCode).equals(SharedConstants.REPEATING_FALSE)) {
            repeatingSwitch.setChecked(true);
            this.toggleDaysOfWeek();
            this.setDaysFromDatabase(requestCode);
        }


        if(this.dbAccess.getSnooze(requestCode) == SharedConstants.ALARM_TRUE) {
            snoozeSwitch.setChecked(true);
        }

        String email = this.dbAccess.getAlarmEmail(requestCode);
        if(!email.equals("")) {
            emailSwitch.setChecked(true);
            this.toggleEmailTextField();
            emailText.setText(email);
        }
    }
	
	/**
	 * 
	 */
	private void setAlarm() {
		Time timeOfAlarm = new Time();
		timeOfAlarm.setToNow();
		TimePicker timePicker = (TimePicker) this.findViewById(R.id.timePicker);
		timeOfAlarm.hour = timePicker.getCurrentHour();
		timeOfAlarm.minute = timePicker.getCurrentMinute();
		timeOfAlarm.second = 0;
		
		int alarmEnabled = SharedConstants.ALARM_TRUE;
		int requestCode = 0;

        this.checkForRepeatingAlarms();
        this.checkForEmail();

        String alarmTime = timeOfAlarm.toMillis(false) + "";
        if(getIntent().getExtras().getBoolean("edit")) {
            requestCode = getIntent().getExtras().getInt("requestCode");
            this.dbAccess.update(requestCode, alarmEnabled, this.alarmName, "Date", alarmTime,
                    this.alarmRingtone, this.alarmImage, this.repeatingAlarm, this.alarmSnoozeEnabled, this.alarmEmailEnabled, this.alarmEmail);
        }
        else {
            requestCode = (int) this.dbAccess.insert(alarmEnabled, this.alarmName, "Date", alarmTime,
                    this.alarmRingtone, this.alarmImage, this.repeatingAlarm, this.alarmSnoozeEnabled, this.alarmEmailEnabled, this.alarmEmail);
        }
		
		Intent intent = new Intent(this, AlarmReceiverActivity.class);
		intent.putExtra("requestCode", requestCode);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager alarmManager = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);

		Time now = new Time();
		now.setToNow();
		if(timeOfAlarm.before(now)) {
			timeOfAlarm.monthDay++;
			Toast.makeText(getApplicationContext(), timeOfAlarm.month + " " + timeOfAlarm.monthDay + " " + timeOfAlarm.year, Toast.LENGTH_SHORT).show();
		}

        if(!this.repeatingAlarm.equals(SharedConstants.REPEATING_FALSE)) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, timeOfAlarm.toMillis(false) + TimeUnit.DAYS.toMillis(this.findHowManyDaysUntilNextAlarm()), pendingIntent);
        }
        else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, timeOfAlarm.toMillis(false), pendingIntent);
        }

		finish();
	}

    /**
     *
     */
    private void showImagePicker() {
        this.imagePicker = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(this.imagePicker, IMAGE_REQUEST_CODE);
    }

    /**
     *
     */
    private void showRingtonePicker() {
        this.ringtonePicker = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        this.ringtonePicker.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_RINGTONE);
        this.ringtonePicker.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Pick the Alarm Ringtone");
        this.ringtonePicker.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
        this.ringtonePicker.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(this.alarmRingtone));
        this.ringtonePicker.getBooleanExtra(RingtoneManager.EXTRA_RINGTONE_INCLUDE_DRM, true);

        startActivityForResult(ringtonePicker, RINGTONE_REQUEST_CODE);
    }

    /**
     *
     */
    private void toggleDaysOfWeek() {
        LinearLayout daysOfTheWeek = (LinearLayout) this.findViewById(R.id.daysOfTheWeekLayout);
        if(daysOfTheWeek.getVisibility() != View.GONE) {
            daysOfTheWeek.setVisibility(View.GONE);
            this.editor.putBoolean("repeatingfield", false);
        }
        else {
            daysOfTheWeek.setVisibility(View.VISIBLE);
            this.editor.putBoolean("repeatingfield", true);
        }
        this.editor.commit();
    }

    /**
     *
     */
    private void setDaysFromDatabase(int requestCode) {
        String[] days = this.dbAccess.getRepeating(requestCode).split(",");
        List<String> daysList = Arrays.asList(days);
        ToggleButton monToggleButton = (ToggleButton) this.findViewById(R.id.monToggleButton);
        ToggleButton tuesToggleButton = (ToggleButton) this.findViewById(R.id.tuesToggleButton);
        ToggleButton wedToggleButton = (ToggleButton) this.findViewById(R.id.wedToggleButton);
        ToggleButton thursToggleButton = (ToggleButton) this.findViewById(R.id.thursToggleButton);
        ToggleButton friToggleButton = (ToggleButton) this.findViewById(R.id.friToggleButton);
        ToggleButton satToggleButton = (ToggleButton) this.findViewById(R.id.satToggleButton);
        ToggleButton sunToggleButton = (ToggleButton) this.findViewById(R.id.sunToggleButton);
        ToggleButton[] toggleButtons = {monToggleButton, tuesToggleButton, wedToggleButton, thursToggleButton, friToggleButton, satToggleButton, sunToggleButton};

        for(ToggleButton dayButton: toggleButtons) {
            if(daysList.contains(dayButton.getTextOn())) {
                dayButton.setChecked(true);
            }
        }
    }

    private void toggleEmailTextField() {
        EditText alarmEmailText = (EditText) this.findViewById(R.id.alarmEmailText);
        if(alarmEmailText.getVisibility() != View.GONE) {
            alarmEmailText.setVisibility(View.GONE);
        }
        else {
            alarmEmailText.setVisibility(View.VISIBLE);
        }
    }

    /**
     *
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                if(requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK && null != data) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };

                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    ImageView imageView = (ImageView) findViewById(R.id.alarmImage);
                    imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                    this.alarmImage = picturePath;
                    this.editor.putString("image", picturePath);
                    this.editor.commit();
                }
                else if(requestCode == RINGTONE_REQUEST_CODE) {
                    Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

                    this.alarmRingtone = uri.toString();

                    TextView ringtoneView = (TextView) this.findViewById(R.id.ringtone);

                    Ringtone tempRingtone = RingtoneManager.getRingtone(this, uri);
                    String ringtoneName= tempRingtone.getTitle(this);
                    ringtoneView.setText(ringtoneName);

                    this.editor.putString("ringtone", this.alarmRingtone);
                    this.editor.commit();
                }
                break;
        }
    }

    /**
     *
     */
    private void checkForRepeatingAlarms() {
        Switch repeatingSwitch = (Switch) this.findViewById(R.id.repeatingSwitch);
        ToggleButton monToggleButton = (ToggleButton) this.findViewById(R.id.monToggleButton);
        ToggleButton tuesToggleButton = (ToggleButton) this.findViewById(R.id.tuesToggleButton);
        ToggleButton wedToggleButton = (ToggleButton) this.findViewById(R.id.wedToggleButton);
        ToggleButton thursToggleButton = (ToggleButton) this.findViewById(R.id.thursToggleButton);
        ToggleButton friToggleButton = (ToggleButton) this.findViewById(R.id.friToggleButton);
        ToggleButton satToggleButton = (ToggleButton) this.findViewById(R.id.satToggleButton);
        ToggleButton sunToggleButton = (ToggleButton) this.findViewById(R.id.sunToggleButton);

        if(repeatingSwitch.isChecked() && (!monToggleButton.isChecked() && !tuesToggleButton.isChecked() && !wedToggleButton.isChecked()
                && !thursToggleButton.isChecked() && !friToggleButton.isChecked() && !satToggleButton.isChecked() && !sunToggleButton.isChecked())) {
            this.repeatingAlarm = SharedConstants.REPEATING_FALSE;
        }
        else if(repeatingSwitch.isChecked()) {
            ToggleButton[] toggleButtons = {monToggleButton, tuesToggleButton, wedToggleButton, thursToggleButton, friToggleButton, satToggleButton, sunToggleButton};
            String daysToRepeat = "";

            for(ToggleButton button: toggleButtons) {
                if(button.isChecked()) {
                    daysToRepeat += button.getText() + ",";
                }
            }
            daysToRepeat = daysToRepeat.substring(0, daysToRepeat.length()-1);
            this.repeatingAlarm = daysToRepeat;
        }
        else {
            this.repeatingAlarm = SharedConstants.REPEATING_FALSE;
        }
    }

    /**
     *
     */
    private void checkForEmail() {
        Switch emailSwitch = (Switch) this.findViewById(R.id.emailSwitch);
        EditText alarmEmailText = (EditText) this.findViewById(R.id.alarmEmailText);

        if(emailSwitch.isChecked()) {
            this.alarmEmail = alarmEmailText.getText().toString();
        }
        else {
            this.alarmEmail = "";
        }
    }

    /**
     *
     */
    private void setAlarmDetails() {
        EditText alarmNameText = (EditText) this.findViewById(R.id.alarmNameText);
        this.alarmName = alarmNameText.getText().toString();

        Switch snooze = (Switch) this.findViewById(R.id.snoozeSwitch);
        if(snooze.isChecked()) {
            this.alarmSnoozeEnabled = 1;
        }

        else {
            this.alarmSnoozeEnabled = 0;
        }

        Switch email = (Switch) this.findViewById(R.id.emailSwitch);
        if(email.isChecked()) {
            this.alarmEmailEnabled = SharedConstants.ALARM_TRUE;
        }

        else {
            this.alarmEmailEnabled = SharedConstants.ALARM_FALSE;
        }
    }

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

        //This might have unknown problems
        for(int i = 0; i < dayDifferences.size(); i++) {
            if(dayDifferences.get(i) >= 0 && (dayDifferences.get(i) < lowestNonNegativeValue)) {
                lowestNonNegativeValue = dayDifferences.get(i);
            }
            else if(dayDifferences.get(i) < 0 && (dayDifferences.get(i) > greatestNegativeValue)) {
                greatestNegativeValue = dayDifferences.get(i);
            }
        }

        if(lowestNonNegativeValue != 999) {
            return lowestNonNegativeValue;
        }

        int daysInAWeek = 7;
        return greatestNegativeValue + daysInAWeek;
    }

	/**
	 * 
	 */
	private OnClickListener inputClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			switch (view.getId()) {
                case R.id.createAlarmButton:
                    setAlarmDetails();
                    setAlarm();
                    break;
                case R.id.alarmImage:
                    showImagePicker();
                    break;
                case R.id.ringtone:
                    showRingtonePicker();
                    break;
                case R.id.repeatingSwitch:
                    toggleDaysOfWeek();
                    break;
                case R.id.emailSwitch:
                    toggleEmailTextField();
                    break;
                default:
                    Toast.makeText(getApplicationContext(), "Clicked", Toast.LENGTH_SHORT).show();
                    break;
			}
		}
	};

}
