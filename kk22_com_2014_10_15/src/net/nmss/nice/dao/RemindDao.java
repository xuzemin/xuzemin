package net.nmss.nice.dao;

import java.util.ArrayList;
import java.util.List;

import net.nmss.nice.bean.RemindEntity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RemindDao {
	private final static String LOG_TAG = "RemindDao";
	private final static String REMIND_TABLE = "REMIND_TABLE";
	private RemindDBHelper mDBHelper;
	
	public RemindDao(Context context) {
		mDBHelper = new RemindDBHelper(context);
	}
	
	public synchronized long addRemind(RemindEntity entity) {
		long rowId = -1;
		ContentValues values = entity.parseToDBEntity();
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		rowId = db.insert(REMIND_TABLE, null, values);
		db.close();
		return rowId;
	}
	
	public synchronized int deleteRemind(long remindID) {
		int count = -1;
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		count = db.delete(REMIND_TABLE, "_id="+remindID, null);
		db.close();
		return count;
	}
	
	public synchronized int updateRemind(RemindEntity entity,Context context) {
		int count = -1;
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		ContentValues values = entity.parseToDBEntity();
		count = db.update(REMIND_TABLE, values, "_id="+entity.getId(), null);
		return count;
	}
	
	public synchronized List<RemindEntity> getReminds() {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from "+ REMIND_TABLE, null);
		List<RemindEntity> reminds = null;
		if(cursor!=null && cursor.moveToFirst()) {
			reminds = new ArrayList<RemindEntity>();
			 do {
				RemindEntity entity = new RemindEntity();
				entity.setActivate(cursor.getInt(cursor.getColumnIndex("status")));
				entity.setId(cursor.getInt(cursor.getColumnIndex("_id")));
				entity.setTime(cursor.getLong(cursor.getColumnIndex("remind_time")));
				reminds.add(entity);
			} while(cursor.moveToNext());
		}
			
		db.close();
		return reminds;
	}
	
	private class RemindDBHelper extends SQLiteOpenHelper {
		public RemindDBHelper(Context context) {
			super(context, REMIND_TABLE, null, 1);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub]
			createBD(db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("drop table if exists " + REMIND_TABLE);
			createBD(db);
		}

		private void createBD(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE "+REMIND_TABLE+"(" +
					  "[_id] INTEGER PRIMARY KEY autoincrement,"+
					  "[remind_time] LONG,"+
					  "[status] INTEGER);");
		}
	}
}
