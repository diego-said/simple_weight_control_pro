package br.com.doublelogic.swcadsf.common.constants;

public enum Gender {

	MALE(0), FEMALE(1), UNKNOWN(-1);

	private final int genderCode;

	private Gender(int genderCode) {
		this.genderCode = genderCode;
	}

	@Override
	public String toString() {
		return String.valueOf(genderCode);
	}

	public static Gender getGender(int genderCode) {
		for (Gender g : values()) {
			if (g.genderCode == genderCode) {
				return g;
			}
		}
		return UNKNOWN;
	}

}