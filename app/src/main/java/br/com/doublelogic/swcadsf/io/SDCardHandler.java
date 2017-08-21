package br.com.doublelogic.swcadsf.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.TreeMap;

import android.app.Activity;
import android.os.Environment;
import br.com.doublelogic.swcadsf.bean.UserSettings;
import br.com.doublelogic.swcadsf.bean.UserWeightData;
import br.com.doublelogic.swcadsf.common.constants.Gender;
import br.com.doublelogic.swcadsf.common.constants.IOConstants;
import br.com.doublelogic.swcadsf.common.constants.LogTags;

public class SDCardHandler extends AbstractIOHandler {

	public SDCardHandler(Activity activity) {
		super(activity);
	}

	@Override
	protected String getLogTag() {
		return String.valueOf(LogTags.SDCARD_HANDLER);
	}

	public boolean userSettingsFileExists() {
		for (File file : Environment.getExternalStorageDirectory().listFiles()) {
			if (String.valueOf(IOConstants.USER_SETTINGS_EXP_IN_FILENAME).equalsIgnoreCase(file.getName())) {
				return true;
			}
		}
		return false;
	}

	public boolean userWeightDataFileExists() {
		for (File file : Environment.getExternalStorageDirectory().listFiles()) {
			if (String.valueOf(IOConstants.WEIGHT_DATA_EXP_IN_FILENAME).equalsIgnoreCase(file.getName())) {
				return true;
			}
		}
		return false;
	}

	private File getUserSettingsFile() {
		return new File(Environment.getExternalStorageDirectory().getPath() + File.separator
				+ String.valueOf(IOConstants.USER_SETTINGS_EXP_IN_FILENAME));
	}

	private File getUserWeightDataFile() {
		return new File(Environment.getExternalStorageDirectory().getPath() + File.separator
				+ String.valueOf(IOConstants.WEIGHT_DATA_EXP_IN_FILENAME));
	}

	private FileOutputStream openUserSettingsFile() throws FileNotFoundException {
		return new FileOutputStream(getUserSettingsFile());
	}

	private FileOutputStream openUserWeightDataFile() throws FileNotFoundException {
		return new FileOutputStream(getUserWeightDataFile());
	}

	public UserSettings loadUserSettings() {
		UserSettings userSettings = null;
		FileInputStream userSettingsIn = null;
		BufferedReader in = null;
		try {
			if (userSettingsFileExists()) {
				userSettingsIn = new FileInputStream(getUserSettingsFile());
				in = new BufferedReader(new InputStreamReader(userSettingsIn));

				String dataIn = in.readLine();
				String[] data = dataIn.split(";");

				userSettings = new UserSettings();
				userSettings.setHeight(Integer.parseInt(data[0]));
				userSettings.setGender(Gender.getGender(Integer.parseInt(data[1])));
			}
		} catch (FileNotFoundException e) {
			logWarning("loadUserSettings");
			logWarning("user settings file not found", e);
		} catch (IOException e) {
			logError("loadUserSettings");
			logError("failed to read the settings file", e);
		} finally {
			closeReader(in);
			closeFileInput(userSettingsIn);
		}
		return userSettings;
	}

	public TreeMap<Long, UserWeightData> loadUserWeightData() {
		TreeMap<Long, UserWeightData> userWeightMap = new TreeMap<Long, UserWeightData>();

		FileInputStream userWeightDataIn = null;
		BufferedReader in = null;

		try {
			if (userWeightDataFileExists()) {
				userWeightDataIn = new FileInputStream(getUserWeightDataFile());
				in = new BufferedReader(new InputStreamReader(userWeightDataIn));

				String dataIn = null;
				while ((dataIn = in.readLine()) != null) {
					String[] data = dataIn.split(";");
					UserWeightData weightData = new UserWeightData();
					weightData.setTimeInMillis(Long.parseLong(data[0]));
					weightData.setWeight(Double.parseDouble(data[1]));
					weightData.setNeck(Integer.parseInt(data[2]));
					weightData.setWaist(Integer.parseInt(data[3]));
					weightData.setHip(Integer.parseInt(data[4]));
					weightData.setBodyFat(Double.parseDouble(data[5]));
					weightData.setBmi(Double.parseDouble(data[6]));

					userWeightMap.put(weightData.getTimeInMillis(), weightData);
				}
			}
		} catch (FileNotFoundException e) {
			logWarning("loadUserWeightData");
			logWarning("user weight datas file not found", e);
		} catch (IOException e) {
			logError("loadUserWeightData");
			logError("failed to read the weight data file", e);
		} finally {
			closeReader(in);
			closeFileInput(userWeightDataIn);
		}
		return userWeightMap;
	}

	public boolean saveUserSettings(UserSettings userSettings) {
		FileOutputStream userSettingsOut = null;
		BufferedWriter out = null;
		try {
			userSettingsOut = openUserSettingsFile();
			out = new BufferedWriter(new PrintWriter(userSettingsOut));

			StringBuilder data = new StringBuilder();
			data.append(String.valueOf(userSettings.getHeight()));
			data.append(";");
			data.append(String.valueOf(userSettings.getGender()));

			out.write(data.toString());
			return true;
		} catch (FileNotFoundException e) {
			logError("saveUserSettings");
			logError("user settings file not found", e);
		} catch (IOException e) {
			logError("saveUserSettings");
			logError("failed to save the settings file", e);
		} finally {
			closeWriter(out);
			closeFileOutput(userSettingsOut);
		}
		return false;
	}

	public boolean saveUserWeightData(TreeMap<Long, UserWeightData> weightData) {
		FileOutputStream userWeightDataOut = null;
		BufferedWriter out = null;
		try {
			userWeightDataOut = openUserWeightDataFile();
			out = new BufferedWriter(new PrintWriter(userWeightDataOut));

			Iterator<UserWeightData> itWeightData = weightData.values().iterator();
			while (itWeightData.hasNext()) {
				UserWeightData weightDataOut = itWeightData.next();

				StringBuilder data = new StringBuilder();
				data.append(String.valueOf(weightDataOut.getTimeInMillis()));
				data.append(";");
				data.append(String.valueOf(weightDataOut.getWeight()));
				data.append(";");
				data.append(String.valueOf(weightDataOut.getNeck()));
				data.append(";");
				data.append(String.valueOf(weightDataOut.getWaist()));
				data.append(";");
				data.append(String.valueOf(weightDataOut.getHip()));
				data.append(";");
				data.append(String.valueOf(weightDataOut.getBodyFat()));
				data.append(";");
				data.append(String.valueOf(weightDataOut.getBmi()));
				data.append("\n");

				out.write(data.toString());
			}
			return true;
		} catch (FileNotFoundException e) {
			logError("saveUserSettings");
			logError("user setting's file not found", e);
		} catch (IOException e) {
			logError("saveUserSettings");
			logError("failed to save the settings file", e);
		} finally {
			closeWriter(out);
			closeFileOutput(userWeightDataOut);
		}
		return false;
	}

}