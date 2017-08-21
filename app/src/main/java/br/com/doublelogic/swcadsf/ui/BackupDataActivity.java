package br.com.doublelogic.swcadsf.ui;

import java.util.TreeMap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import br.com.doublelogic.swcadsf.R;
import br.com.doublelogic.swcadsf.bean.UserBackupData;
import br.com.doublelogic.swcadsf.bean.UserSettings;
import br.com.doublelogic.swcadsf.bean.UserWeightData;
import br.com.doublelogic.swcadsf.common.constants.BackupDataType;
import br.com.doublelogic.swcadsf.io.DataHandler;
import br.com.doublelogic.swcadsf.io.SDCardHandler;

public class BackupDataActivity extends android.app.Activity {

	private UserSettings userSettings;
	private UserBackupData backupData;

	private DataHandler dataHandler;
	private SDCardHandler sdCardHandler;

	private static final int REQUEST_CODE_DROPBOX = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.backup_data);

		dataHandler = new DataHandler(this);
		sdCardHandler = new SDCardHandler(this);

		if (getIntent().hasExtra(UserBackupData.KEY)) {
			backupData = (UserBackupData) getIntent().getExtras().getSerializable(UserBackupData.KEY);
		} else {
			finish();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_CODE_DROPBOX:
				if (data.hasExtra(UserSettings.KEY)) {
					userSettings = (UserSettings) data.getExtras().getSerializable(UserSettings.KEY);
				}
				break;
			}
		}
	}

	@Override
	public void finish() {
		Intent data = new Intent();
		if (userSettings != null) {
			data.putExtra(UserSettings.KEY, userSettings);
		}

		setResult(RESULT_OK, data);
		super.finish();
	}

	public void sdCardClickHandler(View view) {
		switch (view.getId()) {
		case R.id.buttonSDCard:
			if (backupData.getType() == BackupDataType.EXPORT) {
				UserSettings userSettings = dataHandler.loadUserSettings();
				TreeMap<Long, UserWeightData> weightData = dataHandler.loadUserWeightData();
				if ((userSettings != null) && (weightData != null)) {
					if (sdCardHandler.saveUserSettings(userSettings)) {
						if (sdCardHandler.saveUserWeightData(weightData)) {
							Toast.makeText(this, getString(R.string.export_success), Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(this, getString(R.string.export_failed), Toast.LENGTH_LONG).show();
						}
					} else {
						Toast.makeText(this, getString(R.string.export_failed), Toast.LENGTH_LONG).show();
					}
				} else {
					Toast.makeText(this, getString(R.string.export_failed), Toast.LENGTH_LONG).show();
				}
			} else if (backupData.getType() == BackupDataType.IMPORT) {
				userSettings = sdCardHandler.loadUserSettings();
				TreeMap<Long, UserWeightData> weightData = sdCardHandler.loadUserWeightData();
				if ((userSettings != null) && (weightData != null)) {
					dataHandler.saveUserSettings(userSettings);
					dataHandler.saveUserWeightData(weightData);
					showToast(getString(R.string.import_success));
				} else {
					showToast(getString(R.string.import_failed));
				}
			}
			break;
		}
		finish();
	}

	public void dropboxClickHandler(View view) {
		switch (view.getId()) {
		case R.id.buttonDropbox:
			final Intent request = new Intent(this, DropboxActivity.class);
			if (backupData.getType() == BackupDataType.EXPORT) {
				request.putExtra(UserBackupData.KEY, new UserBackupData(BackupDataType.EXPORT));
			} else if (backupData.getType() == BackupDataType.IMPORT) {
				request.putExtra(UserBackupData.KEY, new UserBackupData(BackupDataType.IMPORT));
			}
			startActivityForResult(request, REQUEST_CODE_DROPBOX);
			break;
		}
	}

	private void showToast(String msg) {
		Toast message = Toast.makeText(this, msg, Toast.LENGTH_LONG);
		message.show();
	}

}