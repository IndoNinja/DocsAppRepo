package com.sns.docsapp.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.sns.docsapp.Model.MessageModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABSE_NAME = "DOC_APP_CHAT_DB";
    private String TABLE_NAME = "CHAT_TABLE";
    private String TABLE_REGEX = "CHAT_TABLE";
    private static final String KEY_TIME = "_time";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_IS_SELF = "selfChat";
    private static final String KEY_IS_DATE = "selfdate";
    private static final String[] COLUMNS = {KEY_TIME, KEY_MESSAGE, KEY_IS_SELF, KEY_IS_DATE};

    //private String oldtableName;

    private static Context mContext;
    private static ArrayList<String> mTableList;

    private String CREATION_TABLE = "CREATE TABLE " + TABLE_REGEX + "( "
            + KEY_TIME + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_MESSAGE + " TEXT, "
            + KEY_IS_SELF + " INT, " + KEY_IS_DATE + " INT)";

    private static SQLiteHelper mSqLiteHelper = null;

    public synchronized static SQLiteHelper getInstance(Context context, ArrayList<String> tableList) {
        if (mSqLiteHelper == null) {
            mTableList = tableList;
            mContext = context;
        }
        mSqLiteHelper = new SQLiteHelper(context);
        return mSqLiteHelper;
    }

    private SQLiteHelper(Context context) {
        super(context, DATABSE_NAME, null, DATABASE_VERSION);
    }

    public void UpdateCurrentTable(String table) {
        Log.e("sns", "current table: " + table);
        TABLE_NAME = table;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.e("sns", " onCreate: " + CREATION_TABLE);
        for (int i = 0; i < mTableList.size(); i++) {
            String tmp = CREATION_TABLE;
            tmp = tmp.replaceAll(TABLE_REGEX, mTableList.get(i));
            Log.e("sns", "Query: " + tmp);
            db.execSQL(tmp);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        this.onCreate(db);
    }

    public void addChat(String msg, boolean isSelf, boolean isDate) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TIME, System.currentTimeMillis());
        values.put(KEY_MESSAGE, msg);
        values.put(KEY_IS_SELF, isSelf ? 1 : 0);
        values.put(KEY_IS_DATE, isDate ? 1 : 0);

        sqLiteDatabase.insert(TABLE_NAME, null, values);
        sqLiteDatabase.close();
    }

    public List<MessageModel> getChatFromDates(long startDate, int tillDays) {
        Log.e("sns", "getChatFromDates: " + TABLE_NAME);
        Date date = new Date(startDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, tillDays);
        long endDate = calendar.getTimeInMillis();

        Log.e("sns", "start date: " + endDate + " endDate: " + startDate);

        List<MessageModel> list = new ArrayList<>();

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        Cursor c = sqLiteDatabase.rawQuery("select " + KEY_TIME + ", " + KEY_MESSAGE + " , " + KEY_IS_SELF + " , " + KEY_IS_DATE + " from " + TABLE_NAME + " where " + KEY_TIME + " BETWEEN '" + endDate + "' AND '" + startDate + "' ORDER BY " + KEY_TIME + " ASC", null);

        MessageModel model = null;

        if (c.moveToFirst()) {
            do {
                model = new MessageModel();
                model.setTimeUTC(Long.parseLong(c.getString(0)));
                model.setMessage(String.valueOf(c.getString(1)));
                int id = Integer.parseInt(c.getString(2));
                model.setSelf(id == 1 ? true : false);
                id = Integer.parseInt(c.getString(3));
                model.setDate(id == 1 ? true : false);
                list.add(model);
            } while (c.moveToNext());
        }

        return list;
    }
}
