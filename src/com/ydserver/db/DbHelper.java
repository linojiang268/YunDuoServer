package com.ydserver.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库辅助类
 * 
 * @author ouArea
 * @date 2013-1-21
 */
public class DbHelper extends SQLiteOpenHelper {
	private final static String DB_NAME = "db_clouds";
	private final static int VERSION = 1;

	public DbHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// 创建数据库、表
		String sql = "create table user_info(id Integer primary key,password text,security_phone_numbers text)";
		db.execSQL(sql);
		sql = "create table device_info(_id Integer primary key autoincrement,type Integer,mark text,name text,content text)";
		db.execSQL(sql);
		sql = "create table command_info(_id Integer,type Integer,command text,name text)";
		db.execSQL(sql);
		sql = "create table air_param_info(_id Integer primary key autoincrement,device_id Integer,power Integer,model Integer,wind_speed Integer,wind_direction Integer,temperature Integer,timing Long,command text,name text)";
		db.execSQL(sql);
		sql = "create table scene_info(_id Integer primary key autoincrement,name text,isUsed Integer,command text)";
		db.execSQL(sql);
		sql = "create table scene_command_msg(id Integer primary key autoincrement,_id Integer,d_id Integer,c_type Integer,c_msg text,name text)";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// db.execSQL("ALTER TABLE user ADD grade integer");
	}

}
