package edu.westga.justinwalker.alert.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import edu.westga.justinwalker.alert.R;

/**
 * Created by Justin Walker on 10/8/2014.
 */
public class ViewAlarmsAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] times;
    private final String[] days;
    private final String[] pictures;

    public ViewAlarmsAdapter(Context context, String[] values, String [] days, String[] pictures) {
        super(context, R.layout.alarm_details_layout, values);
        this.context = context;
        this.times = values;
        this.days = days;
        this.pictures = pictures;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.alarm_details_layout, parent, false);

        TextView firstTextView = (TextView) rowView.findViewById(R.id.firstLine);
        TextView textView = (TextView) rowView.findViewById(R.id.secondLine);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);

        firstTextView.setText(this.days[position]);
        textView.setText(times[position]);

        imageView.setImageBitmap(BitmapFactory.decodeFile(this.pictures[position]));;

        return rowView;
    }
}
