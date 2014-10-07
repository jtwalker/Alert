package edu.westga.justinwalker.alert.db.controller;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import edu.westga.justinwalker.alert.db.AlarmDBAdapter;

/**
 * A controller for the database.
 * 
 * @author Justin Walker
 *
 */
public class DBAccess {

	private final int ID_INDEX = 0;
	private final int ENABLED_INDEX = 1;
	private final int NAME_INDEX = 2;
	private final int DATE_INDEX = 3;
	private final int TIME_INDEX = 4;
	private final int RINGTONE_INDEX = 5;
	private final int PICTURE_INDEX = 6;
	private final int REPEAT_INDEX = 7;
	private final int SNOOZE_INDEX = 8;
	private final int NOTIFY_INDEX = 9;
	private final int EMAIL_INDEX = 10;

	private AlarmDBAdapter adapter;

	public DBAccess(Context context) {
		this.adapter = new AlarmDBAdapter(context);
	}

	/**
	 * Inserts an alarm into the database
	 * @param alarmEnabled 0 or 1 depending on if the alarm is enabled
	 * @param alarmName the name of the alarm
	 * @param alarmDate the date for the alarm
	 * @param alarmTime the time of the alarm
	 * @param alarmRingtone the ringtone to use for the alarm
	 * @param alarmPicture the image to use for the alarm
	 * @param alarmRepeat 0 or 1 depending on if the alarm is allowed to repeat
	 * @param alarmSnooze 0 or 1 depending on if snooze is allowed
	 * @param alarmNotify 0 or 1 depending on if the caretaker should be emailed
	 * @param alarmEmail the email of the person to notify
	 * @return the id of the row just inserted
	 */
	public long insert(int alarmEnabled, String alarmName, String alarmDate,
			String alarmTime, String alarmRingtone, String alarmPicture,
			String alarmRepeat, int alarmSnooze, int alarmNotify, String alarmEmail) {
		long rowID = 0;

		this.adapter.open();
		rowID = this.adapter.insertAlarm(alarmEnabled, alarmName, alarmDate,
				alarmTime, alarmRingtone, alarmPicture, alarmRepeat,
				alarmSnooze, alarmNotify, alarmEmail);
		this.adapter.close();

		return rowID;
	}
	
	/**
	 * Updates the alarm at the given id to the parameters given
	 * 
	 * @param alarmID the id of the alarm to update
	 * @param alarmEnabled 0 or 1 depending on if the alarm is enabled
	 * @param alarmName the name of the alarm
	 * @param alarmDate the date for the alarm
	 * @param alarmTime the time of the alarm
	 * @param alarmRingtone the ringtone to use for the alarm
	 * @param alarmPicture the image to use for the alarm
	 * @param alarmRepeat A string separated by commas representing the days to repeat; Example: "Mon,Fri". "False" if you don't repeat.
	 * @param alarmSnooze 0 or 1 depending on if snooze is allowed
	 * @param alarmNotify 0 or 1 depending on if the caretaker should be emailed
	 * @param alarmEmail the email of the person to notify
	 */
	public void update(int alarmID, int alarmEnabled, String alarmName, String alarmDate,
			String alarmTime, String alarmRingtone, String alarmPicture,
			String alarmRepeat, int alarmSnooze, int alarmNotify, String alarmEmail) {
		this.adapter.open();
		this.adapter.updateAlarm(alarmID, alarmEnabled, alarmName, alarmDate,
				alarmTime, alarmRingtone, alarmPicture, alarmRepeat,
				alarmSnooze, alarmNotify, alarmEmail);
		this.adapter.close();
	}

	/**
	 * Retrieves the values stored for snooze for an alarm
	 * @param id the id of the alarm
	 * @return 0 or 1 depending on if snooze is allowed.
	 */
	public int getSnooze(long id) {
		int snooze = 0;

		this.adapter.open();
		Cursor cursor = this.adapter.fetchAlarm(id);
		snooze = cursor.getInt(this.SNOOZE_INDEX);
		cursor.close();
		this.adapter.close();

		return snooze;
	}

	/**
	 * Retrieves the values stored for repeating of the alarm
	 * @param id the id fo the alarm
	 * @return 0 or 1 depending on if repeating is enabled for this alarm
	 */
	public String getRepeating(long id) {
		String repeating = "";

		this.adapter.open();
		Cursor cursor = this.adapter.fetchAlarm(id);
		repeating = cursor.getString(this.REPEAT_INDEX);
		cursor.close();
		this.adapter.close();

		return repeating;
	}

