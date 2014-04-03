package br.com.doublelogic.swcadsf.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.concurrent.ExecutionException;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import br.com.doublelogic.swcadsf.R;
import br.com.doublelogic.swcadsf.bean.UserBackupData;
import br.com.doublelogic.swcadsf.bean.UserSettings;
import br.com.doublelogic.swcadsf.common.constants.BackupDataType;
import br.com.doublelogic.swcadsf.common.constants.IOConstants;
import br.com.doublelogic.swcadsf.common.constants.LogTags;
import br.com.doublelogic.swcadsf.io.DataHandler;
import br.com.doublelogic.swcadsf.io.dropbox.DropboxDownload;
import br.com.doublelogic.swcadsf.io.dropbox.DropboxUpload;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.TokenPair;

public class DropboxActivity extends android.app.Activity {

	private UserSettings userSettings;
	private UserBackupData backupData;

	private DataHandler dataHandler;

	private static final String APP_KEY = "o2s4aw1dvoqxl4l";
	private static final String APP_SECRET = "yyn3cd3xpntvvnl";

	private static final AccessType ACCESS_TYPE = AccessType.APP_FOLDER;

	private static final String ACCOUNT_PREFS_NAME = "prefs";
	private static final String ACCESS_KEY_NAME = "ACCESS_KEY";
	private static final String ACCESS_SECRET_NAME = "ACCESS_SECRET";

	private DropboxAPI<AndroidAuthSession> mApi;

	private boolean mLoggedIn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dropbox);

		dataHandler = new DataHandler(this);

		if (getIntent().hasExtra(UserBackupData.KEY)) {
			backupData = (UserBackupData) getIntent().getExtras().getSerializable(UserBackupData.KEY);
		} else {
			finish();
		}

		AndroidAuthSession session = buildSession();
		mApi = new DropboxAPI<AndroidAuthSession>(session);
		mApi.getSession().startAuthentication(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		AndroidAuthSession session = mApi.getSession();

		// The next part must be inserted in the onResume() method of the
		// activity from which session.startAuthentication() was called, so
		// that Dropbox authentication completes properly.
		if (session.authenticationSuccessful()) {
			try {
				// Mandatory call to complete the auth
				session.finishAuthentication();

				// Store it locally in our app for later use
				TokenPair tokens = session.getAccessTokenPair();
				storeKeys(tokens.key, tokens.secret);
				setLoggedIn(true);

				if (backupData.getType() == BackupDataType.EXPORT) {
					File[] listFiles = dataHandler.getDataFiles();
					if (listFiles != null) {
						DropboxUpload upload = new DropboxUpload(this, mApi, listFiles);
						upload.execute();

						// waits the result;
						upload.get();

						finish();
					}
				} else if (backupData.getType() == BackupDataType.IMPORT) {
					DropboxDownload download = new DropboxDownload(this, mApi);
					download.execute();

					if (download.get()) {
						for (File file : getCacheDir().listFiles()) {
							if (file.isFile()) {
								if (String.valueOf(IOConstants.USER_SETTINGS_EXP_IN_FILENAME).equals(file.getName())) {
									FileInputStream fis = new FileInputStream(file);
									FileOutputStream fos = dataHandler.openUserSettingsFile();
									copyFile(fis, fos);
								} else if (String.valueOf(IOConstants.WEIGHT_DATA_EXP_IN_FILENAME).equals(file.getName())) {
									FileInputStream fis = new FileInputStream(file);
									FileOutputStream fos = dataHandler.openUserWeightDataFile();
									copyFile(fis, fos);
								}
							}
						}
						userSettings = dataHandler.loadUserSettings();
						showToast(getString(R.string.import_success));
					} else {
						showToast(getString(R.string.import_failed));
					}
				}
			} catch (IllegalStateException e) {
				showToast(getString(R.string.import_failed));
				Log.i(String.valueOf(LogTags.DROPBOX_HANDLER), "Error authenticating", e);
			} catch (InterruptedException e) {
				showToast(getString(R.string.import_failed));
				Log.e(String.valueOf(LogTags.DROPBOX_HANDLER), "AsyncTask interrupted error", e);
			} catch (ExecutionException e) {
				showToast(getString(R.string.import_failed));
				Log.e(String.valueOf(LogTags.DROPBOX_HANDLER), "AsyncTask execution error", e);
			} catch (FileNotFoundException e) {
				showToast(getString(R.string.import_failed));
				Log.e(String.valueOf(LogTags.DROPBOX_HANDLER), "Can´t save imported data", e);
			}
			finish();
		}
	}

	private void copyFile(FileInputStream fis, FileOutputStream fos) {
		try {
			byte[] buf = new byte[1024];
			int len;
			while ((len = fis.read(buf)) > 0) {
				fos.write(buf, 0, len);
			}
		} catch (Exception e) {
			Log.e(String.valueOf(LogTags.DROPBOX_HANDLER), "Error importing data", e);
		} finally {
			dataHandler.closeFileInput(fis);
			dataHandler.closeFileOutput(fos);
		}
	}

	@Override
	public void finish() {
		if (mLoggedIn) {
			// Remove credentials from the session
			mApi.getSession().unlink();

			// Clear our stored keys
			clearKeys();
		}

		Intent data = new Intent();
		if (userSettings != null) {
			data.putExtra(UserSettings.KEY, userSettings);
		}

		setResult(RESULT_OK, data);
		super.finish();
	}

	private void showToast(String msg) {
		Toast error = Toast.makeText(this, msg, Toast.LENGTH_LONG);
		error.show();
	}

	/**
	 * Convenience function to change UI state based on being logged in
	 */
	private void setLoggedIn(boolean loggedIn) {
		mLoggedIn = loggedIn;
	}

	/**
	 * Shows keeping the access keys returned from Trusted Authenticator in a local store, rather than storing user name & password, and
	 * re-authenticating each time (which is not to be done, ever).
	 * 
	 * @return Array of [access_key, access_secret], or null if none stored
	 */
	private String[] getKeys() {
		SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
		String key = prefs.getString(ACCESS_KEY_NAME, null);
		String secret = prefs.getString(ACCESS_SECRET_NAME, null);
		if ((key != null) && (secret != null)) {
			String[] ret = new String[2];
			ret[0] = key;
			ret[1] = secret;
			return ret;
		} else {
			return null;
		}
	}

	/**
	 * Shows keeping the access keys returned from Trusted Authenticator in a local store, rather than storing user name & password, and
	 * re-authenticating each time (which is not to be done, ever).
	 */
	private void storeKeys(String key, String secret) {
		// Save the access key for later
		SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
		Editor edit = prefs.edit();
		edit.putString(ACCESS_KEY_NAME, key);
		edit.putString(ACCESS_SECRET_NAME, secret);
		edit.commit();
	}

	private void clearKeys() {
		SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
		Editor edit = prefs.edit();
		edit.clear();
		edit.commit();
	}

	private AndroidAuthSession buildSession() {
		AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);
		AndroidAuthSession session;

		String[] stored = getKeys();
		if (stored != null) {
			AccessTokenPair accessToken = new AccessTokenPair(stored[0], stored[1]);
			session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE, accessToken);
		} else {
			session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE);
		}

		return session;
	}

}