package edu.westga.justinwalker.alert.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import edu.westga.justinwalker.alert.db.AlarmContract.Alarms;

/**
 * The adapter for the database
 * @author Justin Walker and Duane Yoder
 *
 */
public class AlarmDBAdapter {
	
	private AlarmDBHelper databaseHelper = null;
	private SQLiteDatabase theDB = null;
	private Context context = null;
	
	public AlarmDBAdapter(Context context) {
		this.context = context;
	}
	
	public AlarmDBAdapter open() throws SQLException {
		this.databaseHelper = new AlarmDBHelper(this.context);
		this.theDB = this.databaseHelper.getWritableDatabase();
		return this;
	}
	
	public void close() throws SQLException {
		if (this.databaseHelper != null) {
			this.databaseHelper.close();
		}
		if (this.theDB != null) {
			this.theDB.close();
		}
	}
	
	//Check here if any errors arise
	public boolean deleteAlarm(long alarmID) {
		return this.theDB.delete(Alarms.ALARM_TABLE_NAME, Alarms.ID + "=" + alarmID, null) > 0;
	}
	
	public long insertAlarm(int alarmEnabled, String alarmName, String alarmDate, String alarmTime, String alarmRingtone, String alarmPicture, String alarmRepeat, int alarmSnooze, int alarmNotify, String alarmEmail) {
		ContentValues initialValues = new ContentValues();
		
		initialValues.put(Alarms.ALARM_ENABLED, alarmEnabled);
		initialValues.put(Alarms.ALARM_NAME, alarmName);
		initialValues.put(Alarms.ALARM_DATE, alarmDate);
		initialValues.put(Alarms.ALARM_TIME, alarmTime);
		initialValues.put(Alarms.ALARM_RINGTONE, alarmRingtone);
		initialValues.put(Alarms.ALARM_PICTURE, alarmPicture);
		initialValues.put(Alarms.ALARM_REPEAT, alarmRepeat);
		initialValues.put(Alarms.ALARM_SNOOZE, alarmSnooze);
		initialValues.put(Alarms.ALARM_NOTIFY, alarmNotify);
		initialValues.put(Alarms.ALARM_EMAIL, alarmEmail);
		
		return theDB.insert(Alarms.ALARM_TABLE_NAME, null, initialValues);
	}
	
	public Cursor fetchAllAlarms() {
		String[] columns = new String[] { Alarms.ID, Alarms.ALARM_ENABLED, Alarms.ALARM_NAME, Alarms.ALARM_DATE, Alarms.ALARM_TIME, Alarms.ALARM_RINGTONE, Alarms.ALARM_PICTURE, Alarms.ALARM_REPEAT, Alarms.ALARM_SNOOZE, Alarms.ALARM_NOTIFY, Alarms.ALARM_EMAIL };
		return this.theDB.query(Alarms.ALARM_TABLE_NAME, columns, null, null, null, null, null);
	}
	
	public Cursor fetchAlarm(long alarmID) {
		String[] columns = new String[] { Alarms.ID, Alarms.ALARM_ENABLED, Alarms.ALARM_NAME, Alarms.ALARM_DATE, Alarms.ALARM_TIME, Alarms.ALARM_RINGTONE, Alarms.ALARM_PICTURE, Alarms.ALARM_REPEAT, Alarms.ALARM_SNOOZE, Alarms.ALARM_NOTIFY, Alarms.ALARM_EMAIL };
		Cursor cursor = this.theDB.query(true, Alarms.ALARM_TABLE_NAME, columns, Alarms.ID + "=" + alarmID, null, null, null, null, null);
	
		if (cursor != null) {
			cursor.moveToFirst();
		}
		
		return cursor;
	}
	
	public boolean updateAlarm(long id, int alarmEnabled, String alarmName, String alarmDate, String alarmTime, String alarmRingtone, String alarmPicture, String alarmRepeat, int alarmSnooze, int alarmNotify, String alarmEmail) {
		ContentValues args = new ContentValues();
		
		args.put(Alarms.ALARM_ENABLED, alarmEnabled);
		args.put(Alarms.ALARM_NAME, alarmName);
		args.put(Alarms.ALARM_DATE, alarmDate);
		args.put(Alarms.ALARM_TIME, alarmTime);
		args.put(Alarms.ALARM_RINGTONE, alarmRingtone);
		args.put(Alarms.ALARM_PICTURE, alarmPicture);
		args.put(Alarms.ALARM_REPEAT, alarmRepeat);
		args.put(Alarms.ALARM_SNOOZE, alarmSnooze);
		args.put(Alarms.ALARM_NOTIFY, alarmNotify);
		args.put(Alarms.ALARM_EMAIL, alarmEmail);
		
		return this.theDB.update(Alarms.ALARM_TABLE_NAME, args, Alarms.ID + "=" + id, null) > 0;
	}
	
	public int getNextAutoIncID() {
		int nextAutoIncID = 0;
		String query = "SELECT * FROM SQLITE_SEQUENCE";
		
		Cursor cursor = this.theDB.rawQuery(query, null);
		if(cursor.moveToFirst()) {
			do{
	            nextAutoIncID = cursor.getColumnIndex("seq");

	        }while (cursor.moveToNext());
		}
		cursor.close();
		
		return nextAutoIncID;
	}

	public void deleteAllAlarms() {
		this.theDB.delete(Alarms.ALARM_TABLE_NAME, "1", null);		
	}
}

