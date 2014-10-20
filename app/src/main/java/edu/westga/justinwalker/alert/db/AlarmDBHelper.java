package edu.westga.justinwalker.alert.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import edu.westga.justinwalker.alert.db.AlarmContract.Alarms;

/**
 * Sets up the database
 * @author Justin Walker and Duane Yoder
 *
 */
public class AlarmDBHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "alarm.db";
	private static final int DATABASE_VERSION = 1;
	private static final String COMMA_SEP = ", ";
	private static final String TEXT_TYPE = " TEXT";
	private static final String INTEGER_TYPE = " INTEGER";
	//Friendly reminder so I don't spend another 2 hours on this; don't put a comma after the last attribute field
	private static final String SQL_CREATE_TABLE_ALARMS = "CREATE TABLE "
			+ Alarms.ALARM_TABLE_NAME + " (" + Alarms.ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP
			+ Alarms.ALARM_ENABLED + INTEGER_TYPE + COMMA_SEP
			+ Alarms.ALARM_NAME + TEXT_TYPE + COMMA_SEP
			+ Alarms.ALARM_DATE + TEXT_TYPE + COMMA_SEP
			+ Alarms.ALARM_TIME + TEXT_TYPE + COMMA_SEP
			+ Alarms.ALARM_RINGTONE + TEXT_TYPE + COMMA_SEP
			+ Alarms.ALARM_PICTURE + TEXT_TYPE + COMMA_SEP
			+ Alarms.ALARM_REPEAT + TEXT_TYPE + COMMA_SEP
			+ Alarms.ALARM_SNOOZE + INTEGER_TYPE + COMMA_SEP
			+ Alarms.ALARM_NOTIFY + INTEGER_TYPE + COMMA_SEP
			+ Alarms.ALARM_EMAIL + TEXT_TYPE
			+ ");";

    private static final String SQL_CREATE_TABLE_HISTORYS = "CREATE TABLE "
            + Alarms.HISTORY_TABLE_NAME + " (" + Alarms.ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP
            + Alarms.ALARM_ID + INTEGER_TYPE + COMMA_SEP
            + Alarms.HISTORY_TIME + TEXT_TYPE + COMMA_SEP
            + Alarms.HISTORY_DATE + TEXT_TYPE + COMMA_SEP
            + Alarms.HISTORY_ACTION + TEXT_TYPE
            + ");";
	
	public AlarmDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_TABLE_ALARMS);
        db.execSQL(SQL_CREATE_TABLE_HISTORYS);
	}
	
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		
	}
}

