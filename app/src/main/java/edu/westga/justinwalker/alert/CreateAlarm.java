package edu.westga.justinwalker.alert;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.text.format.Time;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;
import edu.westga.justinwalker.alert.db.controller.DBAccess;
import edu.westga.justinwalker.alert.models.SharedConstants;
import edu.westga.justinwalker.alert.services.AlarmReceiverActivity;

public class CreateAlarm extends FragmentActivity {

    private final int RINGTONE_REQUEST_CODE = 0;
	private final int IMAGE_REQUEST_CODE = 1;
	private DBAccess dbAccess;
    private Intent imagePicker;
    private String alarmImage;
    private SharedPreferences settings;
    private Editor editor;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.create_alarm);

        this.settings = getSharedPreferences(SharedConstants.USER_PREFS, 0);
        this.editor = this.settings.edit();

        this.initializeFromSharedPreferences();
		this.initializeClickables();
		
		this.dbAccess = new DBAccess(getBaseContext());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.create_alarm, menu);
		return true;
	}

	/**
	 * 
	 */
	private void initializeClickables() {
		Button createButton = (Button) this.findViewById(R.id.createAlarmButton);
        ImageView imageView = (ImageView) this.findViewById(R.id.alarmImage);

		createButton.setOnClickListener(this.inputClickListener);
        imageView.setOnClickListener(this.inputClickListener);

	}

    /**
     *
     */
    private void initializeFromSharedPreferences() {
        if(this.settings.contains("image")) {
            String picturePath = this.settings.getString("image", "");
            ImageView imageView = (ImageView) this.findViewById(R.id.alarmImage);
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
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
		
		//Should be an if statement once edit alarms is enabled
		//Will also have random other options but not yet
		requestCode = (int) this.dbAccess.insert(alarmEnabled, "Name", "Date", "Time", 
				"ringtone", "Image", 0, 0, 0, "Email");
		
		Intent intent = new Intent(this, AlarmReceiverActivity.class);
		intent.putExtra("requestCode", requestCode);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager alarmManager = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
		
		//If repeating I will have to add more here
		Time now = new Time();
		now.setToNow();
		if(timeOfAlarm.before(now)) {
			//Increase alarm time by a day
			//Going to need massive loops....
			timeOfAlarm.monthDay++;
			Toast.makeText(getApplicationContext(), timeOfAlarm.month + " " + timeOfAlarm.monthDay + " " + timeOfAlarm.year, Toast.LENGTH_SHORT).show();
		}
		
		alarmManager.set(AlarmManager.RTC_WAKEUP, timeOfAlarm.toMillis(false), pendingIntent);
		
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
                    // String picturePath contains the path of selected Image
                    ImageView imageView = (ImageView) findViewById(R.id.alarmImage);
                    imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                    this.alarmImage = picturePath;
                    this.editor.putString("image", picturePath);
                    this.editor.commit();
                }
                break;
        }
    }

	/**
	 * 
	 */
	private OnClickListener inputClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.createAlarmButton:
				setAlarm();
				break;
            case R.id.alarmImage:
                showImagePicker();
                Toast.makeText(getApplicationContext(), "IMAGE!", Toast.LENGTH_SHORT).show();
                break;
			default:
				Toast.makeText(getApplicationContext(), "Clicked", Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

}
