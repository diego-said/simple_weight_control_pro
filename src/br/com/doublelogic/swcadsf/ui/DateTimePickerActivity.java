package br.com.doublelogic.swcadsf.ui;

import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import br.com.doublelogic.swcadsf.R;
import br.com.doublelogic.swcadsf.bean.UserWeightData;

public class DateTimePickerActivity extends Activity {

	private DatePicker datePicker;
	private TimePicker timePicker;

	public static final String KEY = "dateTime";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.datetime_picker);

		loadUIReferences();

		if (getIntent().hasExtra(UserWeightData.KEY)) {
			UserWeightData weightData = (UserWeightData) getIntent().getExtras().getSerializable(UserWeightData.KEY);
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(weightData.getTimeInMillis());

			datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), null);
			timePicker.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
			timePicker.setCurrentMinute(cal.get(Calendar.MINUTE));
		}
	}

	public void confirmClickHandler(View view) {
		switch (view.getId()) {
		case R.id.buttonConfirm:
			finish();
			break;
		}
	}

	@Override
	public void finish() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
		cal.set(Calendar.MONTH, datePicker.getMonth());
		cal.set(Calendar.YEAR, datePicker.getYear());
		cal.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
		cal.set(Calendar.MINUTE, timePicker.getCurrentMinute());

		Intent data = new Intent();
		data.putExtra(KEY, cal.getTimeInMillis());

		setResult(RESULT_OK, data);
		super.finish();
	}

	private void loadUIReferences() {
		datePicker = (DatePicker) findViewById(R.id.datePicker);
		timePicker = (TimePicker) findViewById(R.id.timePicker);
	}

}