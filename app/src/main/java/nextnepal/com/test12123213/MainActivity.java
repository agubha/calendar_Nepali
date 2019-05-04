package nextnepal.com.test12123213;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    EditText month, day, year;
    Button btn_submit;
    private SparseArray<int[]> daysInMonthMap;
    private SparseArray<int[]> startWeekDayMonthMap;
    private DatabaseHelper databaseHelper;
    private TableLayout tableLayout;

    private static boolean isNepDateInConversionRange(int yy, int mm, int dd) {
        return (yy >= 1970 && yy <= 2090) && (mm >= 1 && mm <= 12) && (dd >= 1 && dd <= 32);
    }

    private static boolean isEngLeapYear(int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        return cal.getActualMaximum(Calendar.DAY_OF_YEAR) > 365;
    }

    private static boolean isEngDateInConversionRange(int yy, int mm, int dd) {
        return (yy >= 1913 && yy <= 2033) && (mm >= 1 && mm <= 12) && (dd >= 1 && dd <= 31);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initDatabase();
        initializeData();
        getViews();
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                performLoop();
                createGrid();
            }
        });
    }

    private void performLoop() {
        new LongOperation().execute("");
    }

    private void createGrid() {

        ArrayList<PatroModel> patroModelArrayList = databaseHelper.loadMonth(2012, 1);
        Log.d("SIZE FILTER", "" + patroModelArrayList.size());
        tableLayout = findViewById(R.id.table_layout);
        tableLayout.setStretchAllColumns(true);
        TextView tv;
        int counter = patroModelArrayList.size();
        int k = 0;
        for (int i = 0; i <= 7; i++) {
            TableRow row = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);
            for (int j = 0; j <= 6; j++) {
                if (k < counter) {
                    tv = new TextView(this);
                    tv.setPadding(5, 5, 5, 5);
                    tv.setText(patroModelArrayList.get(k).getDay() + "");
                    tv.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    row.addView(tv);
                    k++;
                }
            }
            tableLayout.addView(row, i);
        }
    }

    private void initDatabase() {
        databaseHelper = new DatabaseHelper(this);
    }

    private void getViews() {
        month = findViewById(R.id.month);
        year = findViewById(R.id.year);
        day = findViewById(R.id.day);
        btn_submit = findViewById(R.id.convert);
    }

    public Model getNepaliDate(@IntRange(from = 1913 - 2033) int engYY,
                               @IntRange(from = 1, to = 12) int engMM,
                               @IntRange(from = 1, to = 31) int engDD) {

        if (isEngDateInConversionRange(engYY, engMM, engDD)) {

            int startingEngYear = 1913;
            int startingEngMonth = 4;
            int startingEngDay = 13;

            int startingDayOfWeek = Calendar.SUNDAY; // 1913/4/13 is a Sunday

            int startingNepYear = 1970;
            int startingNepMonth = 1;
            int startingNepDay = 1;

            int nepYY, nepMM, nepDD;
            int dayOfWeek = startingDayOfWeek;

            Model tempModel = new Model();

            /*
            Calendar currentEngDate = new GregorianCalendar();
            currentEngDate.set(engYY, engMM, engDD);
            Calendar baseEngDate = new GregorianCalendar();
            baseEngDate.set(startingEngYear, startingEngMonth, startingEngDay);
            long totalEngDaysCount = daysBetween(baseEngDate, currentEngDate);
            */
            /*calculate the days between two english date*/


            DateTime base = new DateTime(startingEngYear, startingEngMonth, startingEngDay, 0, 0); // June 20th, 2010
            DateTime newDate = new DateTime(engYY, engMM, engDD, 0, 0); // July 24th
            long totalEngDaysCount = Days.daysBetween(base, newDate).getDays();

            nepYY = startingNepYear;
            nepMM = startingNepMonth;
            nepDD = startingNepDay;

            while (totalEngDaysCount != 0) {
                int daysInMonth = daysInMonthMap.get(nepYY)[nepMM];
                nepDD++;
                if (nepDD > daysInMonth) {
                    nepMM++;
                    nepDD = 1;
                }
                if (nepMM > 12) {
                    nepYY++;
                    nepMM = 1;
                }
                dayOfWeek++;
                if (dayOfWeek > 7) {
                    dayOfWeek = 1;
                }
                totalEngDaysCount--;
            }
            tempModel.setYear(nepYY);
            tempModel.setMonth(nepMM - 1);
            tempModel.setDay(nepDD);
            tempModel.setDayOfWeek(dayOfWeek);
            Log.d("Try Inserting:", "" + tempModel.getYear() + "" + tempModel.getMonth() + "" + tempModel.getDay());

            return tempModel;
        } else throw new IllegalArgumentException("Out of Range: Date is out of range to Convert");
    }

    private void initializeData() {
        getStartWeekDayMonthMap();
        getDaysInMonthMap();
    }

    private void getDaysInMonthMap() {
//        if (daysInMonthMap != null) return daysInMonthMap;
        daysInMonthMap = new SparseArray<>();
        /*
         The 0s at index 0 are dummy values so as to make the int array of
         days in months seems more intuitive that index 1 refers to first
         month "Baisakh", index 2 refers to second month "Jesth" and so on.
         */

        // based on https://github.com/bahadurbaniya/Date-Converter-Bikram-Sambat-to-English-Date/blob/master/src/main/java/np/com/converter/date/nepali/Lookup.java

        daysInMonthMap.put(1970, new int[]{0, 31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30});
        daysInMonthMap.put(1971, new int[]{0, 31, 31, 32, 31, 32, 30, 30, 29, 30, 29, 30, 30});
        daysInMonthMap.put(1972, new int[]{0, 31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31});
        daysInMonthMap.put(1973, new int[]{0, 30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31});
        daysInMonthMap.put(1974, new int[]{0, 30, 32, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30});
        daysInMonthMap.put(1975, new int[]{0, 31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30});
        daysInMonthMap.put(1976, new int[]{0, 31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31});
        daysInMonthMap.put(1977, new int[]{0, 30, 32, 31, 32, 31, 31, 29, 30, 29, 30, 29, 31});
        daysInMonthMap.put(1978, new int[]{0, 31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30});
        daysInMonthMap.put(1979, new int[]{0, 31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30});

        daysInMonthMap.put(1980, new int[]{0, 31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31});
        daysInMonthMap.put(1981, new int[]{0, 31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 30, 30});
        daysInMonthMap.put(1982, new int[]{0, 31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30});
        daysInMonthMap.put(1983, new int[]{0, 31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30});
        daysInMonthMap.put(1984, new int[]{0, 31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31});
        daysInMonthMap.put(1985, new int[]{0, 31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 30, 30});
        daysInMonthMap.put(1986, new int[]{0, 31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30});
        daysInMonthMap.put(1987, new int[]{0, 31, 32, 31, 32, 31, 30, 30, 29, 30, 29, 30, 30});
        daysInMonthMap.put(1988, new int[]{0, 31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31});
        daysInMonthMap.put(1989, new int[]{0, 31, 31, 31, 32, 31, 31, 30, 29, 30, 29, 30, 30});

        daysInMonthMap.put(1990, new int[]{0, 31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30});
        daysInMonthMap.put(1991, new int[]{0, 31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 30});
        daysInMonthMap.put(1992, new int[]{0, 31, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31});
        daysInMonthMap.put(1993, new int[]{0, 31, 31, 31, 32, 31, 31, 30, 29, 30, 29, 30, 30});
        daysInMonthMap.put(1994, new int[]{0, 31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30});
        daysInMonthMap.put(1995, new int[]{0, 31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 30});
        daysInMonthMap.put(1996, new int[]{0, 31, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31});
        daysInMonthMap.put(1997, new int[]{0, 31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30});
        daysInMonthMap.put(1998, new int[]{0, 31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30});
        daysInMonthMap.put(1999, new int[]{0, 31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31});


        //old
        daysInMonthMap.put(2000, new int[]{0, 30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31});
        daysInMonthMap.put(2001, new int[]{0, 31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30});
        daysInMonthMap.put(2002, new int[]{0, 31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30});
        daysInMonthMap.put(2003, new int[]{0, 31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31});
        daysInMonthMap.put(2004, new int[]{0, 30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31});
        daysInMonthMap.put(2005, new int[]{0, 31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30});
        daysInMonthMap.put(2006, new int[]{0, 31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30});
        daysInMonthMap.put(2007, new int[]{0, 31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31});
        daysInMonthMap.put(2008, new int[]{0, 31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 29, 31});
        daysInMonthMap.put(2009, new int[]{0, 31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30});
        daysInMonthMap.put(2010, new int[]{0, 31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30});
        daysInMonthMap.put(2011, new int[]{0, 31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31});
        daysInMonthMap.put(2012, new int[]{0, 31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 30, 30});
        daysInMonthMap.put(2013, new int[]{0, 31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30});
        daysInMonthMap.put(2014, new int[]{0, 31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30});
        daysInMonthMap.put(2015, new int[]{0, 31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31});
        daysInMonthMap.put(2016, new int[]{0, 31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 30, 30});
        daysInMonthMap.put(2017, new int[]{0, 31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30});
        daysInMonthMap.put(2018, new int[]{0, 31, 32, 31, 32, 31, 30, 30, 29, 30, 29, 30, 30});
        daysInMonthMap.put(2019, new int[]{0, 31, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31});
        daysInMonthMap.put(2020, new int[]{0, 31, 31, 31, 32, 31, 31, 30, 29, 30, 29, 30, 30});
        daysInMonthMap.put(2021, new int[]{0, 31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30});
        daysInMonthMap.put(2022, new int[]{0, 31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 30});
        daysInMonthMap.put(2023, new int[]{0, 31, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31});
        daysInMonthMap.put(2024, new int[]{0, 31, 31, 31, 32, 31, 31, 30, 29, 30, 29, 30, 30});
        daysInMonthMap.put(2025, new int[]{0, 31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30});
        daysInMonthMap.put(2026, new int[]{0, 31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31});
        daysInMonthMap.put(2027, new int[]{0, 30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31});
        daysInMonthMap.put(2028, new int[]{0, 31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30});
        daysInMonthMap.put(2029, new int[]{0, 31, 31, 32, 31, 32, 30, 30, 29, 30, 29, 30, 30});
        daysInMonthMap.put(2030, new int[]{0, 31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31});
        daysInMonthMap.put(2031, new int[]{0, 30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31});
        daysInMonthMap.put(2032, new int[]{0, 31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30});
        daysInMonthMap.put(2033, new int[]{0, 31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30});
        daysInMonthMap.put(2034, new int[]{0, 31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31});
        daysInMonthMap.put(2035, new int[]{0, 30, 32, 31, 32, 31, 31, 29, 30, 30, 29, 29, 31});
        daysInMonthMap.put(2036, new int[]{0, 31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30});
        daysInMonthMap.put(2037, new int[]{0, 31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30});
        daysInMonthMap.put(2038, new int[]{0, 31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31});
        daysInMonthMap.put(2039, new int[]{0, 31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 30, 30});
        daysInMonthMap.put(2040, new int[]{0, 31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30});
        daysInMonthMap.put(2041, new int[]{0, 31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30});
        daysInMonthMap.put(2042, new int[]{0, 31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31});
        daysInMonthMap.put(2043, new int[]{0, 31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 30, 30});
        daysInMonthMap.put(2044, new int[]{0, 31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30});
        daysInMonthMap.put(2045, new int[]{0, 31, 32, 31, 32, 31, 30, 30, 29, 30, 29, 30, 30});
        daysInMonthMap.put(2046, new int[]{0, 31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31});
        daysInMonthMap.put(2047, new int[]{0, 31, 31, 31, 32, 31, 31, 30, 29, 30, 29, 30, 30});
        daysInMonthMap.put(2048, new int[]{0, 31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30});
        daysInMonthMap.put(2049, new int[]{0, 31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 30});
        daysInMonthMap.put(2050, new int[]{0, 31, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31});
        daysInMonthMap.put(2051, new int[]{0, 31, 31, 31, 32, 31, 31, 30, 29, 30, 29, 30, 30});
        daysInMonthMap.put(2052, new int[]{0, 31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30});
        daysInMonthMap.put(2053, new int[]{0, 31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 30});
        daysInMonthMap.put(2054, new int[]{0, 31, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31});
        daysInMonthMap.put(2055, new int[]{0, 31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30});
        daysInMonthMap.put(2056, new int[]{0, 31, 31, 32, 31, 32, 30, 30, 29, 30, 29, 30, 30});
        daysInMonthMap.put(2057, new int[]{0, 31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31});
        daysInMonthMap.put(2058, new int[]{0, 30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31});
        daysInMonthMap.put(2059, new int[]{0, 31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30});
        daysInMonthMap.put(2060, new int[]{0, 31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30});
        daysInMonthMap.put(2061, new int[]{0, 31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31});
        daysInMonthMap.put(2062, new int[]{0, 30, 32, 31, 32, 31, 31, 29, 30, 29, 30, 29, 31});
        daysInMonthMap.put(2063, new int[]{0, 31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30});
        daysInMonthMap.put(2064, new int[]{0, 31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30});
        daysInMonthMap.put(2065, new int[]{0, 31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31});
        daysInMonthMap.put(2066, new int[]{0, 31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 29, 31});
        daysInMonthMap.put(2067, new int[]{0, 31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30});
        daysInMonthMap.put(2068, new int[]{0, 31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30});
        daysInMonthMap.put(2069, new int[]{0, 31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31});
        daysInMonthMap.put(2070, new int[]{0, 31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 30, 30});
        daysInMonthMap.put(2071, new int[]{0, 31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30});
        daysInMonthMap.put(2072, new int[]{0, 31, 32, 31, 32, 31, 30, 30, 29, 30, 29, 30, 30});
        daysInMonthMap.put(2073, new int[]{0, 31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31});
        daysInMonthMap.put(2074, new int[]{0, 31, 31, 31, 32, 31, 31, 30, 29, 30, 29, 30, 30});
        daysInMonthMap.put(2075, new int[]{0, 31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30});
        daysInMonthMap.put(2076, new int[]{0, 31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 30});
        daysInMonthMap.put(2077, new int[]{0, 31, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31});
        daysInMonthMap.put(2078, new int[]{0, 31, 31, 31, 32, 31, 31, 30, 29, 30, 29, 30, 30});
        daysInMonthMap.put(2079, new int[]{0, 31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30});
        daysInMonthMap.put(2080, new int[]{0, 31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 30});
        daysInMonthMap.put(2081, new int[]{0, 31, 31, 32, 32, 31, 30, 30, 30, 29, 30, 30, 30});
        daysInMonthMap.put(2082, new int[]{0, 30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 30, 30});
        daysInMonthMap.put(2083, new int[]{0, 31, 31, 32, 31, 31, 30, 30, 30, 29, 30, 30, 30});
        daysInMonthMap.put(2084, new int[]{0, 31, 31, 32, 31, 31, 30, 30, 30, 29, 30, 30, 30});
        daysInMonthMap.put(2085, new int[]{0, 31, 32, 31, 32, 30, 31, 30, 30, 29, 30, 30, 30});
        daysInMonthMap.put(2086, new int[]{0, 30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 30, 30});
        daysInMonthMap.put(2087, new int[]{0, 31, 31, 32, 31, 31, 31, 30, 30, 29, 30, 30, 30});
        daysInMonthMap.put(2088, new int[]{0, 30, 31, 32, 32, 30, 31, 30, 30, 29, 30, 30, 30});
        daysInMonthMap.put(2089, new int[]{0, 30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 30, 30});
        daysInMonthMap.put(2090, new int[]{0, 30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 30, 30});
        /* *//*new*//*
        // based on https://github.com/bahadurbaniya/Date-Converter-Bikram-Sambat-to-English-Date/blob/master/src/main/java/np/com/converter/date/nepali/Lookup.java
        daysInMonthMap.put(2091, new int[]{31, 31, 32, 31, 31, 31, 30, 30, 29, 30, 30, 30});// 2091
        daysInMonthMap.put(2092, new int[]{31, 31, 32, 32, 31, 30, 30, 30, 29, 30, 30, 30});// 2092
        daysInMonthMap.put(2093, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 30, 30, 30});// 2093
        daysInMonthMap.put(2094, new int[]{31, 31, 32, 31, 31, 30, 30, 30, 29, 30, 30, 30});// 2094
        daysInMonthMap.put(2095, new int[]{31, 31, 32, 31, 31, 31, 30, 29, 30, 30, 30, 30});// 2095
        daysInMonthMap.put(2096, new int[]{30, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30});// 2096
        daysInMonthMap.put(2097, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 30, 30, 30});// 2097
        daysInMonthMap.put(2098, new int[]{31, 31, 32, 31, 31, 31, 29, 30, 29, 30, 30, 31});// 2098
        daysInMonthMap.put(2099, new int[]{31, 31, 32, 31, 31, 31, 30, 29, 29, 30, 30, 30});// 2099
        daysInMonthMap.put(2100, new int[]{31, 32, 31, 32, 30, 31, 30, 29, 30, 29, 30, 30});// 2100
        */
//        return daysInMonthMap;
    }

    private void getStartWeekDayMonthMap() {
//        if (startWeekDayMonthMap != null) return startWeekDayMonthMap;
        startWeekDayMonthMap = new SparseArray<>();

        /*
         The 0s at index 0 are dummy values so as to make the int array of
         days in months seems more intuitive that index 1 refers to first
         month "Baisakh", index 2 refers to second month "Jesth" and so on.
         */

        // based on www.ashesh.com.np/neplai-date-converter
        startWeekDayMonthMap.put(1970, new int[]{0, 1, 4, 7, 4, 7, 3, 6, 1, 2, 4, 5, 7});
        startWeekDayMonthMap.put(1971, new int[]{0, 2, 5, 1, 5, 1, 5, 7, 2, 3, 5, 6, 1});
        startWeekDayMonthMap.put(1972, new int[]{0, 3, 6, 3, 6, 3, 6, 1, 3, 5, 6, 7, 2});
        startWeekDayMonthMap.put(1973, new int[]{0, 5, 7, 4, 7, 4, 7, 2, 4, 6, 7, 2, 3});
        startWeekDayMonthMap.put(1974, new int[]{0, 6, 1, 5, 2, 5, 1, 4, 6, 7, 2, 3, 5});
        startWeekDayMonthMap.put(1975, new int[]{0, 7, 3, 6, 3, 7, 3, 5, 7, 1, 3, 4, 6});
        startWeekDayMonthMap.put(1976, new int[]{0, 1, 4, 1, 4, 1, 4, 6, 1, 3, 4, 5, 7});
        startWeekDayMonthMap.put(1977, new int[]{0, 3, 5, 2, 5, 2, 5, 1, 2, 4, 5, 7, 1});
        startWeekDayMonthMap.put(1978, new int[]{0, 4, 7, 3, 7, 3, 6, 2, 4, 5, 7, 1, 3});
        startWeekDayMonthMap.put(1979, new int[]{0, 5, 1, 4, 1, 5, 1, 3, 5, 6, 1, 2, 4});
        startWeekDayMonthMap.put(1980, new int[]{0, 6, 2, 6, 2, 6, 2, 4, 6, 1, 2, 3, 5});
        startWeekDayMonthMap.put(1981, new int[]{0, 1, 4, 7, 3, 7, 3, 6, 7, 2, 4, 5, 7});
        startWeekDayMonthMap.put(1982, new int[]{0, 2, 5, 1, 5, 1, 4, 7, 2, 3, 5, 6, 1});
        startWeekDayMonthMap.put(1983, new int[]{0, 3, 6, 2, 6, 3, 6, 1, 3, 4, 6, 7, 2});
        startWeekDayMonthMap.put(1984, new int[]{0, 4, 7, 4, 7, 4, 7, 2, 4, 6, 7, 1, 3});
        startWeekDayMonthMap.put(1985, new int[]{0, 6, 2, 5, 1, 5, 1, 4, 5, 7, 2, 3, 5});
        startWeekDayMonthMap.put(1986, new int[]{0, 7, 3, 6, 3, 6, 2, 5, 7, 1, 3, 4, 6});
        startWeekDayMonthMap.put(1987, new int[]{0, 1, 4, 1, 4, 1, 4, 6, 1, 2, 4, 5, 7});
        startWeekDayMonthMap.put(1988, new int[]{0, 2, 5, 2, 5, 2, 5, 7, 2, 4, 5, 6, 1});
        startWeekDayMonthMap.put(1989, new int[]{0, 4, 7, 3, 6, 3, 6, 2, 4, 5, 7, 1, 3});
        startWeekDayMonthMap.put(1990, new int[]{0, 5, 1, 4, 1, 4, 7, 3, 5, 6, 1, 2, 4});
        startWeekDayMonthMap.put(1991, new int[]{0, 6, 2, 6, 2, 6, 2, 4, 6, 1, 2, 3, 5});
        startWeekDayMonthMap.put(1992, new int[]{0, 7, 3, 7, 3, 7, 3, 5, 7, 2, 3, 5, 6});
        startWeekDayMonthMap.put(1993, new int[]{0, 2, 5, 1, 4, 1, 4, 7, 2, 3, 5, 6, 1});
        startWeekDayMonthMap.put(1994, new int[]{0, 3, 6, 2, 6, 2, 5, 1, 3, 4, 6, 7, 2});
        startWeekDayMonthMap.put(1995, new int[]{0, 4, 7, 4, 7, 4, 7, 2, 4, 6, 7, 1, 3});
        startWeekDayMonthMap.put(1996, new int[]{0, 5, 1, 5, 1, 5, 1, 3, 5, 7, 1, 3, 4});
        startWeekDayMonthMap.put(1997, new int[]{0, 7, 3, 6, 3, 6, 2, 5, 7, 1, 3, 4, 6});
        startWeekDayMonthMap.put(1998, new int[]{0, 1, 4, 7, 4, 7, 3, 6, 1, 2, 4, 5, 7});
        startWeekDayMonthMap.put(1999, new int[]{0, 2, 5, 2, 5, 2, 5, 7, 2, 4, 5, 6, 1});

        /*old*/
        startWeekDayMonthMap.put(2000, new int[]{0, 4, 6, 3, 6, 3, 6, 1, 3, 5, 6, 1, 2});
        startWeekDayMonthMap.put(2001, new int[]{0, 5, 1, 4, 1, 4, 7, 3, 5, 6, 1, 2, 4});
        startWeekDayMonthMap.put(2002, new int[]{0, 6, 2, 5, 2, 6, 2, 4, 6, 7, 2, 3, 5});
        startWeekDayMonthMap.put(2003, new int[]{0, 7, 3, 7, 3, 7, 3, 5, 7, 2, 3, 4, 6});
        startWeekDayMonthMap.put(2004, new int[]{0, 2, 4, 1, 4, 1, 4, 6, 1, 3, 4, 6, 7});
        startWeekDayMonthMap.put(2005, new int[]{0, 3, 6, 2, 6, 2, 5, 1, 3, 4, 6, 7, 2});
        startWeekDayMonthMap.put(2006, new int[]{0, 4, 7, 3, 7, 4, 7, 2, 4, 5, 7, 1, 3});
        startWeekDayMonthMap.put(2007, new int[]{0, 5, 1, 5, 1, 5, 1, 3, 5, 7, 1, 2, 4});
        startWeekDayMonthMap.put(2008, new int[]{0, 7, 3, 6, 2, 6, 2, 5, 6, 1, 3, 4, 5});
        startWeekDayMonthMap.put(2009, new int[]{0, 1, 4, 7, 4, 7, 3, 6, 1, 2, 4, 5, 7});
        startWeekDayMonthMap.put(2010, new int[]{0, 2, 5, 1, 5, 2, 5, 7, 2, 3, 5, 6, 1});
        startWeekDayMonthMap.put(2011, new int[]{0, 3, 6, 3, 6, 3, 6, 1, 3, 5, 6, 7, 2});
        startWeekDayMonthMap.put(2012, new int[]{0, 5, 1, 4, 7, 4, 7, 3, 4, 6, 1, 2, 4});
        startWeekDayMonthMap.put(2013, new int[]{0, 6, 2, 5, 2, 5, 1, 4, 6, 7, 2, 3, 5});
        startWeekDayMonthMap.put(2014, new int[]{0, 7, 3, 6, 3, 7, 3, 5, 7, 1, 3, 4, 6});
        startWeekDayMonthMap.put(2015, new int[]{0, 1, 4, 1, 4, 1, 4, 6, 1, 3, 4, 5, 7});
        startWeekDayMonthMap.put(2016, new int[]{0, 3, 6, 2, 5, 2, 5, 1, 2, 4, 6, 7, 2});
        startWeekDayMonthMap.put(2017, new int[]{0, 4, 7, 3, 7, 3, 6, 2, 4, 5, 7, 1, 3});
        startWeekDayMonthMap.put(2018, new int[]{0, 5, 1, 5, 1, 5, 1, 3, 5, 6, 1, 2, 4});
        startWeekDayMonthMap.put(2019, new int[]{0, 6, 2, 6, 2, 6, 2, 4, 6, 1, 2, 4, 5});
        startWeekDayMonthMap.put(2020, new int[]{0, 1, 4, 7, 3, 7, 3, 6, 1, 2, 4, 5, 7});
        startWeekDayMonthMap.put(2021, new int[]{0, 2, 5, 1, 5, 1, 4, 7, 2, 3, 5, 6, 1});
        startWeekDayMonthMap.put(2022, new int[]{0, 3, 6, 3, 6, 3, 6, 1, 3, 5, 6, 7, 2});
        startWeekDayMonthMap.put(2023, new int[]{0, 4, 7, 4, 7, 4, 7, 2, 4, 6, 7, 2, 3});
        startWeekDayMonthMap.put(2024, new int[]{0, 6, 2, 5, 1, 5, 1, 4, 6, 7, 2, 3, 5});
        startWeekDayMonthMap.put(2025, new int[]{0, 7, 3, 6, 3, 6, 2, 5, 7, 1, 3, 4, 6});
        startWeekDayMonthMap.put(2026, new int[]{0, 1, 4, 1, 4, 1, 4, 6, 1, 3, 4, 5, 7});
        startWeekDayMonthMap.put(2027, new int[]{0, 3, 5, 2, 5, 2, 5, 7, 2, 4, 5, 7, 1});
        startWeekDayMonthMap.put(2028, new int[]{0, 4, 7, 3, 7, 3, 6, 2, 4, 5, 7, 1, 3});
        startWeekDayMonthMap.put(2029, new int[]{0, 5, 1, 4, 1, 4, 1, 3, 5, 6, 1, 2, 4});
        startWeekDayMonthMap.put(2030, new int[]{0, 6, 2, 6, 2, 6, 2, 4, 6, 1, 2, 3, 5});
        startWeekDayMonthMap.put(2031, new int[]{0, 1, 3, 7, 3, 7, 3, 5, 7, 2, 3, 5, 6});
        startWeekDayMonthMap.put(2032, new int[]{0, 2, 5, 1, 5, 1, 4, 7, 2, 3, 5, 6, 1});
        startWeekDayMonthMap.put(2033, new int[]{0, 3, 6, 2, 6, 3, 6, 1, 3, 4, 6, 7, 2});
        startWeekDayMonthMap.put(2034, new int[]{0, 4, 7, 4, 7, 4, 7, 2, 4, 6, 7, 1, 3});
        startWeekDayMonthMap.put(2035, new int[]{0, 6, 1, 5, 1, 5, 1, 4, 5, 7, 2, 3, 4});
        startWeekDayMonthMap.put(2036, new int[]{0, 7, 3, 6, 3, 6, 2, 5, 7, 1, 3, 4, 6});
        startWeekDayMonthMap.put(2037, new int[]{0, 1, 4, 7, 4, 1, 4, 6, 1, 2, 4, 5, 7});
        startWeekDayMonthMap.put(2038, new int[]{0, 2, 5, 2, 5, 2, 5, 7, 2, 4, 5, 6, 1});
        startWeekDayMonthMap.put(2039, new int[]{0, 4, 7, 3, 6, 3, 6, 2, 3, 5, 7, 1, 3});
        startWeekDayMonthMap.put(2040, new int[]{0, 5, 1, 4, 1, 4, 7, 3, 5, 6, 1, 2, 4});
        startWeekDayMonthMap.put(2041, new int[]{0, 6, 2, 5, 2, 6, 2, 4, 6, 7, 2, 3, 5});
        startWeekDayMonthMap.put(2042, new int[]{0, 7, 3, 7, 3, 7, 3, 5, 7, 2, 3, 4, 6});
        startWeekDayMonthMap.put(2043, new int[]{0, 2, 5, 1, 4, 1, 4, 7, 1, 3, 5, 6, 1});
        startWeekDayMonthMap.put(2044, new int[]{0, 3, 6, 2, 6, 2, 5, 1, 3, 4, 6, 7, 2});
        startWeekDayMonthMap.put(2045, new int[]{0, 4, 7, 4, 7, 4, 7, 2, 4, 5, 7, 1, 3});
        startWeekDayMonthMap.put(2046, new int[]{0, 5, 1, 5, 1, 5, 1, 3, 5, 7, 1, 2, 4});
        startWeekDayMonthMap.put(2047, new int[]{0, 7, 3, 6, 2, 6, 2, 5, 7, 1, 3, 4, 6});
        startWeekDayMonthMap.put(2048, new int[]{0, 1, 4, 7, 4, 7, 3, 6, 1, 2, 4, 5, 7});
        startWeekDayMonthMap.put(2049, new int[]{0, 2, 5, 2, 5, 2, 5, 7, 2, 4, 5, 6, 1});
        startWeekDayMonthMap.put(2050, new int[]{0, 3, 6, 3, 6, 3, 6, 1, 3, 5, 6, 1, 2});
        startWeekDayMonthMap.put(2051, new int[]{0, 5, 1, 4, 7, 4, 7, 3, 5, 6, 1, 2, 4});
        startWeekDayMonthMap.put(2052, new int[]{0, 6, 2, 5, 2, 5, 1, 4, 6, 7, 2, 3, 5});
        startWeekDayMonthMap.put(2053, new int[]{0, 7, 3, 7, 3, 7, 3, 5, 7, 2, 3, 4, 6});
        startWeekDayMonthMap.put(2054, new int[]{0, 1, 4, 1, 4, 1, 4, 6, 1, 3, 4, 6, 7});
        startWeekDayMonthMap.put(2055, new int[]{0, 3, 6, 2, 6, 2, 5, 1, 3, 4, 6, 7, 2});
        startWeekDayMonthMap.put(2056, new int[]{0, 4, 7, 3, 7, 3, 7, 2, 4, 5, 7, 1, 3});
        startWeekDayMonthMap.put(2057, new int[]{0, 5, 1, 5, 1, 5, 1, 3, 5, 7, 1, 2, 4});
        startWeekDayMonthMap.put(2058, new int[]{0, 7, 2, 6, 2, 6, 2, 4, 6, 1, 2, 4, 5});
        startWeekDayMonthMap.put(2059, new int[]{0, 1, 4, 7, 4, 7, 3, 6, 1, 2, 4, 5, 7});
        startWeekDayMonthMap.put(2060, new int[]{0, 2, 5, 1, 5, 2, 5, 7, 2, 3, 5, 6, 1});
        startWeekDayMonthMap.put(2061, new int[]{0, 3, 6, 3, 6, 3, 6, 1, 3, 5, 6, 7, 2});
        startWeekDayMonthMap.put(2062, new int[]{0, 5, 7, 4, 7, 4, 7, 3, 4, 6, 7, 2, 3});
        startWeekDayMonthMap.put(2063, new int[]{0, 6, 2, 5, 2, 5, 1, 4, 6, 7, 2, 3, 5});
        startWeekDayMonthMap.put(2064, new int[]{0, 7, 3, 6, 3, 7, 3, 5, 7, 1, 3, 4, 6});
        startWeekDayMonthMap.put(2065, new int[]{0, 1, 4, 1, 4, 1, 4, 6, 1, 3, 4, 5, 7});
        startWeekDayMonthMap.put(2066, new int[]{0, 3, 6, 2, 5, 2, 5, 1, 2, 4, 6, 7, 1});
        startWeekDayMonthMap.put(2067, new int[]{0, 4, 7, 3, 7, 3, 6, 2, 4, 5, 7, 1, 3});
        startWeekDayMonthMap.put(2068, new int[]{0, 5, 1, 4, 1, 5, 1, 3, 5, 6, 1, 2, 4});
        startWeekDayMonthMap.put(2069, new int[]{0, 6, 2, 6, 2, 6, 2, 4, 6, 1, 2, 3, 5});
        startWeekDayMonthMap.put(2070, new int[]{0, 1, 4, 7, 3, 7, 3, 6, 7, 2, 4, 5, 7});
        startWeekDayMonthMap.put(2071, new int[]{0, 2, 5, 1, 5, 1, 4, 7, 2, 3, 5, 6, 1});
        startWeekDayMonthMap.put(2072, new int[]{0, 3, 6, 3, 6, 3, 6, 1, 3, 4, 6, 7, 2});
        startWeekDayMonthMap.put(2073, new int[]{0, 4, 7, 4, 7, 4, 7, 2, 4, 6, 7, 1, 3});
        startWeekDayMonthMap.put(2074, new int[]{0, 6, 2, 5, 1, 5, 1, 4, 6, 7, 2, 3, 5});
        startWeekDayMonthMap.put(2075, new int[]{0, 7, 3, 6, 3, 6, 2, 5, 7, 1, 3, 4, 6});
        startWeekDayMonthMap.put(2076, new int[]{0, 1, 4, 1, 4, 1, 4, 6, 1, 3, 4, 5, 7});
        startWeekDayMonthMap.put(2077, new int[]{0, 2, 5, 2, 5, 2, 5, 7, 2, 4, 5, 7, 1});
        startWeekDayMonthMap.put(2078, new int[]{0, 4, 7, 3, 6, 3, 6, 2, 4, 5, 7, 1, 3});
        startWeekDayMonthMap.put(2079, new int[]{0, 5, 1, 4, 1, 4, 7, 3, 5, 6, 1, 2, 4});
        startWeekDayMonthMap.put(2080, new int[]{0, 6, 2, 6, 2, 6, 2, 4, 6, 1, 2, 3, 5});
        startWeekDayMonthMap.put(2081, new int[]{0, 7, 3, 6, 3, 7, 3, 5, 7, 2, 3, 5, 7});
        startWeekDayMonthMap.put(2082, new int[]{0, 2, 4, 1, 4, 1, 4, 6, 1, 3, 4, 6, 1});
        startWeekDayMonthMap.put(2083, new int[]{0, 3, 6, 2, 6, 2, 5, 7, 2, 4, 5, 7, 2});
        startWeekDayMonthMap.put(2084, new int[]{0, 4, 7, 3, 7, 3, 6, 1, 3, 5, 6, 1, 3});
        startWeekDayMonthMap.put(2085, new int[]{0, 5, 1, 5, 1, 5, 7, 3, 5, 7, 1, 3, 5});
        startWeekDayMonthMap.put(2086, new int[]{0, 7, 2, 6, 2, 6, 2, 4, 6, 1, 2, 4, 6});
        startWeekDayMonthMap.put(2087, new int[]{0, 1, 4, 7, 4, 7, 3, 6, 1, 3, 4, 6, 1});
        startWeekDayMonthMap.put(2088, new int[]{0, 3, 5, 1, 5, 2, 4, 7, 2, 4, 5, 7, 2});
        startWeekDayMonthMap.put(2089, new int[]{0, 4, 6, 3, 6, 3, 6, 1, 3, 5, 6, 1, 3});
        startWeekDayMonthMap.put(2090, new int[]{0, 5, 7, 4, 7, 4, 7, 2, 4, 6, 7, 2, 4});
        /*  *//*start of new but dummy data just for test todo fix with real data*//*
        startWeekDayMonthMap.put(2091, new int[]{0, 5, 1, 4, 1, 4, 7, 3, 5, 6, 1, 2, 4});
        startWeekDayMonthMap.put(2092, new int[]{0, 6, 2, 5, 2, 6, 2, 4, 6, 7, 2, 3, 5});
        startWeekDayMonthMap.put(2093, new int[]{0, 7, 3, 7, 3, 7, 3, 5, 7, 2, 3, 4, 6});
        startWeekDayMonthMap.put(2094, new int[]{0, 2, 4, 1, 4, 1, 4, 6, 1, 3, 4, 6, 7});
        startWeekDayMonthMap.put(2095, new int[]{0, 3, 6, 2, 6, 2, 5, 1, 3, 4, 6, 7, 2});
        startWeekDayMonthMap.put(2096, new int[]{0, 4, 7, 3, 7, 4, 7, 2, 4, 5, 7, 1, 3});
        startWeekDayMonthMap.put(2097, new int[]{0, 5, 1, 5, 1, 5, 1, 3, 5, 7, 1, 2, 4});
        startWeekDayMonthMap.put(2098, new int[]{0, 7, 3, 6, 2, 6, 2, 5, 6, 1, 3, 4, 5});
        startWeekDayMonthMap.put(2099, new int[]{0, 1, 4, 7, 4, 7, 3, 6, 1, 2, 4, 5, 7});
        startWeekDayMonthMap.put(2100, new int[]{0, 4, 6, 3, 6, 3, 6, 1, 3, 5, 6, 1, 2});
        */
//        return startWeekDayMonthMap;
    }

    @SuppressLint("StaticFieldLeak")
    private class LongOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            int iniYear = 1950;
            int iniMonth = 1;
            int iniDay = 1;
            int finYear = 2033;
            int finMonth = 12;
            int finDay = 31;
            int startdateofMonth = 1;

            Model model;
            for (int i = iniYear; i <= finYear; i++) {
                for (int j = iniMonth; j <= 12; j++) {
                    for (int k = iniDay; k <= 31; k++) {
                        if (isEngDateInConversionRange(i, j, k)) {
                            try {
                                model = getNepaliDate(i, j, k);
                                int act_month = model.getMonth() + 1;
                                ContentValues contentValues = new ContentValues();
                                contentValues.put("year", model.getYear());
                                contentValues.put("month", act_month);
                                contentValues.put("day", model.getDay());
                                String id = model.getYear() + "/" + act_month + "/" + model.getDay();
                                contentValues.put("id", id);
                                contentValues.put("dayOfWeek", model.getDayOfWeek());
                                contentValues.put("startingDay", startdateofMonth);
                                databaseHelper.insertDate(contentValues);
                                if (startdateofMonth != 7) {
                                    startdateofMonth++;
                                } else
                                    startdateofMonth = 1;
                            } catch (IllegalArgumentException e) {
                                Log.d("Error Inserting:", "" + i + j + k);
                            }
                        }

                    }
                }
            }

            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {

            ArrayList<PatroModel> patroModelArrayList = databaseHelper.loadDate();
            Log.d("SIZES OF DATABASE", "" + patroModelArrayList.size());
            int i = 0;
            do {
                Log.d("PatroDate", "" + patroModelArrayList.get(i).getMonth() + "/" + patroModelArrayList.get(i).getDay() + "/" + patroModelArrayList.get(i).getYear());
                i++;
            }
            while (i != patroModelArrayList.size());
            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you
        }

        @Override
        protected void onPreExecute() {
            Toast.makeText(MainActivity.this, "Loading Data...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

}

