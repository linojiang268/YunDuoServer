package com.ydserver.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DeviceService {
	private String TAG = "DeviceService";
	private DbHelper dbHelper;
	private SQLiteDatabase sdb;
	private Cursor cursor;

	// private String sql;

	public DeviceService(Context context) {
		dbHelper = new DbHelper(context);
	}

	/**
	 * 插入设备信息
	 * 
	 * @param deviceInfo
	 * @return
	 */
	public boolean insertDevice(DeviceInfo deviceInfo) {
		boolean result = false;
		sdb = dbHelper.getWritableDatabase();
		sdb.beginTransaction();
		String sql = "insert into device_info(type,mark,name,content) values(?,?,?,?)";
		try {
			sdb.execSQL(sql, new Object[] { deviceInfo.type, deviceInfo.mark, deviceInfo.name, deviceInfo.content });
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
		sbSql.append("delete from device_info where _id=?");
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
	 * 更新device
	 * 
	 * @param deviceInfo
	 * @return
	 */
	public boolean updateDevice(DeviceInfo deviceInfo) {
		boolean result = false;
		sdb = dbHelper.getWritableDatabase();
		sdb.beginTransaction();
		String sql = "update device_info set type=?,mark=?,name=?,content=? where _id=?";
		try {
			sdb.execSQL(sql, new Object[] { deviceInfo.type, deviceInfo.mark, deviceInfo.name, deviceInfo.content, deviceInfo._id });
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

	/**
	 * 更新device状态
	 * 
	 * @param deviceInfo
	 * @return
	 */
	public boolean updateDevice(int _id, String content) {
		boolean result = false;
		sdb = dbHelper.getWritableDatabase();
		sdb.beginTransaction();
		String sql = "update device_info set content=? where _id=?";
		try {
			sdb.execSQL(sql, new Object[] { content, _id });
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

	public DeviceInfo findDevice(int _id) {
		DeviceInfo deviceInfo = null;
		sdb = dbHelper.getWritableDatabase();
		sdb.beginTransaction();
		String sql = "select * from device_info where _id=?";
		try {
			cursor = sdb.rawQuery(sql, new String[] { String.valueOf(_id) });
			if (cursor.moveToNext()) {
				int _id_new = cursor.getInt(cursor.getColumnIndex("_id"));
				int type = cursor.getInt(cursor.getColumnIndex("type"));
				String mark = cursor.getString(cursor.getColumnIndex("mark"));
				String name = cursor.getString(cursor.getColumnIndex("name"));
				String content = cursor.getString(cursor.getColumnIndex("content"));
				deviceInfo = new DeviceInfo(_id_new, type, mark, name, content);
			}
			cursor.close();
			sdb.setTransactionSuccessful();
		} catch (SQLException e) {
			Log.e(TAG, "findDevice method fail");
			e.printStackTrace();
		} finally {
			sdb.endTransaction();
			sdb.close();
		}
		return deviceInfo;
	}

	/**
	 * 根据mark查询某个设备
	 * 
	 * @param mark
	 * @return
	 */
	public DeviceInfo findDevice(int deviceType, String mark) {
		DeviceInfo deviceInfo = null;
		sdb = dbHelper.getWritableDatabase();
		sdb.beginTransaction();
		String sql = "select * from device_info where type=? and mark=?";
		try {
			cursor = sdb.rawQuery(sql, new String[] { String.valueOf(deviceType), mark });
			if (cursor.moveToNext()) {
				int _id = cursor.getInt(cursor.getColumnIndex("_id"));
				int type = cursor.getInt(cursor.getColumnIndex("type"));
				String mark_new = cursor.getString(cursor.getColumnIndex("mark"));
				String name = cursor.getString(cursor.getColumnIndex("name"));
				String content = cursor.getString(cursor.getColumnIndex("content"));
				deviceInfo = new DeviceInfo(_id, type, mark_new, name, content);
			}
			cursor.close();
			sdb.setTransactionSuccessful();
		} catch (SQLException e) {
			Log.e(TAG, "findDevice method fail");
			e.printStackTrace();
		} finally {
			sdb.endTransaction();
			sdb.close();
		}
		return deviceInfo;
	}

	/**
	 * 查出最后一个设备
	 * 
	 * @param mark
	 * @return
	 */
	public DeviceInfo findLastDevice() {
		DeviceInfo deviceInfo = null;
		sdb = dbHelper.getWritableDatabase();
		sdb.beginTransaction();
		String sql = "select * from device_info order by _id desc";
		try {
			cursor = sdb.rawQuery(sql, null);
			if (cursor.moveToNext()) {
				int _id = cursor.getInt(cursor.getColumnIndex("_id"));
				int type = cursor.getInt(cursor.getColumnIndex("type"));
				String mark_new = cursor.getString(cursor.getColumnIndex("mark"));
				String name = cursor.getString(cursor.getColumnIndex("name"));
				String content = cursor.getString(cursor.getColumnIndex("content"));
				deviceInfo = new DeviceInfo(_id, type, mark_new, name, content);
			}
			cursor.close();
			sdb.setTransactionSuccessful();
		} catch (SQLException e) {
			Log.e(TAG, "findLastDevice method fail");
			e.printStackTrace();
		} finally {
			sdb.endTransaction();
			sdb.close();
		}
		return deviceInfo;
	}

	public List<DeviceInfo> getDeviceList(int offset, int maxSize, int... typeList) {
		List<DeviceInfo> deviceList = null;
		sdb = dbHelper.getWritableDatabase();
		sdb.beginTransaction();
		StringBuffer sbSql = new StringBuffer();
		sbSql.append("select * from device_info where type=?");
		for (int i = 0; i < typeList.length - 1; i++) {
			sbSql.append(" or type=?");
		}
		sbSql.append(" order by _id desc");
		if (!(-1 == offset && -1 == maxSize)) {
			sbSql.append(" limit ?,?");
		}
		try {
			if (-1 == offset && -1 == maxSize) {
				String[] values = new String[typeList.length];
				for (int i = 0; i < typeList.length; i++) {
					values[i] = String.valueOf(typeList[i]);
				}
				cursor = sdb.rawQuery(sbSql.toString(), values);
			} else {
				String[] values = new String[typeList.length + 2];
				for (int i = 0; i < typeList.length; i++) {
					values[i] = String.valueOf(typeList[i]);
				}
				values[values.length - 2] = String.valueOf(offset);
				values[values.length - 1] = String.valueOf(maxSize);
				cursor = sdb.rawQuery(sbSql.toString(), values);
			}
			while (cursor.moveToNext()) {
				int _id = cursor.getInt(cursor.getColumnIndex("_id"));
				int type_new = cursor.getInt(cursor.getColumnIndex("type"));
				String mark = cursor.getString(cursor.getColumnIndex("mark"));
				String name = cursor.getString(cursor.getColumnIndex("name"));
				String content = cursor.getString(cursor.getColumnIndex("content"));
				if (null == deviceList) {
					deviceList = new ArrayList<DeviceInfo>();
				}
				deviceList.add(new DeviceInfo(_id, type_new, mark, name, content));
			}
			cursor.close();
			sdb.setTransactionSuccessful();
		} catch (SQLException e) {
			Log.e(TAG, "getDeviceList method fail");
			e.printStackTrace();
		} finally {
			sdb.endTransaction();
			sdb.close();
		}
		return deviceList;
	}

	/**
	 * 获取某些type的消息数目
	 * 
	 * @return
	 */
	public long getCountWithType(int... typeList) {
		long count = -1;
		sdb = dbHelper.getWritableDatabase();
		sdb.beginTransaction();
		StringBuffer sbSql = new StringBuffer();
		sbSql.append("select count(*) from device_info where type=?");
		for (int i = 0; i < typeList.length - 1; i++) {
			sbSql.append(" or type=?");
		}
		try {
			String[] values = new String[typeList.length];
			for (int i = 0; i < typeList.length; i++) {
				values[i] = String.valueOf(typeList[i]);
			}
			cursor = sdb.rawQuery(sbSql.toString(), values);
			cursor.moveToFirst();
			count = cursor.getLong(0);
			cursor.close();
			sdb.setTransactionSuccessful();
		} catch (SQLException e) {
			Log.e(TAG, "getCountWithType method fail");
			e.printStackTrace();
		} finally {
			sdb.endTransaction();
			sdb.close();
		}
		return count;
	}

}
