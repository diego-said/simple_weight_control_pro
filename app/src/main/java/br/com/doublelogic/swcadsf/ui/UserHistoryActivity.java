package br.com.doublelogic.swcadsf.ui;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import br.com.doublelogic.swcadsf.R;
import br.com.doublelogic.swcadsf.bean.UserSettings;
import br.com.doublelogic.swcadsf.bean.UserWeightData;
import br.com.doublelogic.swcadsf.io.DataHandler;

public class UserHistoryActivity extends Activity {

	private UserSettings userSettings;
	private TreeMap<Long, UserWeightData> weightDataMap;

	private DataHandler dataHandler;

	private LinearLayout linearLayoutMain;

	private SimpleDateFormat sdf;
	private NumberFormat nf;

	private float scale;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_history);

		loadUIReferences();

		dataHandler = new DataHandler(this);

		sdf = new SimpleDateFormat("dd/MM HH:mm");
		nf = new DecimalFormat("###.##");

		scale = getResources().getDisplayMetrics().density;

		if (getIntent().hasExtra(UserSettings.KEY)) {
			userSettings = (UserSettings) getIntent().getExtras().getSerializable(UserSettings.KEY);
		} else {
			finish();
		}

		loadHistory();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			linearLayoutMain.removeAllViews();
			loadHistory();
		}
	}

	private void loadUIReferences() {
		linearLayoutMain = (LinearLayout) findViewById(R.id.linearLayoutMain);
	}

	private void loadHistory() {
		weightDataMap = dataHandler.loadUserWeightData();

		boolean colorFlag = false;
		int layoutIndex = 0;
		UserWeightData weightDataPrev = null;
		for (UserWeightData weightData : weightDataMap.values()) {
			LinearLayout layout = new LinearLayout(this);
			layout.setOrientation(LinearLayout.HORIZONTAL);
			layout.setId(++layoutIndex);
			layout.setVisibility(LinearLayout.VISIBLE);

			if (colorFlag = !colorFlag) {
				layout.setBackgroundColor(Color.DKGRAY);
			}

			addTextViewData(layout, 90, weightData.getTimeInMillis(), sdf.format(new Date(weightData.getTimeInMillis())));
			addTextView(layout, 60, nf.format(weightData.getWeight()));
			if (weightDataPrev != null) {
				addTextView(layout, 70, nf.format(weightData.getWeight() - weightDataPrev.getWeight()));
			} else {
				addTextView(layout, 70, getString(R.string.no_content));
			}

			if (weightData.getBodyFat() > 0) {
				addTextView(layout, 50, nf.format(weightData.getBodyFat()));
			} else {
				addTextView(layout, 50, getString(R.string.no_content));
			}

			if (weightData.getBmi() > 0) {
				addTextView(layout, 50, nf.format(weightData.getBmi()));
			} else {
				addTextView(layout, 50, getString(R.string.no_content));
			}

			linearLayoutMain.addView(layout, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

			weightDataPrev = weightData;
		}
	}

	private void addTextViewData(LinearLayout layout, int width, long timeInMillis, String value) {
		SpannableString contentUnderline = new SpannableString("[" + value + "]");
		contentUnderline.setSpan(new UnderlineSpan(), 0, contentUnderline.length(), 0);

		TextView textView = new TextView(this);
		textView.setWidth((int) ((width * scale) + 0.5f));
		textView.setHeight((int) ((25 * scale) + 0.5f));
		textView.setText(contentUnderline);
		textView.setClickable(true);
		textView.setOnClickListener(new TextViewDataOnClickListener(this, timeInMillis));
		layout.addView(textView);
	}

	private void addTextView(LinearLayout layout, int width, String value) {
		TextView textView = new TextView(this);
		textView.setWidth((int) ((width * scale) + 0.5f));
		textView.setHeight((int) ((25 * scale) + 0.5f));
		textView.setText(value);

		layout.addView(textView);
	}

	private class TextViewDataOnClickListener implements OnClickListener {

		private final Context context;
		private final long timeInMillis;

		public TextViewDataOnClickListener(Context context, long timeInMillis) {
			this.context = context;
			this.timeInMillis = timeInMillis;
		}

		public void onClick(View v) {
			UserWeightData weightData = weightDataMap.get(timeInMillis);

			if (weightData != null) {
				Intent request = new Intent(context, NewDataActivity.class);
				request.putExtra(UserSettings.KEY, userSettings);
				request.putExtra(UserWeightData.KEY, weightData);
				startActivityForResult(request, 0);
			}
		}
	}
}