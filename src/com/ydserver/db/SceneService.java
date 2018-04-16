package com.ydserver.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SceneService {
	private String TAG = "SceneService";
	private String table = "scene_info";
	private DbHelper dbHelper;
	private SQLiteDatabase sdb;
	private Cursor cursor;

	// private String sql;

	public SceneService(Context context) {
		dbHelper = new DbHelper(context);
	}

	public boolean insertSceneInfo(SceneInfo sceneInfo) {
		boolean result = false;
		sdb = dbHelper.getWritableDatabase();
		sdb.beginTransaction();
		StringBuffer sql_sb = new StringBuffer();
		sql_sb.append("insert into ");
		sql_sb.append(table);
		sql_sb.append("(name,isUsed,command) values(?,?,?)");
		try {
			sdb.execSQL(sql_sb.toString(), new Object[] { sceneInfo.name, sceneInfo.isUsed, sceneInfo.command });
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

	/**
	 * 根据id删除
	 * 
	 * @param id
	 */
	public boolean delete(int... _idList) {
		boolean result = false;
		sdb = dbHelper.getWritableDatabase();
		sdb.beginTransaction();
		StringBuffer sql_sb = new StringBuffer();
		sql_sb.append("delete from ");
		sql_sb.append(table);
		sql_sb.append(" where _id=?");
		for (int i = 0; i < _idList.length - 1; i++) {
			sql_sb.append(" or _id=?");
		}
		try {
			Object[] values = new Object[_idList.length];
			for (int i = 0; i < _idList.length; i++) {
				values[i] = _idList[i];
			}
			sdb.execSQL(sql_sb.toString(), values);
			// sdb.execSQL(sql, new Object[] { _id });
			sdb.setTransactionSuccessful();
			result = true;
		} catch (SQLException e) {
			Log.e(TAG, "delete method fail");
			e.printStackTrace();
		} finally {
			sdb.endTransaction();
			sdb.close();
		}
		return result;
	}

	public boolean updateScence(SceneInfo sceneInfo) {
		boolean result = false;
		sdb = dbHelper.getWritableDatabase();
		sdb.beginTransaction();
		StringBuffer sql_sb = new StringBuffer();
		sql_sb.append("update ");
		sql_sb.append(table);
		sql_sb.append(" set name=?,isUsed=?,command=? where _id=?");
		try {
			sdb.execSQL(sql_sb.toString(), new Object[] { sceneInfo.name, sceneInfo.isUsed, sceneInfo.command, sceneInfo._id });
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

	public SceneInfo findScene(int _id) {
		SceneInfo sceneInfo = null;
		sdb = dbHelper.getWritableDatabase();
		sdb.beginTransaction();
		StringBuffer sql_sb = new StringBuffer();
		sql_sb.append("select * from ").append(table).append(" where _id=?");
		try {
			cursor = sdb.rawQuery(sql_sb.toString(), new String[] { String.valueOf(_id) });
			if (cursor.moveToNext()) {
				int _id_new = cursor.getInt(cursor.getColumnIndex("_id"));
				String name = cursor.getString(cursor.getColumnIndex("name"));
				int isUsed = cursor.getInt(cursor.getColumnIndex("isUsed"));
				String command = cursor.getString(cursor.getColumnIndex("command"));
				sceneInfo = new SceneInfo(_id_new, name, isUsed, command);
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
		return sceneInfo;
	}

	public SceneInfo findLastScene() {
		SceneInfo sceneInfo = null;
		sdb = dbHelper.getWritableDatabase();
		sdb.beginTransaction();
		StringBuffer sql_sb = new StringBuffer();
		sql_sb.append("select * from ").append(table).append(" order by _id desc");
		try {
			cursor = sdb.rawQuery(sql_sb.toString(), null);
			if (cursor.moveToNext()) {
				int _id = cursor.getInt(cursor.getColumnIndex("_id"));
				String name = cursor.getString(cursor.getColumnIndex("name"));
				int isUsed = cursor.getInt(cursor.getColumnIndex("isUsed"));
				String command = cursor.getString(cursor.getColumnIndex("command"));
				sceneInfo = new SceneInfo(_id, name, isUsed, command);
			}
			cursor.close();
			sdb.setTransactionSuccessful();
		} catch (SQLException e) {
			Log.e(TAG, "findLastScene method fail");
			e.printStackTrace();
		} finally {
			sdb.endTransaction();
			sdb.close();
		}
		return sceneInfo;
	}

	/**
	 * 根据mac搜场景
	 * 
	 * @param mac
	 * @return
	 */
	public SceneInfo findSceneMac(String mac) {
		if (null == mac) {
			return null;
		}
		SceneInfo sceneInfo = null;
		sdb = dbHelper.getWritableDatabase();
		sdb.beginTransaction();
		StringBuffer sql_sb = new StringBuffer();
		sql_sb.append("select * from ").append(table).append(" where command=?");
		try {
			cursor = sdb.rawQuery(sql_sb.toString(), new String[] { mac });
			if (cursor.moveToNext()) {
				int _id_new = cursor.getInt(cursor.getColumnIndex("_id"));
				String name = cursor.getString(cursor.getColumnIndex("name"));
				int isUsed = cursor.getInt(cursor.getColumnIndex("isUsed"));
				String command = cursor.getString(cursor.getColumnIndex("command"));
				sceneInfo = new SceneInfo(_id_new, name, isUsed, command);
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
		return sceneInfo;
	}

	public List<SceneInfo> getSceneList(int offset, int maxSize, int isUsed) {
		List<SceneInfo> sceneList = null;
		sdb = dbHelper.getWritableDatabase();
		sdb.beginTransaction();
		StringBuffer sql_sb = new StringBuffer();
		sql_sb.append("select * from ").append(table).append(" where isUsed=?");
		sql_sb.append(" order by _id desc");
		if (!(-1 == offset && -1 == maxSize)) {
			sql_sb.append(" limit ?,?");
		}
		try {
			if (-1 == offset && -1 == maxSize) {
				cursor = sdb.rawQuery(sql_sb.toString(), new String[] { String.valueOf(isUsed) });
			} else {
				cursor = sdb.rawQuery(sql_sb.toString(), new String[] { String.valueOf(isUsed), String.valueOf(offset), String.valueOf(maxSize) });
			}
			while (cursor.moveToNext()) {
				int _id = cursor.getInt(cursor.getColumnIndex("_id"));
				String name = cursor.getString(cursor.getColumnIndex("name"));
				int isUsed_new = cursor.getInt(cursor.getColumnIndex("isUsed"));
				String command = cursor.getString(cursor.getColumnIndex("command"));
				if (null == sceneList) {
					sceneList = new ArrayList<SceneInfo>();
				}
				sceneList.add(new SceneInfo(_id, name, isUsed_new, command));
			}
			cursor.close();
			sdb.setTransactionSuccessful();
		} catch (SQLException e) {
			Log.e(TAG, "getSceneList method fail");
			e.printStackTrace();
		} finally {
			sdb.endTransaction();
			sdb.close();
		}
		return sceneList;
	}

	public long getCount(int isUsed) {
		long count = -1;
		sdb = dbHelper.getWritableDatabase();
		sdb.beginTransaction();
		StringBuffer sql_sb = new StringBuffer();
		sql_sb.append("select count(*) from ");
		sql_sb.append(table).append(" where isUsed=?");
		try {
			cursor = sdb.rawQuery(sql_sb.toString(), new String[] { String.valueOf(isUsed) });
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
