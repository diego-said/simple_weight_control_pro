package br.com.doublelogic.swcadsf.bean;

import java.io.Serializable;

import br.com.doublelogic.swcadsf.common.constants.BackupDataType;

public class UserBackupData implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String KEY = "backupData";

	private final BackupDataType type;

	public UserBackupData(BackupDataType type) {
		this.type = type;
	}

	public BackupDataType getType() {
		return type;
	}

}
