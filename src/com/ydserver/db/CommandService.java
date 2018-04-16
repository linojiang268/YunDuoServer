package com.ydserver.db;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class CommandService {
	private String TAG = "CommandService";
	private DbHelper dbHelper;
	private SQLiteDatabase sdb;
	private Cursor cursor;

	// private String sql;

	public CommandService(Context context) {
		dbHelper = new DbHelper(context);
	}

	/**
	 * 插入设备对应的命令信息
	 * 
	 * @param deviceInfo
	 * @return
	 */
	public boolean insertCommand(CommandInfo commandInfo) {
		boolean result = false;
		sdb = dbHelper.getWritableDatabase();
		sdb.beginTransaction();
		String sql = "insert into command_info(_id,type,command,name) values(?,?,?,?)";
		try {
			sdb.execSQL(sql, new Object[] { commandInfo._id, commandInfo.type, commandInfo.command, commandInfo.name });
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
		StringBuffer sbSql = new StringBuffer();
		sbSql.append("delete from command_info where _id=?");
		for (int i = 0; i < _idList.length - 1; i++) {
			sbSql.append(" or _id=?");
		}
		try {
			Object[] values = new Object[_idList.length];
			for (int i = 0; i < _idList.length; i++) {
				values[i] = _idList[i];
			}
			sdb.execSQL(sbSql.toString(), values);
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

	/**
	 * 根据id和type删除某个命令
	 * 
	 * @param id
	 */
	public boolean deleteCommand(int _id, int type) {
		boolean result = false;
		sdb = dbHelper.getWritableDatabase();
		sdb.beginTransaction();
		String sql = "delete from command_info where _id=? and type=?";
		try {
			sdb.execSQL(sql, new Object[] { _id, type });
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

	/**
	 * 更新Command
	 * 
	 * @param deviceInfo
	 * @return
	 */
	public boolean updateCommand(CommandInfo commandInfo) {
		boolean result = false;
		sdb = dbHelper.getWritableDatabase();
		sdb.beginTransaction();
		String sql = "update command_info set command=?,name=? where _id=? and type=?";
		try {
			sdb.execSQL(sql, new Object[] { commandInfo.command, commandInfo.name, commandInfo._id, commandInfo.type });
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
	public CommandInfo findCommand(int _id, int type) {
		CommandInfo commandInfo = null;
		sdb = dbHelper.getWritableDatabase();
		sdb.beginTransaction();
		String sql = "select * from command_info where _id=? and type=?";
		try {
			cursor = sdb.rawQuery(sql, new String[] { String.valueOf(_id), String.valueOf(type) });
			if (cursor.moveToNext()) {
				int _id_new = cursor.getInt(cursor.getColumnIndex("_id"));
				int type_new = cursor.getInt(cursor.getColumnIndex("type"));
				String command = cursor.getString(cursor.getColumnIndex("command"));
				String name = cursor.getString(cursor.getColumnIndex("name"));
				commandInfo = new CommandInfo(_id_new, type_new, command, name);
			}
			cursor.close();
			sdb.setTransactionSuccessful();
		} catch (SQLException e) {
			Log.e(TAG, "getCount method fail");
			e.printStackTrace();
		} finally {
			sdb.endTransaction();
			sdb.close();
		}
		return commandInfo;
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
