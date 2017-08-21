package br.com.doublelogic.swcadsf.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import br.com.doublelogic.swcadsf.R;
import br.com.doublelogic.swcadsf.bean.UserSettings;
import br.com.doublelogic.swcadsf.common.constants.Gender;
import br.com.doublelogic.swcadsf.io.DataHandler;

public class UserSettingsActivity extends Activity {

	private UserSettings userSettings;

	private DataHandler dataHandler;

	private EditText editTextHeight;
	private RadioButton radioMale;
	private RadioButton radioFemale;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_settings);

		loadUIReferences();

		dataHandler = new DataHandler(this);
		userSettings = dataHandler.loadUserSettings();
		if (userSettings != null) {
			editTextHeight.setText(String.valueOf(userSettings.getHeight()));
			if (userSettings.getGender() == Gender.FEMALE) {
				radioFemale.setChecked(true);
				radioMale.setChecked(false);
			} else {
				radioFemale.setChecked(false);
				radioMale.setChecked(true);
			}
		}
	}

	@Override
	public void finish() {
		if (userSettings == null) {
			userSettings = new UserSettings();
		}

		Intent data = new Intent();
		data.putExtra(UserSettings.KEY, userSettings);

		setResult(RESULT_OK, data);
		super.finish();
	}

	private void loadUIReferences() {
		editTextHeight = (EditText) findViewById(R.id.editTextHeight);
		radioMale = (RadioButton) findViewById(R.id.radioMale);
		radioFemale = (RadioButton) findViewById(R.id.radioFemale);
	}

	public void confirmClickHandler(View view) {
		switch (view.getId()) {
		case R.id.buttonConfirm:
			userSettings = new UserSettings();

			String height = editTextHeight.getText().toString();
			if ((height != null) && (height.trim().length() > 0)) {
				userSettings.setHeight(Integer.parseInt(height));
			} else {
				userSettings.setHeight(UserSettings.DEFAULT_HEIGHT_VALUE);
			}

			if (radioMale.isChecked()) {
				userSettings.setGender(Gender.MALE);
			} else {
				userSettings.setGender(Gender.FEMALE);
			}

			dataHandler.saveUserSettings(userSettings);
			finish();
			break;
		}
	}
}
