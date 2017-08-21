package br.com.doublelogic.swcadsf.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import br.com.doublelogic.swcadsf.R;
import br.com.doublelogic.swcadsf.bean.UserBackupData;
import br.com.doublelogic.swcadsf.bean.UserSettings;
import br.com.doublelogic.swcadsf.common.SendMail;
import br.com.doublelogic.swcadsf.common.constants.BackupDataType;
import br.com.doublelogic.swcadsf.io.DataHandler;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class SimpleWeightControlActivity extends Activity {

	private UserSettings userSettings;

	private DataHandler dataHandler;

	private GoogleAnalyticsTracker tracker;
	
	private static final int REQUEST_CODE_SETTINGS = 1;
	private static final int REQUEST_CODE_NEW_DATA = 2;
	private static final int REQUEST_CODE_SUMMARY = 3;
	private static final int REQUEST_CODE_HISTORY = 4;
	private static final int REQUEST_CODE_BACKUP_DATA = 5;
	private static final int REQUEST_CODE_REMOVE_DATA = 6;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		dataHandler = new DataHandler(this);
		userSettings = dataHandler.loadUserSettings();
		
		tracker = GoogleAnalyticsTracker.getInstance();
		// Start the tracker in manual dispatch mode...
		tracker.startNewSession("UA-25775234-2", 300, this);
		
		tracker.trackPageView("/main");
		
		if (userSettings == null) {
			Intent request = new Intent(this, UserSettingsActivity.class);
			startActivityForResult(request, REQUEST_CODE_SETTINGS);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.itemExport:
			tracker.trackPageView("/export");
			Intent requestExport = new Intent(this, BackupDataActivity.class);
			requestExport.putExtra(UserBackupData.KEY, new UserBackupData(BackupDataType.EXPORT));
			startActivityForResult(requestExport, REQUEST_CODE_BACKUP_DATA);
			break;
		case R.id.itemImport:
			tracker.trackPageView("/import");
			Intent requestImport = new Intent(this, BackupDataActivity.class);
			requestImport.putExtra(UserBackupData.KEY, new UserBackupData(BackupDataType.IMPORT));
			startActivityForResult(requestImport, REQUEST_CODE_BACKUP_DATA);
			break;
		case R.id.itemRemove:
			tracker.trackPageView("/remove");
			Intent requestRemove = new Intent(this, UserDataActivity.class);
			requestRemove.putExtra(UserSettings.KEY, userSettings);
			startActivityForResult(requestRemove, REQUEST_CODE_REMOVE_DATA);
			break;
		case R.id.itemAbout:
			tracker.trackPageView("/about");
			Intent requestAbout = new Intent(this, AboutActivity.class);
			startActivityForResult(requestAbout, -1);
			break;
		case R.id.itemSuggestion:
			SendMail mail = new SendMail(this);
			mail.send("Feedback", getString(R.string.message_feedback));
			break;
		}
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_CODE_SETTINGS:
			case REQUEST_CODE_BACKUP_DATA:
				if (data.hasExtra(UserSettings.KEY)) {
					userSettings = (UserSettings) data.getExtras().getSerializable(UserSettings.KEY);
				}
				break;
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		tracker.stopSession();
	}

	public void newDataClickHandler(View view) {
		switch (view.getId()) {
		case R.id.buttonNewData:
			tracker.trackPageView("/weight_data");
			Intent request = new Intent(this, NewDataActivity.class);
			request.putExtra(UserSettings.KEY, userSettings);
			startActivityForResult(request, REQUEST_CODE_NEW_DATA);
			break;
		}
	}

	public void summaryClickHandler(View view) {
		switch (view.getId()) {
		case R.id.buttonSummary:
			tracker.trackPageView("/summary");
			Intent request = new Intent(this, UserSummaryActivity.class);
			request.putExtra(UserSettings.KEY, userSettings);
			startActivityForResult(request, REQUEST_CODE_SUMMARY);
			break;
		}
	}

	public void historyClickHandler(View view) {
		switch (view.getId()) {
		case R.id.buttonHistory:
			tracker.trackPageView("/history");
			Intent request = new Intent(this, UserHistoryActivity.class);
			request.putExtra(UserSettings.KEY, userSettings);
			startActivityForResult(request, REQUEST_CODE_HISTORY);
			break;
		}
	}

	public void settingsClickHandler(View view) {
		switch (view.getId()) {
		case R.id.buttonSettings:
			tracker.trackPageView("/settings");
			Intent request = new Intent(this, UserSettingsActivity.class);
			startActivityForResult(request, REQUEST_CODE_SETTINGS);
			break;
		}
	}
}