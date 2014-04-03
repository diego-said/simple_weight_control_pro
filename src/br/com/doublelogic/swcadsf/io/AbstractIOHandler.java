package br.com.doublelogic.swcadsf.io;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import android.app.Activity;
import android.util.Log;

public abstract class AbstractIOHandler {

	protected final Activity activity;

	public AbstractIOHandler(Activity activity) {
		this.activity = activity;
	}

	protected abstract String getLogTag();

	public void closeReader(Reader... readers) {
		for (Reader reader : readers) {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					logError("Error on close reader", e);
				}
			}
		}
	}

	public void closeWriter(Writer... writers) {
		for (Writer writer : writers) {
			if (writer != null) {
				try {
					writer.flush();
					writer.close();
				} catch (IOException e) {
					logError("Error on close writer", e);
				}
			}
		}
	}

	public void closeFileInput(FileInputStream... files) {
		for (FileInputStream file : files) {
			if (file != null) {
				try {
					file.close();
				} catch (IOException e) {
					logError("Error on close input file", e);
				}
			}
		}
	}

	public void closeFileOutput(FileOutputStream... files) {
		for (FileOutputStream file : files) {
			if (file != null) {
				try {
					file.flush();
					file.close();
				} catch (IOException e) {
					logError("Error on close output file", e);
				}
			}
		}
	}

	protected void logError(String message) {
		Log.e(getLogTag(), message);
	}

	protected void logError(String message, Throwable tr) {
		Log.e(getLogTag(), message, tr);
	}

	protected void logWarning(String message) {
		Log.w(getLogTag(), message);
	}

	protected void logWarning(String message, Throwable tr) {
		Log.w(getLogTag(), message, tr);
	}

}