package com.maomuffy.medicnaija;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MedicNaijaFeedData {
	private static final String MEDICNAIJA_LOG = "com.maomuffy.medicnaija";

	public static final String DB_NAME = "medicnaijafeed.db";
	public static final int DB_VERSION = 2;
	public static final String DB_TABLE = "feeds";
	public static final String C_ID = "_id";
	public static final String C_TITLE = "title";
	public static final String C_DESCRIPTION = "description";
	public static final String C_LINK = "link";
	public static final String C_PUBDATE = "pubDate";

	Context ct;
	SQLiteDatabase db;
	MedicNaijaDbHelper dbHelper;

	public MedicNaijaFeedData(Context context) {
		this.ct = context;
		dbHelper = new MedicNaijaDbHelper();
	}

	public void insertFeed(Feeds feed) {
		db = dbHelper.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(C_TITLE, feed.getTitle());
		cv.put(C_DESCRIPTION, feed.getDescription());
		cv.put(C_LINK, feed.getLink());
		cv.put(C_PUBDATE, feed.getPubDate());

		db.insertWithOnConflict(DB_TABLE, null, cv,
				SQLiteDatabase.CONFLICT_IGNORE);
		db.close();
	}

	public List<String> getFeeds() {
		List<String> feedsArrayList = new ArrayList<String>();
		db = dbHelper.getReadableDatabase();
		String[] feedsSelectColumns = new String[] { C_ID, C_TITLE };
		Cursor feedResult = db.query(DB_TABLE, feedsSelectColumns, null, null,
				null, null, C_PUBDATE + " DESC", "3");
		feedResult.moveToFirst();
		while (!feedResult.isAfterLast()) {
			feedsArrayList.add(feedResult.getString(feedResult
					.getColumnIndex(C_TITLE)));
			feedResult.moveToNext();
		}
		feedResult.close();
		db.close();
		return feedsArrayList;
	}

	public Cursor getOneFeed(int feedId) {
		db = dbHelper.getReadableDatabase();
		String[] feedsSelectColumns = new String[] { C_ID, C_TITLE };
		Cursor feedResult = db.query(DB_TABLE, feedsSelectColumns, C_ID + "="
				+ feedId, null, null, null, null);
		return feedResult;
	}

	public void updateFeed(int updateId, String title) {
		db = dbHelper.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(C_TITLE, title);
		db.updateWithOnConflict(DB_TABLE, cv, C_ID + "=" + updateId, null,
				SQLiteDatabase.CONFLICT_IGNORE);
		db.close();
	}

	public void deleteAllFeeds(int deleteId) {
		db = dbHelper.getWritableDatabase();
		db.delete(DB_TABLE, C_ID + "=" + deleteId, null);
		db.close();
	}

	public void resetFeedsDbTable() {
		db = dbHelper.getWritableDatabase();
		String resetSql = "DELETE FROM " + DB_TABLE + ";";
		String resetIndexSql = "DELETE FROM SQLITE_SEQUENCE WHERE name='"
				+ DB_TABLE + "';";
		db.execSQL(resetSql);
		db.execSQL(resetIndexSql);
		Log.i(MEDICNAIJA_LOG, "Reset Table: " + resetSql);
		Log.i(MEDICNAIJA_LOG, "Reset Index: " + resetIndexSql);
		db.close();
	}

	class MedicNaijaDbHelper extends SQLiteOpenHelper {

		public MedicNaijaDbHelper() {
			super(ct, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			String sql = String
					.format("CREATE TABLE %s ("
							+ "%s INTEGER PRIMARY KEY AUTOINCREMENT,%s text,%s text,%s text, %s text);",
							DB_TABLE, C_ID, C_TITLE, C_DESCRIPTION, C_LINK,
							C_PUBDATE);
			Log.i(MEDICNAIJA_LOG, "onCreate ran: " + sql);
			db.execSQL(sql);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			String sql = "DROP TABLE IF EXISTS " + DB_TABLE;
			db.execSQL(sql);
			Log.i(MEDICNAIJA_LOG, "Upgrading DB schema from " + oldVersion
					+ " to " + newVersion);
			// Lazy man's way - Marko Gargenta, Marakana Inc.
			onCreate(db);
		}

	}

}