	/**
	 * Retrieves the value stored for notify
	 * @param id the id of the alarm
	 * @return 0 or 1 depending on if notify is enabled
	 */
	public int getNotify(long id) {
		int notify = 0;
		
		this.adapter.open();
		Cursor cursor = this.adapter.fetchAlarm(id);
		notify = cursor.getInt(this.NOTIFY_INDEX);
		cursor.close();
		this.adapter.close();

		return notify;
	}

	/**
	 * Retrieves the time of the alarm
	 * @param id the id of the alarm
	 * @return the time of the alarm
	 */
	public String getAlarmTime(long id) {
		String alarmTime = "";

		this.adapter.open();
		Cursor cursor = this.adapter.fetchAlarm(id);
		alarmTime = cursor.getString(this.TIME_INDEX);
		cursor.close();
		this.adapter.close();

		return alarmTime;
	}

	/**
	 * Gets the date of the alarm
	 * @param id the id of the alarm
	 * @return the date of the alarm
	 */
	public String getAlarmDate(long id) {
		String alarmDate = "";

		this.adapter.open();
		Cursor cursor = this.adapter.fetchAlarm(id);
		alarmDate = cursor.getString(this.DATE_INDEX);
		cursor.close();
		this.adapter.close();

		return alarmDate;
	}

	/**
	 * Gets the name of the alarm
	 * @param id the id of the alarm
	 * @return the name of the alarm
	 */
	public String getAlarmName(long id) {
		String alarmName = "";

		this.adapter.open();
		Cursor cursor = this.adapter.fetchAlarm(id);
		alarmName = cursor.getString(this.NAME_INDEX);
		cursor.close();
		this.adapter.close();

		return alarmName;
	}

	/**
	 * Gets the ringtone of the alarm
	 * @param id the id of the alarm
	 * @return the ringtone of the alarm
	 */
	public String getAlarmRingtone(long id) {
		String alarmRingtone = "";

		this.adapter.open();
		Cursor cursor = this.adapter.fetchAlarm(id);
		alarmRingtone = cursor.getString(this.RINGTONE_INDEX);
		cursor.close();
		this.adapter.close();

		return alarmRingtone;
	}

	/**
	 * Gets the image for the alarm
	 * @param id the id of the alarm
	 * @return the image for the alarm
	 */
	public String getAlarmImage(long id) {
		String alarmImage = "";

		this.adapter.open();
		Cursor cursor = this.adapter.fetchAlarm(id);
		alarmImage = cursor.getString(this.PICTURE_INDEX);
		cursor.close();
		this.adapter.close();

		return alarmImage;
	}

	/**
	 * Gets the email for the alarm
	 * @param id the id of the alarm
	 * @return the email for the alarm
	 */
	public String getAlarmEmail(long id) {
		String alarmEmail = "";

		this.adapter.open();
		Cursor cursor = this.adapter.fetchAlarm(id);
		alarmEmail = cursor.getString(this.EMAIL_INDEX);
		cursor.close();
		this.adapter.close();

		return alarmEmail;
	}

	/**
	 * Deletes an alarm
	 * @param alarmID the id of the alarm
	 */
	public void delete(int alarmID) {
		this.adapter.open();
		this.adapter.deleteAlarm(alarmID);
		this.adapter.close();
	}
	
	/**
	 * Deletes all alarms
	 */
	public void deleteAllAlarms() {
		this.adapter.open();
		this.adapter.deleteAllAlarms();
		this.adapter.close();
	}

	/**
	 * Returns the number of alarms
	 * @return the number of alarms
	 */
	public int getCount() {
		int count = 0;

		this.adapter.open();
		Cursor cursor = this.adapter.fetchAllAlarms();
		count = cursor.getCount();
		cursor.close();
		this.adapter.close();

		return count;
	}
	
	/**
	 * Gets the id of all of the alarms
	 * @return the ids of all alarms
	 */
	public ArrayList<Integer> getAllAlarmIDs() {
		ArrayList<Integer> alarmsIDs = new ArrayList<Integer>();
		
		this.adapter.open();
		Cursor cursor = this.adapter.fetchAllAlarms();
		while(cursor.moveToNext()) {
			alarmsIDs.add(cursor.getInt(this.ID_INDEX));
		}
		cursor.close();
		this.adapter.close();
		
		return alarmsIDs;
	}

	/**
	 * Retrieves the next AutoIncID WIP
	 * @return the next auto increment id
	 */
	public int getNextAutoIncID() {
		int nextAutoIncID = 0;

		this.adapter.open();
		nextAutoIncID = this.adapter.getNextAutoIncID();
		this.adapter.close();

		return nextAutoIncID;
	}

}

