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
    private String [] result;
    private Context context;
    private String [] imageId;
    private static LayoutInflater inflater=null;

    public CustomAdapter(Activity mainActivity, String[] prgmNameList, String[] prgmImages) {
        result=prgmNameList;
        context=mainActivity;
        imageId=prgmImages;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return result.length;
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
        View rowView;

        rowView = inflater.inflate(R.layout.alarm_details_layout, parent, false);
        TextView firstLine = (TextView) rowView.findViewById(R.id.firstLine);
        ImageView image = (ImageView) rowView.findViewById(R.id.icon);
        firstLine.setText(result[position]);
        image.setImageBitmap(BitmapFactory.decodeFile(imageId[position]));
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(context, "You Clicked " + result[position], Toast.LENGTH_LONG).show();
            }
        });
        return rowView;
    }

}
