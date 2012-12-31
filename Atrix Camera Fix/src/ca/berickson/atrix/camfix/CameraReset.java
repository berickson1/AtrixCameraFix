package ca.berickson.atrix.camfix;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.R.string;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.view.View;
import android.view.ViewDebug.IntToString;
import android.widget.Button;
import android.widget.Toast;

public class CameraReset extends Activity {

	private static Button btnKillMediaServer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera_reset);
		btnKillMediaServer = (Button) this.findViewById(R.id.btnKillMediaServer);
		btnKillMediaServer.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				killMediaServer();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_camera_reset, menu);
		return true;
	}

	private void killMediaServer() {
		try {
			Process process;
			process = Runtime.getRuntime().exec("su");
			DataOutputStream os = new DataOutputStream(process.getOutputStream());
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			os.writeBytes("ls -l\n");
			// Waiting here for root
			reader.readLine();
			reader.close();
			os.writeBytes("kill -9 `pidof mediaserver`\n");
			os.writeBytes("exit\n");
			os.flush();
			os.close();
			process.waitFor();
			displayToast("Done!");
		} catch (IOException e) {
			displayToast("An Error Occurred. Did you grant root privileges?");
		} catch (Exception e) {
			displayToast("OOPS! Something bad happened! Please report this.");
			e.printStackTrace();
		}
	}

	private void displayToast(String msg) {
		Context context = getApplicationContext();
		Toast.makeText(context, msg, Toast.LENGTH_LONG).show();

	}
}
