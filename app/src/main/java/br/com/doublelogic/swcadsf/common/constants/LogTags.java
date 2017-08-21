package br.com.doublelogic.swcadsf.common.constants;

public enum LogTags {

	DATA_HANDLER("[SimpleWeightControl] - DataHandler"), 
	SDCARD_HANDLER("[SimpleWeightControl] - SDCardHandler"), 
	DROPBOX_HANDLER("[SimpleWeightControl] - DropboxHandler"), 
	SEND_MAIL("[SimpleWeightControl] - SendMail");

	private final String tag;

	private LogTags(String tag) {
		this.tag = tag;
	}

	@Override
	public String toString() {
		return tag;
	}

}
