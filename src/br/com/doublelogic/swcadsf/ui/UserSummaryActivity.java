package br.com.doublelogic.swcadsf.ui;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.TreeMap;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import br.com.doublelogic.swcadsf.R;
import br.com.doublelogic.swcadsf.bean.UserSettings;
import br.com.doublelogic.swcadsf.bean.UserWeightData;
import br.com.doublelogic.swcadsf.common.constants.Gender;
import br.com.doublelogic.swcadsf.io.DataHandler;

public class UserSummaryActivity extends Activity {

	private UserSettings userSettings;

	private DataHandler dataHandler;

	private Button buttonChart;

	private TextView textViewGender;
	private TextView textViewHeight;
	private TextView textViewWeight;
	private TextView textViewFat;
	private TextView textViewBmi;
	private TextView textViewCondition;
	private TextView textViewWeightChange;
	private TextView textViewTotalLoss;

	private static final int REQUEST_CODE_CHART = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_summary);

		loadUIReferences();

		dataHandler = new DataHandler(this);

		if (getIntent().hasExtra(UserSettings.KEY)) {
			userSettings = (UserSettings) getIntent().getExtras().getSerializable(UserSettings.KEY);
		} else {
			finish();
		}

		setSummary();
	}

	private void loadUIReferences() {
		buttonChart = (Button) findViewById(R.id.buttonChart);
		textViewGender = (TextView) findViewById(R.id.textViewGender);
		textViewHeight = (TextView) findViewById(R.id.textViewHeight);
		textViewWeight = (TextView) findViewById(R.id.textViewWeight);
		textViewFat = (TextView) findViewById(R.id.textViewFat);
		textViewBmi = (TextView) findViewById(R.id.textViewBmi);
		textViewCondition = (TextView) findViewById(R.id.textViewCondition);
		textViewWeightChange = (TextView) findViewById(R.id.textViewWeightChange);
		textViewTotalLoss = (TextView) findViewById(R.id.textViewTotalLoss);
	}

	private void setSummary() {
		userSettings();
		userWeightData();
	}

	private void userSettings() {
		if (userSettings.getGender() == Gender.MALE) {
			textViewGender.setText(getString(R.string.male));
		} else {
			textViewGender.setText(getString(R.string.female));
		}

		textViewHeight.setText(userSettings.getHeight() + " " + getString(R.string.centimeter));
	}

	private void userWeightData() {
		TreeMap<Long, UserWeightData> weightData = dataHandler.loadUserWeightData();

		if ((weightData != null) && (weightData.size() > 0)) {
			UserWeightData firstWeight = weightData.get(weightData.firstKey());
			UserWeightData lastWeight = weightData.get(weightData.lastKey());

			NumberFormat nf = new DecimalFormat("###.##");

			textViewWeight.setText(nf.format(lastWeight.getWeight()) + " " + getString(R.string.kilogram));
			if (lastWeight.getBodyFat() > 0) {
				textViewFat.setText(nf.format(lastWeight.getBodyFat()));
			} else {
				textViewFat.setText(getString(R.string.no_content));
			}

			if (lastWeight.getBmi() > 0) {
				textViewBmi.setText(nf.format(lastWeight.getBmi()));
				if (lastWeight.getBmi() < 18.49) {
					textViewCondition.setText(getString(R.string.condition_underweight));
				} else if ((lastWeight.getBmi() >= 18.50) && (lastWeight.getBmi() <= 24.99)) {
					textViewCondition.setText(getString(R.string.condition_normal));
				} else if ((lastWeight.getBmi() >= 25.00) && (lastWeight.getBmi() <= 29.99)) {
					textViewCondition.setText(getString(R.string.condition_overweight));
				} else {
					textViewCondition.setText(getString(R.string.condition_obese));
				}
			} else {
				textViewBmi.setText(getString(R.string.no_content));
				textViewCondition.setText(getString(R.string.no_content));
			}

			if (weightData.size() > 1) {
				UserWeightData[] weightDataArray = weightData.values().toArray(new UserWeightData[weightData.size()]);
				UserWeightData secondLastWeight = weightDataArray[weightDataArray.length - 2];
				double change = lastWeight.getWeight() - secondLastWeight.getWeight();
				textViewWeightChange.setText(nf.format(change) + " " + getString(R.string.kilogram));
				if (change > 0) {
					textViewWeightChange.setTextColor(Color.RED);
				} else if (change < 0) {
					textViewWeightChange.setTextColor(Color.GREEN);
				}
			} else {
				textViewWeightChange.setText(getString(R.string.no_content));
			}

			if (firstWeight.getTimeInMillis() != lastWeight.getTimeInMillis()) {
				textViewTotalLoss.setText(nf.format(lastWeight.getWeight() - firstWeight.getWeight()) + " " + getString(R.string.kilogram));
			} else {
				textViewTotalLoss.setText(getString(R.string.no_content));
			}
		} else {
			buttonChart.setEnabled(false);
			textViewWeight.setText(getString(R.string.no_content));
			textViewFat.setText(getString(R.string.no_content));
			textViewBmi.setText(getString(R.string.no_content));
			textViewCondition.setText(getString(R.string.no_content));
			textViewWeightChange.setText(getString(R.string.no_content));
			textViewTotalLoss.setText(getString(R.string.no_content));
		}
	}

	public void chartClickHandler(View view) {
		switch (view.getId()) {
		case R.id.buttonChart:
			Intent request = new Intent(this, UserChartActivity.class);
			request.putExtra(UserSettings.KEY, userSettings);
			startActivityForResult(request, REQUEST_CODE_CHART);
			break;
		}
	}

}