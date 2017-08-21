package br.com.doublelogic.swcadsf.common;

import br.com.doublelogic.swcadsf.R;
import br.com.doublelogic.swcadsf.common.constants.LogTags;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class SendMail {

	private boolean isConnected = false;
	private Context context;

	public SendMail(Context context){
		this.context = context;
	}

	public boolean send(String subject, String msgBody){
		try{
			try {
				//VERIFICA A CONEXAO NO MOMENTO
				/*ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo networkInfo = conMan.getActiveNetworkInfo();
				isConnected = networkInfo.isConnected();*/
			} catch (Throwable ta) {}

			StringBuilder msg = new StringBuilder();

			if (msgBody != null && !"".equals(msgBody)) {
				msg.append(msgBody);
				msg.append("\n\n");
			}

			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
			
			msg.append("\n\n" + context.getString(R.string.app_name) + " " + info.versionName + "(" + info.versionCode + ")");
			msg.append("\nBrand: "+ android.os.Build.BRAND);
			msg.append("\nModel: " + android.os.Build.MODEL);
			msg.append("\nDisplay: " + android.os.Build.DISPLAY);
			msg.append("\nSoftware: " + android.os.Build.DISPLAY);
			msg.append("\nAndroid Version: " + android.os.Build.VERSION.RELEASE);
			msg.append("\n\n");

			// CRIA UM INTENT DE EMAIL
			final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
			emailIntent.setType("plain/text");
			emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { "diego@doublelogic.com.br" });
			emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "[" + context.getString(R.string.app_name) + "] " + subject);
			emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, msg.toString());

			// INICIA A ACTIVITY DO EMAIL - O USUARIO TERA QUE ESCOLHER QUAL PROGRAMA USAR
			context.startActivity(Intent.createChooser(emailIntent, "Send email (" + subject + ")"));

			return true;
		} catch (Throwable t) {
			Log.e(String.valueOf(LogTags.SEND_MAIL), "Erro mandando email", t);
			return false;
		}

	}
	
}
