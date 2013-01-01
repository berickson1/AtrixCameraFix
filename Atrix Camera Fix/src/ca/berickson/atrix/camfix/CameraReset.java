package ca.berickson.atrix.camfix;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

public class CameraReset extends Activity {

    private static final String LOG_TAG = "Atrix.CamFix";

    // Controls
    private static Button btnKillMediaServer;
    private static CheckBox chkKillGallery;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_reset);

        // Find Controls
        btnKillMediaServer = (Button) this
                .findViewById(R.id.btnKillMediaServer);
        chkKillGallery = (CheckBox) this.findViewById(R.id.chkKillGallery);

        // Setup Button on-click
        btnKillMediaServer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                killGallery();
                killMediaServer();
                displayToast("Done!");
            }
        });

        // Set CheckBox State
        chkKillGallery.setChecked(getGalleryCheck());

        // Setup CheckBox on-check
        chkKillGallery
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView,
                            boolean isChecked) {
                        setGalleryCheck(isChecked);
                    }
                });
    }

    private void killMediaServer() {
        killProcess("mediaserver");
    }

    private void killGallery() {
        if (chkKillGallery.isChecked()) {
            Log.d(LOG_TAG, "Gallery CheckBox set");
            killProcess("com.android.gallery3d");
        }
    }

    private void setGalleryCheck(Boolean value) {
        SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
        editor.putBoolean("kill-gallery", value);
        editor.commit();
    }

    private Boolean getGalleryCheck() {
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        return prefs.getBoolean("kill-gallery", true);
    }

    /* Utility Functions */
    private void killProcess(String proc) {
        executeAsRoot("killall " + proc + "");
    }

    private void displayToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

    private void executeAsRoot(String command) {
        try {
            Process process;
            process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(
                    process.getOutputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
            os.writeBytes("ls -l\n");
            // Waiting here for root
            reader.readLine();
            reader.close();
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            os.close();
            process.waitFor();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Caught an IO exception.", e);
            displayToast("An Error Occurred. Did you grant root privileges?");
        } catch (Exception e) {
            Log.e(LOG_TAG, "Caught an exception.", e);
            displayToast("OOPS! Something bad happened! Please report this.");
        }
    }
}
