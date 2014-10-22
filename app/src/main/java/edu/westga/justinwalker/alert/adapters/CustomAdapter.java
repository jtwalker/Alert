package edu.westga.justinwalker.alert.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import edu.westga.justinwalker.alert.R;

/**
 * Created by Family on 10/21/2014.
 */
public class CustomAdapter extends BaseAdapter {
    private Context context;
    private String[] alarmImages, alarmTimes, alarmDays, alarmEmails, alarmRingtones, alarmSnooze;
    private static LayoutInflater inflater;

    public CustomAdapter(Activity activity, String[] images, String[] times, String[] days, String[] emails, String[] ringtones, String[] snooze) {
        this.context = activity;
        this.alarmImages = images;
        this.alarmTimes = times;
        this.alarmDays = days;
        this.alarmEmails = emails;
        this.alarmRingtones = ringtones;
        this.alarmSnooze = snooze;

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

        ImageView image = (ImageView) rowView.findViewById(R.id.icon);
        TextView alarmTimeView = (TextView) rowView.findViewById(R.id.alarmTimeView);
        TextView alarmDaysView = (TextView) rowView.findViewById(R.id.alarmDaysView);
        TextView alarmEmailsView = (TextView) rowView.findViewById(R.id.alarmEmailView);
        TextView alarmRingtonesView = (TextView) rowView.findViewById(R.id.alarmRingtoneView);
        TextView alarmSnoozeView = (TextView) rowView.findViewById(R.id.alarmSnoozeView);

        image.setImageBitmap(convertBitmap(this.alarmImages[position]));
        alarmTimeView.setText(this.alarmTimes[position]);
        alarmDaysView.setText(this.alarmDays[position]);
        alarmEmailsView.setText(this.alarmEmails[position]);
        alarmRingtonesView.setText(this.alarmRingtones[position]);
        alarmSnoozeView.setText(this.alarmSnooze[position]);

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "You Clicked " + alarmTimes[position], Toast.LENGTH_LONG).show();
            }
        });

        return rowView;
    }

    private Bitmap shrinkmethod(String file,int width,int height){
        BitmapFactory.Options bitopt=new BitmapFactory.Options();
        bitopt.inJustDecodeBounds=true;
        Bitmap bit=BitmapFactory.decodeFile(file, bitopt);

        int h=(int) Math.ceil(bitopt.outHeight/(float)height);
        int w=(int) Math.ceil(bitopt.outWidth/(float)width);

        if(h>1 || w>1){
            if(h>w){
                bitopt.inSampleSize=h;

            }else{
                bitopt.inSampleSize=w;
            }
        }
        bitopt.inJustDecodeBounds=false;
        bit=BitmapFactory.decodeFile(file, bitopt);



        return bit;

    }

    public static Bitmap convertBitmap(String path)   {

        Bitmap bitmap=null;
        BitmapFactory.Options bfOptions=new BitmapFactory.Options();
        bfOptions.inDither=false;                     //Disable Dithering mode
        bfOptions.inPurgeable=true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared
        bfOptions.inInputShareable=true;              //Which kind of reference will be used to recover the Bitmap data after being clear, when it will be used in the future
        bfOptions.inTempStorage=new byte[32 * 1024];
        //bfOptions.inJustDecodeBounds=true;
        bfOptions.inSampleSize = 20;

        File file=new File(path);
        FileInputStream fs=null;
        try {
            fs = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            if(fs!=null)
            {
                bitmap=BitmapFactory.decodeFileDescriptor(fs.getFD(), null, bfOptions);
            }
        } catch (IOException e) {

            e.printStackTrace();
        } finally{
            if(fs!=null) {
                try {
                    fs.close();
                } catch (IOException e) {

                    e.printStackTrace();
                }
            }
        }

        return bitmap;
    }

}
