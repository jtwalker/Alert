package edu.westga.justinwalker.alert;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import edu.westga.justinwalker.alert.models.SharedConstants;

/**
 * Created by Family on 11/10/2014.
 */
public class ViewSettings extends Activity {

    private SharedPreferences settings;
    private Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_settings);

        this.settings = getSharedPreferences(SharedConstants.USER_PREFS, 0);
        this.editor = this.settings.edit();

        this.setCheckBoxBorderColor();

        this.initializeClickables();

        this.initializeFromSharedPreferences();
    }

    private void setCheckBoxBorderColor() {
        CheckBox syncCheckBox = (CheckBox) this.findViewById(R.id.syncCheckBox);
        int id = Resources.getSystem().getIdentifier("btn_check_holo_dark", "drawable", "android");
        syncCheckBox.setButtonDrawable(id);
    }

    private void initializeClickables() {
        LinearLayout syncLayout = (LinearLayout) this.findViewById(R.id.syncWithCalendarLayout);

        syncLayout.setOnClickListener(this.inputClickListener);
    }

    private void initializeFromSharedPreferences() {
        CheckBox syncCheckBox = (CheckBox) this.findViewById(R.id.syncCheckBox);
        syncCheckBox.setChecked(this.settings.getBoolean("sync", false));

        LinearLayout syncDetailsLayout = (LinearLayout) this.findViewById(R.id.syncDetailsLayout);
        if(syncCheckBox.isChecked()) {
            syncDetailsLayout.setVisibility(View.VISIBLE);
        }
        else {
            syncDetailsLayout.setVisibility(View.GONE);
        }
    }

    private View.OnClickListener inputClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.syncWithCalendarLayout:
                    changeCheckBoxCheck();
                    break;
                default:
                    Toast.makeText(getApplicationContext(), "Clicked", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void changeCheckBoxCheck() {
        CheckBox checkBox = (CheckBox) this.findViewById(R.id.syncCheckBox);
        LinearLayout syncDetailsLayout = (LinearLayout) this.findViewById(R.id.syncDetailsLayout);
        if(checkBox.isChecked()) {
            checkBox.setChecked(false);
            syncDetailsLayout.setVisibility(View.GONE);
        }
        else {
            checkBox.setChecked(true);
            syncDetailsLayout.setVisibility(View.VISIBLE);
        }

        this.editor.putBoolean("sync", checkBox.isChecked());
        this.editor.commit();
    }
}
