package br.com.doublelogic.swcadsf.ui;

import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import br.com.doublelogic.swcadsf.R;
import br.com.doublelogic.swcadsf.bean.UserChartData;
import br.com.doublelogic.swcadsf.bean.UserSettings;
import br.com.doublelogic.swcadsf.chart.LineChart;
import br.com.doublelogic.swcadsf.common.constants.ChartDataType;
import br.com.doublelogic.swcadsf.common.constants.ChartTimeType;
import br.com.doublelogic.swcadsf.io.DataHandler;

public class UserChartActivity extends Activity {

	private UserSettings userSettings;

	private DataHandler dataHandler;

	private RadioButton radioWeight;
	private RadioButton radioBmi;
	private RadioButton radioFat;
	
	private RadioButton radioAllData;
	private RadioButton radioLastMonth;
	private RadioButton radioLast3Months;

	private static final int REQUEST_CODE_CHART = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_chart);

		loadUIReferences();

		dataHandler = new DataHandler(this);

		if (getIntent().hasExtra(UserSettings.KEY)) {
			userSettings = (UserSettings) getIntent().getExtras().getSerializable(UserSettings.KEY);
		} else {
			finish();
		}
	}

	private void loadUIReferences() {
		radioWeight = (RadioButton) findViewById(R.id.radioButtonWeight);
		radioBmi = (RadioButton) findViewById(R.id.radioButtonBmi);   
		radioFat = (RadioButton) findViewById(R.id.radioButtonFat);   
		
		radioAllData = (RadioButton) findViewById(R.id.radioAllData);
		radioLastMonth = (RadioButton) findViewById(R.id.radioLastMonth);
		radioLast3Months = (RadioButton) findViewById(R.id.radioLast3Months);
	}

	public void confirmClickHandler(View view) {
		switch (view.getId()) {
		case R.id.buttonConfirm:
			UserChartData chartData = new UserChartData();

			Calendar cal = Calendar.getInstance();
			chartData.setFinalDateInMillis(cal.getTimeInMillis());

			if (radioBmi.isChecked()) {
				chartData.setDataType(ChartDataType.BMI);
			} else if (radioFat.isChecked()) {
				chartData.setDataType(ChartDataType.FAT);
			} else {
				chartData.setDataType(ChartDataType.WEIGHT);
			}
			
			if (radioLastMonth.isChecked()) {
				chartData.setTimeType(ChartTimeType.LAST_MONTH);
				cal.add(Calendar.MONTH, -1);
			} else if (radioLast3Months.isChecked()) {
				chartData.setTimeType(ChartTimeType.LAST_3_MONTHS);
				cal.add(Calendar.MONTH, -3);
			} else {
				chartData.setTimeType(ChartTimeType.ALL_DATA);
				cal.set(Calendar.YEAR, 1970);
			}
			chartData.setInitialDateInMillis(cal.getTimeInMillis());

			LineChart lineChart = new LineChart(chartData, dataHandler);
			Intent chartIntent = lineChart.getChartIntent(this);
			if (chartIntent != null) {
				startActivityForResult(chartIntent, REQUEST_CODE_CHART);
			} else {
				finish();
			}
			break;
		}
	}
}