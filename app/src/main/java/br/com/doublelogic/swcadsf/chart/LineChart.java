package br.com.doublelogic.swcadsf.chart;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import br.com.doublelogic.swcadsf.R;
import br.com.doublelogic.swcadsf.bean.UserChartData;
import br.com.doublelogic.swcadsf.bean.UserWeightData;
import br.com.doublelogic.swcadsf.io.DataHandler;

public class LineChart {

	private final UserChartData chartData;
	private final DataHandler dataHandler;

	private final SimpleDateFormat sdf;

	public LineChart(UserChartData chartData, DataHandler dataHandler) {
		this.chartData = chartData;
		this.dataHandler = dataHandler;
		sdf = new SimpleDateFormat("dd/MM");
	}

	public Intent getChartIntent(Context context) {
		UserWeightData[] chartWeightData = loadChartData();
		if (chartWeightData.length > 0) {
			XYMultipleSeriesDataset seriesDataset = new XYMultipleSeriesDataset();
			XYMultipleSeriesRenderer multiSeriesRenderer = new XYMultipleSeriesRenderer();
			multiSeriesRenderer.setBackgroundColor(Color.DKGRAY);
			multiSeriesRenderer.setShowGrid(true);
			multiSeriesRenderer.setShowLegend(true);
			multiSeriesRenderer.setXLabels(0);
			multiSeriesRenderer.setPointSize(2);

			TimeSeries dataSerie = new TimeSeries(context.getString(R.string.weight));

			for (UserWeightData weightData : chartWeightData) {
				if (dataSerie.getItemCount() == 0) {
					multiSeriesRenderer.setXAxisMin(weightData.getTimeInMillis());
				}
				switch (chartData.getDataType()) {
				case WEIGHT:
					dataSerie.add(weightData.getTimeInMillis(), weightData.getWeight());
					break;
				case FAT:
					dataSerie.add(weightData.getTimeInMillis(), weightData.getBodyFat());
					break;
				case BMI:
					dataSerie.add(weightData.getTimeInMillis(), weightData.getBmi());
					break;
				}
				
				multiSeriesRenderer.addXTextLabel(weightData.getTimeInMillis(), sdf.format(new Date(weightData.getTimeInMillis())));
				multiSeriesRenderer.setXAxisMax(weightData.getTimeInMillis());
			}

			XYSeriesRenderer serieWeightRenderer = new XYSeriesRenderer();
			serieWeightRenderer.setDisplayChartValues(true);
			serieWeightRenderer.setColor(Color.GREEN);
			serieWeightRenderer.setPointStyle(PointStyle.SQUARE);

			seriesDataset.addSeries(dataSerie);
			multiSeriesRenderer.addSeriesRenderer(serieWeightRenderer);

			return ChartFactory.getTimeChartIntent(context, seriesDataset, multiSeriesRenderer, context.getString(R.string.app_name));
		} else {
			return null;
		}

	}

	private UserWeightData[] loadChartData() {
		List<UserWeightData> chartWeightData = new ArrayList<UserWeightData>();
		TreeMap<Long, UserWeightData> weightDataMap = dataHandler.loadUserWeightData();
		for (UserWeightData weightData : weightDataMap.values()) {
			if ((weightData.getTimeInMillis() >= chartData.getInitialDateInMillis())
					&& (weightData.getTimeInMillis() <= chartData.getFinalDateInMillis())) {
				chartWeightData.add(weightData);
			}
		}
		return chartWeightData.toArray(new UserWeightData[chartWeightData.size()]);
	}

}
