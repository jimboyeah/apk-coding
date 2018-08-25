package hakka.jimbowhy.demo;

import java.lang.Thread;
import java.text.*;
import java.io.*;
import android.os.*;
import android.widget.*;
import android.content.*;
import java.util.*;
import android.test.*;
import android.content.pm.*;
import android.Manifest.permission;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.util.*;
import java.lang.reflect.*;
import android.appwidget.*;

public class UncaughtAgent implements Thread.UncaughtExceptionHandler
{
	private static Context context;
	private static String logFile = "exception.log";
	
	public void UncaughtHandler(Activity ui)
	{
		context = ui;
		if( PackageManager.PERMISSION_DENIED ==
		context.checkSelfPermission(permission.WRITE_EXTERNAL_STORAGE)){
			//
		}
		
	}
	
	public void UncaughtHandler(Context ui, String logFile)
	{
		this.logFile = logFile;
		context = ui;
	}
	
	public static UncaughtAgent installHandler(Context ui)
	{
		UncaughtAgent h = new UncaughtAgent();
		context = ui;
		Thread.setDefaultUncaughtExceptionHandler(h);
		return h;
	}
	
	public static String save2file(String filename, String content)
		throws Exception
	{
		String sd=Environment.getExternalStorageState();
		if (sd.equals(Environment.MEDIA_MOUNTED))
		{
			File dir = Environment.getExternalStorageDirectory();
			File file = new File(dir, filename);
			FileOutputStream fos = new FileOutputStream(file, true);
			fos.write(content.getBytes());
			fos.close();
			return file.getPath();
		}
		else
		{
			FileOutputStream f = 
				context.openFileOutput(filename, Context.MODE_APPEND);
			f.write(content.getBytes());
			f.flush();
			f.close();
			return new File(filename).getPath();
		}
	}
	
	public static void i(String message)
	{
		log("INFO",message);
	}
	public static void d(String message)
	{
		log("DEBUG",message);
	}
	public static void w(String message)
	{
		log("WARNING!",message);
	}
	public static void e(String message)
	{
		log("ERROR!",message);
	}
	public static void log(String state, String mesaage)
	{
		try{
			String stamp = DateFormat.getDateTimeInstance().format(new Date());
			save2file(logFile,String.format("[%s] %s %s\n",state,stamp,mesaage));
		}catch(Exception e){
			Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG);
		}
	}
	public static void log(Throwable e)
	{
		try {
			save2file(logFile,dumpDebugInfo(e));
		}catch(Exception ex){
			Toast.makeText(context,ex.getMessage(),Toast.LENGTH_LONG);
		}
	}
	
	@Override
	public void uncaughtException(Thread t, final Throwable e)
	{
		String tip = "";
		try
		{
			String content = dumpDebugInfo(e);
			tip = save2file(logFile,content);
			tip = "App crashed, log save to" + tip;
		}
		catch (Exception ex)
		{
			tip = e.getMessage() + ex.getMessage();
		}
		final String ftip = tip;
		Log.d(context.getPackageName(),tip);
		//使用Toast和sleep来确保异常信息显示 
		new Thread() {  
			@Override  
			public void run()
			{  
				Looper.prepare();  
				Toast.makeText(context, 
							   "程序异常即将退出." + ftip, Toast.LENGTH_LONG).show();  
				Looper.loop();  
			}  
		}.start();  
		try
		{
			Thread.sleep(2222);
		}
		catch (Exception ex)
		{  }
		context = null;
		System.exit(1);
	}
	
	static public String dumpDebugInfo(Throwable e)
		throws Exception
	{
		PackageManager pm = context.getPackageManager();
		String df = DateFormat.getDateTimeInstance().format(new Date());
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		e.printStackTrace(new PrintStream(buf));

		String content = String.format(
			"\n\n%s uncaughtException:%s\n%s\n", df, e.getClass().getName(), buf.toString());
		
		String pkg = context.getPackageName();
		content += String.format(
			"Package Name:%s\n", pkg);
			
		PackageInfo pi = pm.getPackageInfo(pkg, 
					PackageManager.GET_ACTIVITIES);
		content += String.format(
			"App Version:%s_%s\n",pi.versionName, pi.versionCode);

		//content += String.format(
		//	"Base OS:%s\n", Build.BASE_OS);
		content += String.format(
			"OS Version:%s_%s\n",Build.VERSION.RELEASE,Build.VERSION.SDK_INT);
		content += String.format(
			"Vendor:%s \n", Build.MANUFACTURER);
		content += String.format(
			"Model:%s \n", Build.MODEL);
		content += String.format(
			"CPU ABIs:%s \n", Build.SUPPORTED_ABIS);
		content += String.format(
			"Host:%s \n", Build.HOST);
		content += String.format(
			"Bootloader:%s \n", Build.BOOTLOADER);
		
		return content;
	}

}
