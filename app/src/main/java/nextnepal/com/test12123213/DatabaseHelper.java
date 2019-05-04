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
    private static String tableNAME = "NepPatro";
    private static int VERSION = 1;

    String createTableUser = "CREATE TABLE IF NOT EXISTS `NepPatro` (\n" +
            "\t`year`\tINTEGER,\n" +
            "\t`month`\tINTEGER,\n" +
            "\t`day`\tINTEGER,\n" +
            "\t`dayOfWeek`\tINTEGER,\n" +
            "\t`startingDay`\tINTEGER,\n" +
            "\t`id`\tTEXT,\n" +
            "\tPRIMARY KEY(`id`)\n" +
            ")";


    private Context context;

    DatabaseHelper(Context context) {
        super(context, dbNAME, null, VERSION);
        this.context = context;
    }

    ArrayList<PatroModel> loadMonth(int year, int month) {
        ArrayList<PatroModel> patroList = new ArrayList<>();
        String sql = "Select * from " + tableNAME + " Where year=" + year + " and month=" + month;

        getReadableDatabase().rawQuery(sql, null);
        Cursor c = getReadableDatabase().rawQuery(sql, null);
        while (c.moveToNext()) {
            PatroModel patroModel = new PatroModel();
            patroModel.setYear(c.getInt(c.getColumnIndex("year")));
            patroModel.setMonth(c.getInt(c.getColumnIndex("month")));
            patroModel.setDay(c.getInt(c.getColumnIndex("day")));
            patroModel.setId(c.getString(c.getColumnIndex("id")));
            patroModel.setDayOfWeek(c.getInt(c.getColumnIndex("dayOfWeek")));
            patroModel.setStartingDay(c.getInt(c.getColumnIndex("startingDay")));
            patroList.add(patroModel);

        }
        return patroList;
    }

    ArrayList<PatroModel> loadDate() {
        ArrayList<PatroModel> patroList = new ArrayList<>();
        String sql = "Select * from " + tableNAME;
        getReadableDatabase().rawQuery(sql, null);
        Cursor c = getReadableDatabase().rawQuery(sql, null);
        while (c.moveToNext()) {
            PatroModel patroModel = new PatroModel();
            patroModel.setYear(c.getInt(c.getColumnIndex("year")));
            patroModel.setMonth(c.getInt(c.getColumnIndex("month")));
            patroModel.setDay(c.getInt(c.getColumnIndex("day")));
            patroModel.setId(c.getString(c.getColumnIndex("id")));
            patroModel.setDayOfWeek(c.getInt(c.getColumnIndex("dayOfWeek")));
            patroModel.setStartingDay(c.getInt(c.getColumnIndex("startingDay")));
            patroList.add(patroModel);

        }
        return patroList;

    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createTableUser);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + tableNAME);
        onCreate(db);
    }

    void insertDate(ContentValues cv) {
        getWritableDatabase().insert(tableNAME, "", cv);
    }
}
