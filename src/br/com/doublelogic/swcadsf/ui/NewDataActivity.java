package br.com.doublelogic.swcadsf.ui;

import java.util.Date;
import java.util.TreeMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import br.com.doublelogic.swcadsf.CalculatorWeight;
import br.com.doublelogic.swcadsf.R;
import br.com.doublelogic.swcadsf.bean.UserSettings;
import br.com.doublelogic.swcadsf.bean.UserWeightData;
import br.com.doublelogic.swcadsf.common.constants.Gender;
import br.com.doublelogic.swcadsf.io.DataHandler;

public class NewDataActivity extends Activity {

	private UserSettings userSettings;
	private UserWeightData weightData;

	private DataHandler dataHandler;

	private EditText editTextWeight;
	private EditText editTextNeck;
	private EditText editTextWaist;
	private EditText editTextHip;

	private long userDateTime;

	private static final int REQUEST_DATE_TIME_PICKER = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newdata);

		loadUIReferences();

		dataHandler = new DataHandler(this);

		userDateTime = -1;

		if (getIntent().hasExtra(UserSettings.KEY)) {
			userSettings = (UserSettings) getIntent().getExtras().getSerializable(UserSettings.KEY);
			if (userSettings.getGender() == Gender.FEMALE) {
				editTextHip.setEnabled(true);
			} else {
				editTextHip.setEnabled(false);
			}

			if (getIntent().hasExtra(UserWeightData.KEY)) {
				weightData = (UserWeightData) getIntent().getExtras().getSerializable(UserWeightData.KEY);
				editTextWeight.setText(String.valueOf(weightData.getWeight()));
				if (weightData.getNeck() > 0) {
					editTextNeck.setText(String.valueOf(weightData.getNeck()));
				}
				if (weightData.getWaist() > 0) {
					editTextWaist.setText(String.valueOf(weightData.getWaist()));
				}
				if (weightData.getHip() > 0) {
					editTextHip.setText(String.valueOf(weightData.getHip()));
				}
			}
		} else {
			finish();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.weightdata_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.itemDate:
			Intent request = new Intent(this, DateTimePickerActivity.class);
			if (weightData != null) {
				request.putExtra(UserWeightData.KEY, weightData);
			}
			startActivityForResult(request, REQUEST_DATE_TIME_PICKER);
			break;
		}
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_DATE_TIME_PICKER:
				userDateTime = data.getExtras().getLong(DateTimePickerActivity.KEY, -1);
				break;
			}
		}
	}

	@Override
	public void finish() {
		setResult(RESULT_OK);
		super.finish();
	}

	private void loadUIReferences() {
		editTextWeight = (EditText) findViewById(R.id.editTextWeight);
		editTextNeck = (EditText) findViewById(R.id.editTextNeck);
		editTextWaist = (EditText) findViewById(R.id.editTextWaist);
		editTextHip = (EditText) findViewById(R.id.editTextHip);
	}

	public void confirmNewDataClickHandler(View view) {
		switch (view.getId()) {
		case R.id.buttonConfirmNewData:
			String weight = editTextWeight.getText().toString();
			if ((weight != null) && (weight.trim().length() > 0)) {

				if (weightData == null) {
					weightData = new UserWeightData();
					weightData.setTimeInMillis((new Date()).getTime());
				}

				if (userDateTime > 0) {
					TreeMap<Long, UserWeightData> weightDataMap = dataHandler.loadUserWeightData();
					weightDataMap.remove(weightData.getTimeInMillis());
					dataHandler.saveUserWeightData(weightDataMap);

					weightData.setTimeInMillis(userDateTime);
				}

				weightData.setWeight(Double.parseDouble(weight));

				String neck = editTextNeck.getText().toString();
				if ((neck != null) && (neck.trim().length() > 0)) {
					weightData.setNeck(Integer.parseInt(neck));
				}

				String waist = editTextWaist.getText().toString();
				if ((waist != null) && (waist.trim().length() > 0)) {
					weightData.setWaist(Integer.parseInt(waist));
				}

				if (userSettings.getGender() == Gender.FEMALE) {
					String hip = editTextHip.getText().toString();
					if ((hip != null) && (hip.trim().length() > 0)) {
						weightData.setHip(Integer.parseInt(hip));
					}
				}

				CalculatorWeight.bmi(userSettings, weightData);
				CalculatorWeight.bodyFat(userSettings, weightData);

				dataHandler.saveUserWeightData(weightData);

				finish();
			} else {
				Toast.makeText(this, getString(R.string.fill_weight), Toast.LENGTH_LONG).show();
			}
			break;
		}
	}
}