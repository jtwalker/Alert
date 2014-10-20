package edu.westga.justinwalker.alert.db;

import android.provider.BaseColumns;

/**
 * Defines static viables for use in the database
 * @author Justin Walker and Duane Yoder
 *
 */
public final class AlarmContract {
	
	private AlarmContract() {
	}
	
	//Define constants for columns for the table here
	public static abstract class Alarms implements BaseColumns {
		public static final String ALARM_TABLE_NAME = "alarms";
		public static final String ID = "_id";
		public static final String ALARM_ENABLED = "enabled";
		public static final String ALARM_NAME = "name";
		public static final String ALARM_DATE = "date";
		public static final String ALARM_TIME = "time";
		public static final String ALARM_RINGTONE = "ringtone";
		public static final String ALARM_PICTURE = "picture";
		public static final String ALARM_REPEAT = "repeat";
		public static final String ALARM_SNOOZE = "snooze";
		public static final String ALARM_EMAIL = "email";
		public static final String ALARM_NOTIFY = "notify";

        public static final String HISTORY_TABLE_NAME = "history";
        public static final String ALARM_ID = "alarmid";
        public static final String HISTORY_TIME = "time";
        public static final String HISTORY_DATE = "date";
        public static final String HISTORY_ACTION = "action";
		
		private Alarms() {
		}
	}
	
}

