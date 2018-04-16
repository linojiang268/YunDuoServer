package com.ydserver.db;

import org.json.JSONArray;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class UserService {
	private String TAG = "UserService";
	private String table = "user_info";
	private DbHelper dbHelper;
	private SQLiteDatabase sdb;
	private Cursor cursor;

	// private String sql;

	public UserService(Context context) {
		dbHelper = new DbHelper(context);
	}

	public boolean insert(UserInfo userInfo) {
		boolean result = false;
		sdb = dbHelper.getWritableDatabase();
		sdb.beginTransaction();
		StringBuffer sql_sb = new StringBuffer();
		sql_sb.append("insert into ");
		sql_sb.append(table);
		sql_sb.append("(id,password,security_phone_numbers) values(?,?,?)");
		try {
			sdb.execSQL(sql_sb.toString(), new Object[] { userInfo.id, userInfo.password, userInfo.security_phone_numbers });
			sdb.setTransactionSuccessful();
			result = true;
		} catch (SQLException e) {
			Log.e(TAG, "insert method fail");
			e.printStackTrace();
		} finally {
			sdb.endTransaction();
			sdb.close();
		}
		return result;
	}

	public boolean update(UserInfo userInfo) {
		boolean result = false;
		sdb = dbHelper.getWritableDatabase();
		sdb.beginTransaction();
		StringBuffer sql_sb = new StringBuffer();
		sql_sb.append("update ");
		sql_sb.append(table);
		sql_sb.append(" set password=?,security_phone_numbers=? where id=?");
		try {
			sdb.execSQL(sql_sb.toString(), new Object[] { userInfo.password, userInfo.security_phone_numbers, userInfo.id });
			sdb.setTransactionSuccessful();
			result = true;
		} catch (SQLException e) {
			Log.e(TAG, "update method fail");
			e.printStackTrace();
		} finally {
			sdb.endTransaction();
			sdb.close();
		}
		return result;
	}

	public UserInfo find(int id) {
		if (getCount() <= 0) {
			insert(new UserInfo(1, "123456", new JSONArray().toString()));
		}
		// ================================
		UserInfo userInfo = null;
		sdb = dbHelper.getWritableDatabase();
		sdb.beginTransaction();
		StringBuffer sql_sb = new StringBuffer();
		sql_sb.append("select * from ").append(table).append(" where id=?");
		try {
			cursor = sdb.rawQuery(sql_sb.toString(), new String[] { String.valueOf(id) });
			if (cursor.moveToNext()) {
				int id_new = cursor.getInt(cursor.getColumnIndex("id"));
				String password = cursor.getString(cursor.getColumnIndex("password"));
				String security_phone_numbers = cursor.getString(cursor.getColumnIndex("security_phone_numbers"));
				userInfo = new UserInfo(id_new, password, security_phone_numbers);
			}
			cursor.close();
			sdb.setTransactionSuccessful();
		} catch (SQLException e) {
			Log.e(TAG, "findScene method fail");
			e.printStackTrace();
		} finally {
			sdb.endTransaction();
			sdb.close();
		}
		return userInfo;
	}

	public long getCount() {
		long count = -1;
		sdb = dbHelper.getWritableDatabase();
		sdb.beginTransaction();
		StringBuffer sql_sb = new StringBuffer();
		sql_sb.append("select count(*) from ");
		sql_sb.append(table);
		try {
			cursor = sdb.rawQuery(sql_sb.toString(), null);
			cursor.moveToFirst();
			count = cursor.getLong(0);
			cursor.close();
			sdb.setTransactionSuccessful();
		} catch (SQLException e) {
			Log.e(TAG, "getCount method fail");
			e.printStackTrace();
		} finally {
			sdb.endTransaction();
			sdb.close();
		}
		return count;
	}
}
