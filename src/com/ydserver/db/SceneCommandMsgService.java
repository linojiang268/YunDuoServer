package com.ydserver.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SceneCommandMsgService {
	private String TAG = "SceneCommandMsg";
	private String table = "scene_command_msg";
	private DbHelper dbHelper;
	private SQLiteDatabase sdb;
	private Cursor cursor;

	// private String sql;

	public SceneCommandMsgService(Context context) {
		dbHelper = new DbHelper(context);
	}

	public boolean insertCommandMsg(SceneCommandMsg sceneCommandMsg) {
		boolean result = false;
		sdb = dbHelper.getWritableDatabase();
		sdb.beginTransaction();
		StringBuffer sql_sb = new StringBuffer();
		sql_sb.append("insert into ");
		sql_sb.append(table);
		sql_sb.append("(_id,d_id,c_type,c_msg,name) values(?,?,?,?,?)");
		try {
			sdb.execSQL(sql_sb.toString(), new Object[] { sceneCommandMsg._id, sceneCommandMsg.d_id, sceneCommandMsg.c_type, sceneCommandMsg.c_msg, sceneCommandMsg.name });
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

	public boolean deleteCommandMsg(int _id, int d_id) {
		boolean result = false;
		sdb = dbHelper.getWritableDatabase();
		sdb.beginTransaction();
		StringBuffer sql_sb = new StringBuffer();
		sql_sb.append("delete from ");
		sql_sb.append(table);
		sql_sb.append(" where _id=? and d_id=?");
		try {
			sdb.execSQL(sql_sb.toString(), new Object[] { _id, d_id });
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

	public boolean deleteCommandMsgWithOut(int _id, SceneCommandMsg... msgs) {
		boolean result = false;
		sdb = dbHelper.getWritableDatabase();
		sdb.beginTransaction();
		StringBuffer sql_sb = new StringBuffer();
		sql_sb.append("delete from ");
		sql_sb.append(table);
		// sql_sb.append(" where  (_id<>? or d_id<>?)");
		// for (int i = 1; i < msgs.length; i++) {
		// sql_sb.append(" and (_id<>? or d_id<>?)");
		// }
		sql_sb.append(" where _id=? and d_id<>?");
		for (int i = 1; i < msgs.length; i++) {
			sql_sb.append(" and d_id<>?");
		}
		try {
			// Object[] objects = new Object[msgs.length * 2];
			// for (int i = 0; i < msgs.length; i++) {
			// objects[i * 2] = msgs[i]._id;
			// objects[i * 2 + 1] = msgs[i].d_id;
			// }
			Object[] objects = new Object[msgs.length + 1];
			objects[0] = _id;
			for (int i = 1; i < objects.length; i++) {
				objects[i] = msgs[i - 1].d_id;
			}
			sdb.execSQL(sql_sb.toString(), objects);
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

	public boolean updateCommandMsg(SceneCommandMsg sceneCommandMsg) {
		boolean result = false;
		sdb = dbHelper.getWritableDatabase();
		sdb.beginTransaction();
		StringBuffer sql_sb = new StringBuffer();
		sql_sb.append("update ").append(table).append(" set c_type=?,c_msg=?,name=? where _id=? and d_id=?");
		try {
			sdb.execSQL(sql_sb.toString(), new Object[] { sceneCommandMsg.c_type, sceneCommandMsg.c_msg, sceneCommandMsg.name, sceneCommandMsg._id, sceneCommandMsg.d_id });
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

	// 查询某个设备的命令
	public SceneCommandMsg findCommandMsg(int _id, int d_id) {
		SceneCommandMsg sceneCommandMsg = null;
		sdb = dbHelper.getWritableDatabase();
		sdb.beginTransaction();
		StringBuffer sql_sb = new StringBuffer();
		sql_sb.append("select * from ");
		sql_sb.append(table);
		sql_sb.append(" where _id=? and d_id=?");
		try {
			cursor = sdb.rawQuery(sql_sb.toString(), new String[] { String.valueOf(_id), String.valueOf(d_id) });
			if (cursor.moveToNext()) {
				int id = cursor.getInt(cursor.getColumnIndex("id"));
				int _id_new = cursor.getInt(cursor.getColumnIndex("_id"));
				int d_id_new = cursor.getInt(cursor.getColumnIndex("d_id"));
				int c_type = cursor.getInt(cursor.getColumnIndex("c_type"));
				String c_msg = cursor.getString(cursor.getColumnIndex("c_msg"));
				String name = cursor.getString(cursor.getColumnIndex("name"));
				sceneCommandMsg = new SceneCommandMsg(id, _id_new, d_id_new, c_type, c_msg, name);
			}
			cursor.close();
			sdb.setTransactionSuccessful();
		} catch (SQLException e) {
			Log.e(TAG, "findCommandMsg method fail");
			e.printStackTrace();
		} finally {
			sdb.endTransaction();
			sdb.close();
		}
		return sceneCommandMsg;
	}

	public List<SceneCommandMsg> getSceneCommandMsgList(int offset, int maxSize, int _id) {
		List<SceneCommandMsg> commandMsgList = null;
		sdb = dbHelper.getWritableDatabase();
		sdb.beginTransaction();
		StringBuffer sql_sb = new StringBuffer();
		sql_sb.append("select * from ").append(table).append(" where _id=?");
		sql_sb.append(" order by id asc");
		if (!(-1 == offset && -1 == maxSize)) {
			sql_sb.append(" limit ?,?");
		}
		try {
			if (-1 == offset && -1 == maxSize) {
				cursor = sdb.rawQuery(sql_sb.toString(), new String[] { String.valueOf(_id) });
			} else {
				cursor = sdb.rawQuery(sql_sb.toString(), new String[] { String.valueOf(_id), String.valueOf(offset), String.valueOf(maxSize) });
			}
			while (cursor.moveToNext()) {
				int id = cursor.getInt(cursor.getColumnIndex("id"));
				int _id_new = cursor.getInt(cursor.getColumnIndex("_id"));
				int d_id = cursor.getInt(cursor.getColumnIndex("d_id"));
				int c_type = cursor.getInt(cursor.getColumnIndex("c_type"));
				String c_msg = cursor.getString(cursor.getColumnIndex("c_msg"));
				String name = cursor.getString(cursor.getColumnIndex("name"));
				if (null == commandMsgList) {
					commandMsgList = new ArrayList<SceneCommandMsg>();
				}
				commandMsgList.add(new SceneCommandMsg(id, _id_new, d_id, c_type, c_msg, name));
			}
			cursor.close();
			sdb.setTransactionSuccessful();
		} catch (SQLException e) {
			Log.e(TAG, "getCommandMsgList method fail");
			e.printStackTrace();
		} finally {
			sdb.endTransaction();
			sdb.close();
		}
		return commandMsgList;
	}

	// 获取列表
	// protected List<DeviceInfo> getUserList(int offset, int maxSize, int...
	// typeList) {
	// List<DeviceInfo> deviceList = null;
	// sdb = dbHelper.getWritableDatabase();
	// sdb.beginTransaction();
	// StringBuffer sbSql = new StringBuffer();
	// sbSql.append("select * from device_info where type=?");
	// for (int i = 0; i < typeList.length - 1; i++) {
	// sbSql.append(" or type=?");
	// }
	// sbSql.append(" order by _id desc");
	// if (!(-1 == offset && -1 == maxSize)) {
	// sbSql.append(" limit ?,?");
	// }
	// try {
	// if (-1 == offset && -1 == maxSize) {
	// String[] values = new String[typeList.length];
	// for (int i = 0; i < typeList.length; i++) {
	// values[0] = String.valueOf(typeList[0]);
	// }
	// cursor = sdb.rawQuery(sbSql.toString(), values);
	// } else {
	// String[] values = new String[typeList.length + 2];
	// for (int i = 0; i < typeList.length; i++) {
	// values[0] = String.valueOf(typeList[0]);
	// }
	// values[values.length - 2] = String.valueOf(offset);
	// values[values.length - 1] = String.valueOf(maxSize);
	// cursor = sdb.rawQuery(sbSql.toString(), values);
	// }
	// while (cursor.moveToNext()) {
	// int _id = cursor.getInt(cursor.getColumnIndex("_id"));
	// int type_new = cursor.getInt(cursor.getColumnIndex("type"));
	// String mark = cursor.getString(cursor.getColumnIndex("mark"));
	// String name = cursor.getString(cursor.getColumnIndex("name"));
	// if (null == deviceList) {
	// deviceList = new ArrayList<DeviceInfo>();
	// }
	// deviceList.add(new DeviceInfo(_id, type_new, mark, name));
	// }
	// cursor.close();
	// sdb.setTransactionSuccessful();
	// } catch (SQLException e) {
	// Log.e(TAG, "getCount method fail");
	// e.printStackTrace();
	// } finally {
	// sdb.endTransaction();
	// sdb.close();
	// }
	// return deviceList;
	// }

	// /**
	// * 获取某些type的消息数目
	// *
	// * @return
	// */
	// public long getCountWithType(int... typeList) {
	// long count = -1;
	// sdb = dbHelper.getWritableDatabase();
	// sdb.beginTransaction();
	// StringBuffer sbSql = new StringBuffer();
	// sbSql.append("select count(*) from device_info where type=?");
	// for (int i = 0; i < typeList.length - 1; i++) {
	// sbSql.append(" or type=?");
	// }
	// try {
	// String[] values = new String[typeList.length];
	// for (int i = 0; i < typeList.length; i++) {
	// values[0] = String.valueOf(typeList[0]);
	// }
	// cursor = sdb.rawQuery(sbSql.toString(), values);
	// cursor.moveToFirst();
	// count = cursor.getLong(0);
	// cursor.close();
	// sdb.setTransactionSuccessful();
	// } catch (SQLException e) {
	// Log.e(TAG, "getCount method fail");
	// e.printStackTrace();
	// } finally {
	// sdb.endTransaction();
	// sdb.close();
	// }
	// return count;
	// }

}
