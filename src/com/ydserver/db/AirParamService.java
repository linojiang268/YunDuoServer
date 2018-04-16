package com.ydserver.db;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class AirParamService {
	private String TAG = "AirParamService";
	private DbHelper dbHelper;
	private SQLiteDatabase sdb;
	private Cursor cursor;
	private String table = "air_param_info";

	// private String sql;

	public AirParamService(Context context) {
		dbHelper = new DbHelper(context);
	}

	public boolean insert(AirParamInfo airParamInfo) {
		boolean result = false;
		sdb = dbHelper.getWritableDatabase();
		sdb.beginTransaction();
		StringBuffer sb = new StringBuffer();
		sb.append("insert into ");
		sb.append(table);
		sb.append("(device_id,power,model,wind_speed,wind_direction,temperature,timing,command,name) values(?,?,?,?,?,?,?,?,?)");
		try {
			sdb.execSQL(sb.toString(), new Object[] { airParamInfo.device_id, airParamInfo.power, airParamInfo.model, airParamInfo.wind_speed, airParamInfo.wind_direction, airParamInfo.temperature, airParamInfo.timing, airParamInfo.command, airParamInfo.name });
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
		StringBuffer sb = new StringBuffer();
		sb.append("delete from ");
		sb.append(table);
		sb.append(" where _id=?");
		for (int i = 0; i < _idList.length - 1; i++) {
			sb.append(" or _id=?");
		}
		try {
			Object[] values = new Object[_idList.length];
			for (int i = 0; i < _idList.length; i++) {
				values[i] = _idList[i];
			}
			sdb.execSQL(sb.toString(), values);
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

	public boolean update(AirParamInfo airParamInfo) {
		boolean result = false;
		sdb = dbHelper.getWritableDatabase();
		sdb.beginTransaction();
		StringBuffer sb = new StringBuffer();
		sb.append("update ");
		sb.append(table);
		sb.append(" set command=?,name=? where _id=?");
		try {
			sdb.execSQL(sb.toString(), new Object[] { airParamInfo.command, airParamInfo.name, airParamInfo._id });
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
	public AirParamInfo find(int device_id, int power, int model, int wind_speed, int wind_direction, int temperature) {
		AirParamInfo airParamInfo = null;
		sdb = dbHelper.getWritableDatabase();
		sdb.beginTransaction();
		StringBuffer sb = new StringBuffer();
		sb.append("select * from ");
		sb.append(table);
		String[] strings = null;
		if (0 == power) {
			// 开（多种状态）
			sb.append(" where device_id=? and power=0 and model=? and wind_speed=? and wind_direction=? and temperature=?");
			strings = new String[] { String.valueOf(device_id), String.valueOf(model), String.valueOf(wind_speed), String.valueOf(wind_direction), String.valueOf(temperature) };
		} else {
			// 关（一种状态）
			sb.append(" where device_id=? and power=1");
			strings = new String[] { String.valueOf(device_id) };
		}
		try {
			cursor = sdb.rawQuery(sb.toString(), strings);
			if (cursor.moveToNext()) {
				int _id = cursor.getInt(cursor.getColumnIndex("_id"));
				int device_id_new = cursor.getInt(cursor.getColumnIndex("device_id"));
				int power_new = cursor.getInt(cursor.getColumnIndex("power"));
				int model_new = cursor.getInt(cursor.getColumnIndex("model"));
				int wind_speed_new = cursor.getInt(cursor.getColumnIndex("wind_speed"));
				int wind_direction_new = cursor.getInt(cursor.getColumnIndex("wind_direction"));
				int temperature_new = cursor.getInt(cursor.getColumnIndex("temperature"));
				long timing = cursor.getLong(cursor.getColumnIndex("timing"));
				String command = cursor.getString(cursor.getColumnIndex("command"));
				String name = cursor.getString(cursor.getColumnIndex("name"));
				airParamInfo = new AirParamInfo(_id, device_id_new, power_new, model_new, wind_speed_new, wind_direction_new, temperature_new, timing, command, name);
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
		return airParamInfo;
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
