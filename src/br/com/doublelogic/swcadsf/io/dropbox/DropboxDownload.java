package br.com.doublelogic.swcadsf.io.dropbox;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.widget.Toast;
import br.com.doublelogic.swcadsf.R;
import br.com.doublelogic.swcadsf.common.constants.IOConstants;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.ProgressListener;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxIOException;
import com.dropbox.client2.exception.DropboxParseException;
import com.dropbox.client2.exception.DropboxPartialFileException;
import com.dropbox.client2.exception.DropboxServerException;
import com.dropbox.client2.exception.DropboxUnlinkedException;

public class DropboxDownload extends AsyncTask<Void, Long, Boolean> {

	private final Context mContext;
	private final ProgressDialog mDialog;
	private final DropboxAPI<?> mApi;
	private final String mPath;

	private FileOutputStream mFos;

	private boolean mCanceled;
	private Long mFileLen;
	private String mErrorMsg;

	public DropboxDownload(Context context, DropboxAPI<?> api) {
		// We set the context this way so we don't accidentally leak activities
		mContext = context.getApplicationContext();

		mApi = api;
		mPath = "/";

		mDialog = new ProgressDialog(context);
		mDialog.setMessage(mContext.getString(R.string.downloading));
		mDialog.setButton(mContext.getString(R.string.cancel), new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				mCanceled = true;
				mErrorMsg = mContext.getString(R.string.error_down_canceled);

				// This will cancel the getThumbnail operation by closing
				// its stream
				if (mFos != null) {
					try {
						mFos.close();
					} catch (IOException e) {
					}
				}
			}
		});

		mDialog.show();
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		try {
			if (mCanceled) {
				return false;
			}

			// Get the metadata for a directory
			Entry dirent = mApi.metadata(mPath, 1000, null, true, null);

			if (!dirent.isDir || (dirent.contents == null)) {
				// It's not a directory, or there's nothing in it
				mErrorMsg = mContext.getString(R.string.error_not_file);
				return false;
			}

			for (Entry entry : dirent.contents) {
				if (String.valueOf(IOConstants.USER_SETTINGS_FILENAME).equals(entry.fileName())) {
					String cachePath = mContext.getCacheDir().getAbsolutePath() + "/" + String.valueOf(IOConstants.USER_SETTINGS_EXP_IN_FILENAME);
					try {
						mFos = new FileOutputStream(cachePath);
					} catch (FileNotFoundException e) {
						mErrorMsg = mContext.getString(R.string.error_cache);
						return false;
					}
					mFileLen = entry.bytes;
					mApi.getFile(entry.path, null, mFos, new ProgressListener() {
						@Override
						public void onProgress(long bytes, long total) {
							publishProgress(bytes);
						}
					});
					mFos.close();
				} else if (String.valueOf(IOConstants.WEIGHT_DATA_FILENAME).equals(entry.fileName())) {
					String cachePath = mContext.getCacheDir().getAbsolutePath() + "/" + String.valueOf(IOConstants.WEIGHT_DATA_EXP_IN_FILENAME);
					try {
						mFos = new FileOutputStream(cachePath);
					} catch (FileNotFoundException e) {
						mErrorMsg = mContext.getString(R.string.error_cache);
						return false;
					}
					mFileLen = entry.bytes;
					mApi.getFile(entry.path, null, mFos, new ProgressListener() {
						@Override
						public void onProgress(long bytes, long total) {
							publishProgress(bytes);
						}
					});
					mFos.close();
				}
			}
			return true;
		} catch (DropboxUnlinkedException e) {
			// The AuthSession wasn't properly authenticated or user unlinked.
			mErrorMsg = mContext.getString(R.string.error_authenticated);
		} catch (DropboxPartialFileException e) {
			// We canceled the operation
			mErrorMsg = mContext.getString(R.string.error_down_canceled);
		} catch (DropboxServerException e) {
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
		} catch (Exception e) {
			mErrorMsg = mContext.getString(R.string.error_unknown);
		} finally {
			try {
				mFos.close();
			} catch (Exception e) {
			}
		}
		return false;
	}

	@Override
	protected void onProgressUpdate(Long... progress) {
	}

	@Override
	protected void onPostExecute(Boolean result) {
		mDialog.dismiss();
		if (!result) {
			// Couldn't download it, so show an error
			showToast(mErrorMsg);
		}
	}

	private void showToast(String msg) {
		Toast error = Toast.makeText(mContext, msg, Toast.LENGTH_LONG);
		error.show();
	}

}