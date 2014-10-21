package edu.westga.justinwalker.alert.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.provider.Browser;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import edu.westga.justinwalker.alert.R;

/**
 * Created by Family on 10/21/2014.
 */
public class ImageCursorAdapter extends SimpleCursorAdapter {

    private Cursor cursor;
    private Context context;

    public ImageCursorAdapter(Context context, int layout, Cursor cursor, String[] from, int[] to, int flags) {
        super(context, layout, cursor, from, to, flags);
        this.cursor = cursor;
        this.context = context;
    }

    public View getView(int position, View inView, ViewGroup parent) {
        View view = inView;
        if(view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.alarm_details_layout, null);
        }
        this.cursor.moveToPosition(position);
        String bookmark = this.cursor.getString(this.cursor.getColumnIndex(Browser.BookmarkColumns.TITLE));
        byte[] favicon = this.cursor.getBlob(this.cursor.getColumnIndex(Browser.BookmarkColumns.FAVICON));
        if(favicon != null) {
            ImageView imageView = (ImageView) view.findViewById(R.id.icon);
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(favicon, 0, favicon.length));
        }

        return view;
    }
}
