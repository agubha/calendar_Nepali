package nextnepal.com.test12123213;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class GridViewAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<PatroModel> listOfDaysInMonth;
    private TextView dateMajor, dateMinor;

    public GridViewAdapter(Context mainActivity) {
        this.context = mainActivity;
    }

    @Override
    public int getCount() {
        if (listOfDaysInMonth == null) {
            return 0;
        } else {
            return listOfDaysInMonth.size();
        }
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        convertView = inflater.inflate(R.layout.custom_calendar_day, parent, false);
        dateMajor = convertView.findViewById(R.id.major);
        dateMinor = convertView.findViewById(R.id.minor);
        if (listOfDaysInMonth.get(position).getId() != null) {
            dateMinor.setText(String.valueOf(listOfDaysInMonth.get(position).getEday()));
            dateMajor.setText(String.valueOf(listOfDaysInMonth.get(position).getNday()));
        }
        return convertView;
    }

    public void setDays(ArrayList<PatroModel> listOfDaysInMonth) {
        this.listOfDaysInMonth = listOfDaysInMonth;
        notifyDataSetChanged();
    }
}
