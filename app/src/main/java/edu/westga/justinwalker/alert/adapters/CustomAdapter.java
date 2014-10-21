package edu.westga.justinwalker.alert.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import edu.westga.justinwalker.alert.R;

/**
 * Created by Family on 10/21/2014.
 */
public class CustomAdapter extends BaseAdapter {
    private final String[] alarmDays;
    private String [] alarmTimes;
    private Context context;
    private String [] alarmImages;
    private static LayoutInflater inflater;

    public CustomAdapter(Activity activity, String[] images, String[] times, String[] days) {
        this.alarmTimes = times;
        this.context = activity;
        this.alarmImages = images;
        this.alarmDays = days;
        this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return alarmTimes.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView = inflater.inflate(R.layout.alarm_details_layout, parent, false);
        TextView alarmTimeView = (TextView) rowView.findViewById(R.id.alarmTimeView);
        TextView alarmDaysView = (TextView) rowView.findViewById(R.id.alarmDaysView);
        ImageView image = (ImageView) rowView.findViewById(R.id.icon);

        alarmTimeView.setText(this.alarmTimes[position]);
        alarmDaysView.setText(this.alarmTimes[position]);
        image.setImageBitmap(BitmapFactory.decodeFile(this.alarmImages[position]));

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "You Clicked " + alarmTimes[position], Toast.LENGTH_LONG).show();
            }
        });

        return rowView;
    }

}
