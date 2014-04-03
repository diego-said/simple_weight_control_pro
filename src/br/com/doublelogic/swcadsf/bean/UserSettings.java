package br.com.doublelogic.swcadsf.bean;

import java.io.Serializable;

import br.com.doublelogic.swcadsf.common.constants.Gender;

public class UserSettings implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String KEY = "userSettings";

	public static final int DEFAULT_HEIGHT_VALUE = 170;
	public static final Gender DEFAULT_GENDER = Gender.MALE;

	private int height;
	private Gender gender;

	public UserSettings() {
		height = DEFAULT_HEIGHT_VALUE;
		gender = DEFAULT_GENDER;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

}