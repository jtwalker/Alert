package edu.westga.justinwalker.alert;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import edu.westga.justinwalker.alert.models.SharedConstants;
import yuku.ambilwarna.AmbilWarnaDialog;

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
        Button syncButton = (Button) this.findViewById(R.id.syncButton);
        Button backgroundColorButton = (Button) this.findViewById(R.id.backgroundColorButton);
        Button fontColorButton = (Button) this.findViewById(R.id.fontColorButton);
        Button resetColorButton = (Button) this.findViewById(R.id.resetColorButton);

        syncLayout.setOnClickListener(this.inputClickListener);
        autoSyncLayout.setOnClickListener(this.inputClickListener);
        syncButton.setOnClickListener(this.inputClickListener);
        backgroundColorButton.setOnClickListener(this.inputClickListener);
        fontColorButton.setOnClickListener(this.inputClickListener);
        resetColorButton.setOnClickListener(this.inputClickListener);
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
                case R.id.syncButton:
                    syncNow();
                    break;
                case R.id.backgroundColorButton:
                    changeBackgroundColor();
                    break;
                case R.id.fontColorButton:
                    changeFontColor();
                    break;
                case R.id.resetColorButton:
                    resetColor();
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

    private void syncNow() {

    }

    private void changeBackgroundColor() {

        AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, getResources().getColor(R.color.background_color), new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                editor.putInt("backgroundcolor", color);
                editor.commit();
                Toast.makeText(getApplicationContext(), "" + color, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
                Toast.makeText(getApplicationContext(), "Cancelled", Toast.LENGTH_LONG).show();
            }
        });

        dialog.show();
    }

    private void changeFontColor() {

        AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, getResources().getColor(R.color.text_color), new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                editor.putInt("fontcolor", color);
                editor.commit();
                Toast.makeText(getApplicationContext(), "" + color, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
                Toast.makeText(getApplicationContext(), "Cancelled", Toast.LENGTH_LONG).show();
            }
        });

        dialog.show();
    }

    private void resetColor() {
        editor.putInt("backgroundcolor", getResources().getColor(R.color.background_color));
        editor.putInt("fontcolor", getResources().getColor(R.color.text_color));
        editor.commit();
        Toast.makeText(getApplicationContext(), "Theme Reset", Toast.LENGTH_SHORT).show();
    }
}
