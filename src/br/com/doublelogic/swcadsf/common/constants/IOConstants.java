package br.com.doublelogic.swcadsf.common.constants;

public enum IOConstants {

	USER_SETTINGS_FILENAME("user_settings"), 
	WEIGHT_DATA_FILENAME("weight_data"), 
	USER_SETTINGS_EXP_IN_FILENAME("swc_user_settings"), 
	WEIGHT_DATA_EXP_IN_FILENAME("swc_weight_data");

	private final String fileName;

	private IOConstants(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public String toString() {
		return fileName;
	}

}