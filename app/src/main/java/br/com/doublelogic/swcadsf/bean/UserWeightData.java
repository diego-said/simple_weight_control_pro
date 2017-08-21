package br.com.doublelogic.swcadsf.bean;

import java.io.Serializable;

public class UserWeightData implements Serializable, Comparable<UserWeightData> {

	private static final long serialVersionUID = 1L;

	public static final String KEY = "weightData";

	private long timeInMillis;

	private double weight;

	private int neck;
	private int waist;
	private int hip;

	private double bodyFat;
	private double bmi;

	public long getTimeInMillis() {
		return timeInMillis;
	}

	public void setTimeInMillis(long timeInMillis) {
		this.timeInMillis = timeInMillis;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public int getNeck() {
		return neck;
	}

	public void setNeck(int neck) {
		this.neck = neck;
	}

	public int getWaist() {
		return waist;
	}

	public void setWaist(int waist) {
		this.waist = waist;
	}

	public int getHip() {
		return hip;
	}

	public void setHip(int hip) {
		this.hip = hip;
	}

	public double getBodyFat() {
		return bodyFat;
	}

	public void setBodyFat(double bodyFat) {
		this.bodyFat = bodyFat;
	}

	public double getBmi() {
		return bmi;
	}

	public void setBmi(double bmi) {
		this.bmi = bmi;
	}

	public int compareTo(UserWeightData another) {
		return new Long(timeInMillis).compareTo(new Long(another.getTimeInMillis()));
	}

}