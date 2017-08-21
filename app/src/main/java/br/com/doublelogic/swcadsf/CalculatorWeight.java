package br.com.doublelogic.swcadsf;

import br.com.doublelogic.swcadsf.bean.UserSettings;
import br.com.doublelogic.swcadsf.bean.UserWeightData;
import br.com.doublelogic.swcadsf.common.constants.Gender;

public class CalculatorWeight {

	public static void bmi(UserSettings userSettings, UserWeightData weightData) {
		double bmi = weightData.getWeight() / (Math.pow(userSettings.getHeight() / 100.0d, 2));
		weightData.setBmi(bmi);
	}

	public static void bodyFat(UserSettings userSettings, UserWeightData weightData) {
		if (userSettings.getGender() == Gender.MALE) {
			double aux2 = weightData.getWaist() - weightData.getNeck();
			double aux = Math.log(aux2) / Math.log(10);
			aux = 0.19077 * aux;
			aux2 = Math.log(userSettings.getHeight()) / Math.log(10);
			aux2 = 0.15456 * aux2;
			aux = 1.0324 - aux;
			aux = aux + aux2;
			aux = 495 / aux;
			aux = aux - 450;

			weightData.setBodyFat(aux);
		} else if (userSettings.getGender() == Gender.FEMALE) {
			double aux2 = (weightData.getHip() + weightData.getWaist()) - weightData.getNeck();
			double aux = Math.log(aux2) / Math.log(10);
			aux = 0.35004 * aux;
			aux2 = Math.log(userSettings.getHeight()) / Math.log(10);
			aux2 = 0.22100 * aux2;
			aux = 1.29579 - aux;
			aux = aux + aux2;
			aux = 495 / aux;
			aux = aux - 450;

			weightData.setBodyFat(aux);
		}
	}
}
