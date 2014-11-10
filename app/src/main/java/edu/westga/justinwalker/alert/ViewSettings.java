package edu.westga.justinwalker.alert;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.storeSyncEmail();
    }

    private void setCheckBoxBorderColor() {
        CheckBox syncCheckBox = (CheckBox) this.findViewById(R.id.syncCheckBox);
        CheckBox autoSyncCheckBox = (CheckBox) this.findViewById(R.id.autoSyncCheckBox);
        int id = Resources.getSystem().getIdentifier("btn_check_holo_dark", "drawable", "android");
        syncCheckBox.setButtonDrawable(id);
        autoSyncCheckBox.setButtonDrawable(id);
    }

    private void initializeClickables() {
        LinearLayout syncLayout = (LinearLayout) this.findViewById(R.id.syncWithCalendarLayout);
        LinearLayout autoSyncLayout = (LinearLayout) this.findViewById(R.id.autoSyncLayout);

        syncLayout.setOnClickListener(this.inputClickListener);
        autoSyncLayout.setOnClickListener(this.inputClickListener);
    }

    private void initializeFromSharedPreferences() {
        CheckBox syncCheckBox = (CheckBox) this.findViewById(R.id.syncCheckBox);
        syncCheckBox.setChecked(this.settings.getBoolean("sync", false));

        CheckBox autoSyncCheckBox = (CheckBox) this.findViewById(R.id.autoSyncCheckBox);
        autoSyncCheckBox.setChecked(this.settings.getBoolean("autosync", false));

        LinearLayout syncDetailsLayout = (LinearLayout) this.findViewById(R.id.syncDetailsLayout);
        if(syncCheckBox.isChecked()) {
            syncDetailsLayout.setVisibility(View.VISIBLE);
        }
        else {
            syncDetailsLayout.setVisibility(View.GONE);
        }

        EditText syncEmail = (EditText) this.findViewById(R.id.syncEmailEditText);
        syncEmail.setText(this.settings.getString("syncemail", ""));
    }

    private void storeSyncEmail() {
        EditText syncEmail = (EditText) this.findViewById(R.id.syncEmailEditText);
        this.editor.putString("syncemail", syncEmail.getText().toString());
        this.editor.commit();
    }

    private View.OnClickListener inputClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.syncWithCalendarLayout:
                    changeCheckBoxCheck();
                    break;
                case R.id.autoSyncLayout:
                    changeAutoSyncCheckBoxCheck();
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

    private void changeAutoSyncCheckBoxCheck() {
        CheckBox checkBox = (CheckBox) this.findViewById(R.id.autoSyncCheckBox);
        if(checkBox.isChecked()) {
            checkBox.setChecked(false);
        }
        else {
            checkBox.setChecked(true);
        }

        this.editor.putBoolean("autosync", checkBox.isChecked());
        this.editor.commit();
    }
}
