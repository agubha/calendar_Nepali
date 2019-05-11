package nextnepal.com.test12123213;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;


public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper.Sqlite";
    private static String dbNAME = "HamroPatro";
    private static String patroDateTable = "NepPatro";
    private static String patroEventTable = "NepPatroEvent";
    private static int VERSION = 1;

    String createTablePatro = "CREATE TABLE IF NOT EXISTS `NepPatro` (\n" +
            "\t`nyear`\tINTEGER,\n" +
            "\t`nmonth`\tINTEGER,\n" +
            "\t`nday`\tINTEGER,\n" +
            "\t`eyear`\tINTEGER,\n" +
            "\t`emonth`\tINTEGER,\n" +
            "\t`eday`\tINTEGER,\n" +
            "\t`dayOfWeek`\tINTEGER,\n" +
            "\t`id`\tTEXT,\n" +
            "\tPRIMARY KEY(`id`)\n" +
            ")";

    String createTableEvent = "CREATE TABLE IF NOT EXISTS `NepPatroEvent` (\n" +
            "\t`event_detail_np`\tTEXT,\n" +
            "\t`event_detail_en`\tTEXT,\n" +
            "\t`tithe`\tTEXT,\n" +
            "\t`holiday`\tINTEGER,\n" +
            "\t`event_id`\tTEXT,\n" +
            "\tPRIMARY KEY(`event_id`)\n" +
            ")";

    private Context context;

    DatabaseHelper(Context context) {
        super(context, dbNAME, null, VERSION);
        this.context = context;
    }

    ArrayList<PatroModel> loadMonth(int year, int month) {
        ArrayList<PatroModel> patroList = new ArrayList<>();
        String sql = "Select * from " + patroDateTable + " Where nyear = " + year + " and nmonth = " + month;

        getReadableDatabase().rawQuery(sql, null);
        Cursor c = getReadableDatabase().rawQuery(sql, null);
        while (c.moveToNext()) {
            int nyear = c.getInt(c.getColumnIndex("nyear"));
            int nmonth = c.getInt(c.getColumnIndex("nmonth"));
            int nday = c.getInt(c.getColumnIndex("nday"));
            int eyear = c.getInt(c.getColumnIndex("eyear"));
            int emonth = c.getInt(c.getColumnIndex("emonth"));
            int eday = c.getInt(c.getColumnIndex("eday"));
            String id = c.getString(c.getColumnIndex("id"));
            int dayOfWeek = c.getInt(c.getColumnIndex("dayOfWeek"));
            PatroModel patroModel = new PatroModel(nyear, nmonth, nday, eyear, emonth, eday, id, dayOfWeek);
            patroList.add(patroModel);

        }
        return patroList;
    }

    EventDb loadPatroEvent(String id) {
        EventDb eventDb = null;
        String sql = "SELECT * FROM " + patroEventTable + " WHERE event_id =" + " '" + id + "' ";
        Log.d("SQL QUERY =", "" + sql);
        Cursor c = getReadableDatabase().rawQuery(sql, null);
        while (c.moveToNext()) {
            String event_id = c.getString(c.getColumnIndex("event_id"));
            String event_detail_np = c.getString(c.getColumnIndex("event_detail_np"));
            String event_detail_en = c.getString(c.getColumnIndex("event_detail_en"));
            String tithe = c.getString(c.getColumnIndex("tithe"));
            int holiday = c.getInt(c.getColumnIndex("holiday"));
            eventDb = new EventDb(event_id, event_detail_np, event_detail_en, tithe, holiday);
        }
        c.close();
        return eventDb;
    }

    ArrayList<PatroModel> loadDate() {
        ArrayList<PatroModel> patroList = new ArrayList<>();
        String sql = "Select * from " + patroDateTable;
        getReadableDatabase().rawQuery(sql, null);
        Cursor c = getReadableDatabase().rawQuery(sql, null);
        while (c.moveToNext()) {
            int nyear = c.getInt(c.getColumnIndex("nyear"));
            int nmonth = c.getInt(c.getColumnIndex("nmonth"));
            int nday = c.getInt(c.getColumnIndex("nday"));
            int eyear = c.getInt(c.getColumnIndex("eyear"));
            int emonth = c.getInt(c.getColumnIndex("emonth"));
            int eday = c.getInt(c.getColumnIndex("eday"));
            String id = c.getString(c.getColumnIndex("id"));
            int dayOfWeek = c.getInt(c.getColumnIndex("dayOfWeek"));
            PatroModel patroModel = new PatroModel(nyear, nmonth, nday, eyear, emonth, eday, id, dayOfWeek);
            patroList.add(patroModel);

        }
        return patroList;

    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createTablePatro);
        db.execSQL(createTableEvent);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + patroDateTable);
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + patroEventTable);
        onCreate(db);
    }

    void insertDate(ContentValues cv) {
        getWritableDatabase().insert(patroDateTable, "", cv);
    }


    Long insertIvents(ContentValues contentValues) {
        return getWritableDatabase().insert(patroEventTable, "", contentValues);
    }


}
