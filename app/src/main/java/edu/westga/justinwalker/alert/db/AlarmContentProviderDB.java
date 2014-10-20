package edu.westga.justinwalker.alert.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.HashSet;

import edu.westga.justinwalker.alert.db.AlarmContract.Alarms;

/**
 * Retrieves data from the database
 * @author Justin Walker and Duane Yoder
 *
 */
public class AlarmContentProviderDB extends ContentProvider {
	
	private static final int ALL_ALARMS = 1;
	private static final int ALARM_ID = 2;
    private static final int HISTORY_ID = 3;
	
	private static final String AUTHORITY = "edu.uwg.justinwalker.visualalarm.alarmsdbprovider";
	private static final String BASE_PATH = "alarms";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

	private static final UriMatcher sURIMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(AUTHORITY, BASE_PATH, ALL_ALARMS);
		sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", ALARM_ID);
	}
	
	private AlarmDBHelper dbHelper;
	
	public boolean onCreate() {
		this.dbHelper = new AlarmDBHelper(getContext());
		return true;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = this.dbHelper.getWritableDatabase();

		int rowsDeleted = 0;

		switch (uriType) {
		case ALL_ALARMS:
			rowsDeleted = sqlDB.delete(
					AlarmContract.Alarms.ALARM_TABLE_NAME,
					selection, selectionArgs);
			break;
		case ALARM_ID:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = sqlDB.delete(
						AlarmContract.Alarms.ALARM_TABLE_NAME,
						AlarmContract.Alarms.ID + "=" + id, null);
			} else {
				rowsDeleted = sqlDB.delete(
						AlarmContract.Alarms.ALARM_TABLE_NAME,
						AlarmContract.Alarms.ID + "=" + id + " and "
								+ selection, selectionArgs);
			}
			break;
        /**
        case HISTORY_ID:
            String historyid = uri.getLastPathSegment();
            if (TextUtils.isEmpty(selection)) {
                rowsDeleted = sqlDB.delete(
                    AlarmContract.Alarms.HISTORY_TABLE_NAME,
                    AlarmContract.Alarms.ID + "=" + historyid, null);
            } else {
                rowsDeleted = sqlDB.delete(
                    AlarmContract.Alarms.HISTORY_TABLE_NAME,
                    AlarmContract.Alarms.ID + "=" + historyid + " and "
                            + selection, selectionArgs);
            }
            break;
         */
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		// Notify potential listeners
		getContext().getContentResolver().notifyChange(uri, null);

		return rowsDeleted;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = this.dbHelper.getWritableDatabase();

		long id = 0;
		switch (uriType) {
		case ALL_ALARMS:
			id = sqlDB.insert(
					AlarmContract.Alarms.ALARM_TABLE_NAME, null,
					values);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);

		return Uri.parse(BASE_PATH + "/" + id);
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

		checkColumns(projection);

		queryBuilder
				.setTables(AlarmContract.Alarms.ALARM_TABLE_NAME);

		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
		case ALL_ALARMS:
			break;
		case ALARM_ID:
			queryBuilder.appendWhere(AlarmContract.Alarms.ID + "="
					+ uri.getLastPathSegment());
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		SQLiteDatabase db = this.dbHelper.getReadableDatabase();
		Cursor cursor = queryBuilder.query(db, projection, selection,
				selectionArgs, null, null, sortOrder);

		// Notify potential listeners
		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;
	}
	
	private void checkColumns(String[] projection) {
		String[] available = new String[] { Alarms.ID, Alarms.ALARM_ENABLED, Alarms.ALARM_NAME, Alarms.ALARM_DATE, Alarms.ALARM_TIME, Alarms.ALARM_RINGTONE, Alarms.ALARM_PICTURE, Alarms.ALARM_REPEAT, Alarms.ALARM_SNOOZE, Alarms.ALARM_NOTIFY, Alarms.ALARM_EMAIL };
	
		if (projection != null) {
			HashSet<String> requestedColumns = new HashSet<String>(
					Arrays.asList(projection));
			HashSet<String> availableColumns = new HashSet<String>(
					Arrays.asList(available));

			if (!availableColumns.containsAll(requestedColumns)) {
				throw new IllegalArgumentException(
						"Unknown columns in projection");
			}
		}
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = this.dbHelper.getWritableDatabase();

		int rowsUpdated = 0;
		switch (uriType) {
		case ALL_ALARMS:
			rowsUpdated = sqlDB.update(
					AlarmContract.Alarms.ALARM_TABLE_NAME,
					values, selection, selectionArgs);
			break;
		case ALARM_ID:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsUpdated = sqlDB.update(
						AlarmContract.Alarms.ALARM_TABLE_NAME,
						values, AlarmContract.Alarms.ID + "=" + id,
						null);
			} else {
				rowsUpdated = sqlDB.update(
						AlarmContract.Alarms.ALARM_TABLE_NAME,
						values, AlarmContract.Alarms.ID + "=" + id
								+ " and " + selection, selectionArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		// Notify potential listeners
		getContext().getContentResolver().notifyChange(uri, null);

		return rowsUpdated;
	}
}

