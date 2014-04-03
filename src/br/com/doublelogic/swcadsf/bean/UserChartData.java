package br.com.doublelogic.swcadsf.bean;

import java.io.Serializable;

import br.com.doublelogic.swcadsf.common.constants.ChartDataType;
import br.com.doublelogic.swcadsf.common.constants.ChartTimeType;

public class UserChartData implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String KEY = "chartData";

	private ChartTimeType timeType;
	private ChartDataType dataType;

	private long initialDateInMillis;
	private long finalDateInMillis;

	public ChartTimeType getTimeType() {
		return timeType;
	}

	public void setTimeType(ChartTimeType timeType) {
		this.timeType = timeType;
	}

	public ChartDataType getDataType() {
		return dataType;
	}

	public void setDataType(ChartDataType dataType) {
		this.dataType = dataType;
	}

	public long getInitialDateInMillis() {
		return initialDateInMillis;
	}

	public void setInitialDateInMillis(long initialDateInMillis) {
		this.initialDateInMillis = initialDateInMillis;
	}

	public long getFinalDateInMillis() {
		return finalDateInMillis;
	}

	public void setFinalDateInMillis(long finalDateInMillis) {
		this.finalDateInMillis = finalDateInMillis;
	}

}