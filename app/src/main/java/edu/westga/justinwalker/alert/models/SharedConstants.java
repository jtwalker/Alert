package edu.westga.justinwalker.alert.models;

import edu.westga.justinwalker.alert.db.AlarmContract.Alarms;

public class SharedConstants {

	public static final int ALARM_TRUE = 1;
	public static final int ALARM_FALSE = 0;
	public static final String USER_PREFS = "UserPrefsFils";
	public static final String[] ALARM_COLUMNS = new String[] { Alarms.ID,
			Alarms.ALARM_ENABLED, Alarms.ALARM_NAME, Alarms.ALARM_DATE,
			Alarms.ALARM_TIME, Alarms.ALARM_RINGTONE, Alarms.ALARM_PICTURE,
			Alarms.ALARM_REPEAT, Alarms.ALARM_SNOOZE, Alarms.ALARM_NOTIFY,
			Alarms.ALARM_EMAIL };
    public static final String REPEATING_FALSE = "false";

    public static final String MONDAY = "Mon";
    public static final String TUESDAY = "Tues";
    public static final String WEDNESDAY = "Wed";
    public static final String THURSDAY = "Thurs";
    public static final String FRIDAY = "Fri";
    public static final String SATURDAY = "Sat";
    public static final String SUNDAY = "Sun";

	private SharedConstants() {

	}

}
