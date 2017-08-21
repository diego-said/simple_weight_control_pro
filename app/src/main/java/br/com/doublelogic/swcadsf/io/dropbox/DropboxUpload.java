package br.com.doublelogic.swcadsf.io.dropbox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.widget.Toast;
import br.com.doublelogic.swcadsf.R;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.UploadRequest;
import com.dropbox.client2.ProgressListener;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxFileSizeException;
import com.dropbox.client2.exception.DropboxIOException;
import com.dropbox.client2.exception.DropboxParseException;
import com.dropbox.client2.exception.DropboxPartialFileException;
import com.dropbox.client2.exception.DropboxServerException;
import com.dropbox.client2.exception.DropboxUnlinkedException;

public class DropboxUpload extends AsyncTask<Void, Long, Boolean> {

	private final DropboxAPI<?> mApi;

	private final File[] mFile;

	private long mFileLen;
	private UploadRequest mRequest;
	private final Context mContext;
	private final ProgressDialog mDialog;

	private String mErrorMsg;

	private long transferedBytes;

	public DropboxUpload(Context context, DropboxAPI<?> api, File... file) {
		mContext = context.getApplicationContext();

		mFile = file;
		for (File f : mFile) {
			mFileLen += f.length();
		}

		mApi = api;

		mDialog = new ProgressDialog(context);
		mDialog.setMax(100);
		mDialog.setMessage(mContext.getString(R.string.uploading));
		mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mDialog.setProgress(0);
		mDialog.setButton(mContext.getString(R.string.cancel), new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// This will cancel the putFile operation
				mRequest.abort();
			}
		});
		mDialog.show();
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		try {
			FileInputStream fis = null;
			for (File f : mFile) {
				fis = new FileInputStream(f);
				String path = f.getName();
				mRequest = mApi.putFileOverwriteRequest(path, fis, f.length(), new ProgressListener() {
					@Override
					public long progressInterval() {
						return 500;
					}

					@Override
					public void onProgress(long bytes, long total) {
						transferedBytes += bytes;
						publishProgress(transferedBytes);
					}
				});
				if (mRequest != null) {
					mRequest.upload();
				}
			}
			return true;
		} catch (DropboxUnlinkedException e) {
			// This session wasn't authenticated properly or user unlinked
			mErrorMsg = mContext.getString(R.string.error_authenticated);
		} catch (DropboxFileSizeException e) {
			// File size too big to upload via the API
			mErrorMsg = mContext.getString(R.string.error_too_big);
		} catch (DropboxPartialFileException e) {
			// We canceled the operation
			mErrorMsg = mContext.getString(R.string.error_up_canceled);
		} catch (DropboxServerException e) {
			// This gets the Dropbox error, translated into the user's language
			mErrorMsg = e.body.userError;
			if (mErrorMsg == null) {
				mErrorMsg = e.body.error;
			}
		} catch (DropboxIOException e) {
			// Happens all the time, probably want to retry automatically.
			mErrorMsg = mContext.getString(R.string.error_network);
		} catch (DropboxParseException e) {
			// Probably due to Dropbox server restarting, should retry
			mErrorMsg = mContext.getString(R.string.error_dropbox);
		} catch (DropboxException e) {
			// Unknown error
			mErrorMsg = mContext.getString(R.string.error_unknown);
		} catch (FileNotFoundException e) {
		}
		return false;
	}

	@Override
	protected void onProgressUpdate(Long... progress) {
		int percent = (int) (((100.0 * (double) progress[0]) / mFileLen) + 0.5);
		mDialog.setProgress(percent);
	}

	@Override
	protected void onPostExecute(Boolean result) {
		mDialog.dismiss();
		if (result) {
			showToast(mContext.getString(R.string.export_success));
		} else {
			showToast(mErrorMsg);
		}
	}

	private void showToast(String msg) {
		Toast error = Toast.makeText(mContext, msg, Toast.LENGTH_LONG);
		error.show();
	}
}
