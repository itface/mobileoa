package koa.android.tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLLiteUtil{
	private static final String TAG = "DatabaseUtil";
	/**
	 * Database Name
	 */
	private static final String DATABASE_NAME = "user_database";

	/**
	 * Database Version
	 */
	private static final int DATABASE_VERSION = 1;

	/**
	 * Table Name
	 */
	private static final String DATABASE_TABLE = "user_table";

	/**
	 * Table columns
	 */
	public static final String KEY_NAME = "name";
	public static final String KEY_PASSWORD = "password";

	/**
	 * Database creation sql statement
	 */
	private static final String CREATE_USER_TABLE =
			"CREATE TABLE IF NOT EXISTS " + DATABASE_TABLE + " (" + KEY_NAME +" text not null, " + KEY_PASSWORD + " text not null);";
	
	/**
	 * Context
	 */
	private final Context mCtx;
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	
	/**
	 * Constructor - takes the context to allow the database to be
	 * opened/created
	 *
	 * @param ctx the Context within which to work
	 */
	public SQLLiteUtil(Context ctx) {
		this.mCtx = ctx;
	}

	/**
	 * 两个作用
	 * 1.getReadableDateBase(),getWritableDatabase()可以获得SQLiteDateBase对象
	 * 2.提供了onCreate()onUpgrade()两个回调函数
	 */
	public class DatabaseHelper extends SQLiteOpenHelper{
		public static final int VERSION = 1;
		//在SQLiteOpenHelper的子类当中，必须有该构造行数
		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		//该函数实在第一次创建数据库的时候执行，实际上是第一次得到SQLiteDateBase对象的时候
		@Override
		public void onCreate(SQLiteDatabase db) {
			System.out.println("create a dataBase");
			db.execSQL(CREATE_USER_TABLE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "	+ newVersion);
		}
	}
	
	/**
	 * This method is used for creating/opening connection
	 * @return instance of DatabaseUtil
	 * @throws SQLException
	 */
	public SQLLiteUtil open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}
	
	/**
	 * This method is used for closing the connection.
	 */
	public void close() {
		mDbHelper.close();
	}
	/**
	 * This method is used to create/insert new record user record.
	 * @param name
	 * @param grade
	 * @return long
	 */
	public long createUser(String name, String password) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_NAME, name);
		initialValues.put(KEY_PASSWORD, password);
		return mDb.insert(DATABASE_TABLE, null, initialValues);
	}
	/**
	 * This method will delete User record.
	 * @param rowId
	 * @return boolean
	 */
	public boolean deleteUser(String user) {
		return mDb.delete(DATABASE_TABLE, KEY_NAME + "='" + user+"'", null) > 0;
	}

	/**
	 * This method will return Cursor holding all the user  records.
	 * @return Cursor
	 */
	public Cursor fetchAllUsers() {
		return mDb.query(DATABASE_TABLE, new String[] {KEY_NAME,
				KEY_PASSWORD}, null, null, null, null, null);
	}

	/**
	 * This method will return Cursor holding the specific user record.
	 * @param id
	 * @return Cursor
	 * @throws SQLException
	 */
	public Cursor fetchUser(String user) throws SQLException {
		Cursor mCursor =
			mDb.query(true, DATABASE_TABLE, new String[] {
					KEY_NAME, KEY_PASSWORD}, KEY_NAME + "='" + user+"'", null,
					null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	/**
	 * This method will update user record.
	 * @param name
	 * @param standard
	 * @return boolean
	 */
	public boolean updateUser(String name, String password) {
		ContentValues args = new ContentValues();
		args.put(KEY_NAME, name);
		args.put(KEY_PASSWORD, password);
		return mDb.update(DATABASE_TABLE, args,KEY_NAME + "='" + name+"'", null) > 0;
	}
}
