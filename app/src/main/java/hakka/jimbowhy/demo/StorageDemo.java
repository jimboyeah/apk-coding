package hakka.jimbowhy.demo;

import android.app.*;
import android.os.*;
import android.view.*;
import android.view.animation.*;
import android.widget.*;
import java.io.*;
import android.content.*;
import android.util.*;
import java.util.*;
import java.text.*;
import android.*;
import android.graphics.drawable.shapes.*;
import android.graphics.drawable.*;

public class StorageDemo extends Activity
{
	private EditText box;

    /** 第一次被创建时调用活动. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
		UncaughtAgent.installHandler(this);

		// 设置layout.xml作为用户界面布局
        setContentView(R.layout.storage_demo);
		box = ((EditText)findViewById(R.id.storage_demoEditText));
		
	}
	
	public void printDirectory(){
		
		String s = "Environment:\n";
		String[] d = {
			Environment.DIRECTORY_ALARMS,
			Environment.DIRECTORY_DCIM,
			Environment.DIRECTORY_DOCUMENTS,
			Environment.DIRECTORY_DOWNLOADS,
			Environment.DIRECTORY_MOVIES,
			Environment.DIRECTORY_MUSIC,
			Environment.DIRECTORY_NOTIFICATIONS,
			Environment.DIRECTORY_PICTURES,
			Environment.DIRECTORY_PODCASTS,
			Environment.DIRECTORY_RINGTONES};
		for( int i=0; i<d.length; i++){
			s += s.format("%s: %s\n", d[i] ,
			Environment.getExternalStoragePublicDirectory(d[i]));
		}
		s += s.format("%s: %s\n", "getDataDirectory", Environment.getDataDirectory());
		s += s.format("%s: %s\n", "getDownloadCacheDirectory", Environment.getDownloadCacheDirectory());
		s += s.format("%s: %s\n", "getExternalStorageDirectory", Environment.getExternalStorageDirectory());
		s += s.format("%s: %s\n", "getRootDirectory", Environment.getRootDirectory());
		s += s.format("%s: %s\n", "File.separator", File.separator);
		s += "\n\nContext:\n";
		s += s.format("%s: %s\n","getFilesDir", getFilesDir().toString());
		s += s.format("%s: %s\n","getCacheDir", getCacheDir().toString());
		s += s.format("%s: %s\n","getExternalCacheDir",getExternalCacheDir());
		s += s.format("%s: %s\n","getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)", getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString());
		s += s.format("%s: %s\n","getExternalMediaDirs",getExternalMediaDirs().toString());
		//s += s.format("%s: %s\n","getDataDir",getDataDir().toString());
		s += s.format("%s: %s\n","getDatabasePath",getDatabasePath("b.sqlite").toString());
		s += s.format("%s: %s\n","getFileStreamPath",getFileStreamPath("ex.log").toString());
		s += s.format("%s: %s\n","getPackageCodePath",getPackageCodePath().toString());
		s += s.format("%s: %s\n","getPackageResourcePath",getPackageResourcePath().toString());
		s += s.format("%s: %s\n","getPackageName",getPackageName().toString());
		s += s.format("%s: %s\n","getObbDir",getObbDir().toString());
		s += "\n\nFileList\n";
		String[] fs = fileList();
		for( int i=0; i<fs.length; i++)
			s += s.format("%s: %s\n","#"+i,fs[i]);
		box.setText(s);
		
	}

    public void onExitButtonClick(View view) 
    {
		int i=1, j=0;
		i=i/j;
        animate(view);
		finish();
	}

    public void onDropButtonClick(View view) 
    {
		animate(view);
		printDirectory();
	}

	public void onClickWrite(View view)
	{
		box.setTextColor(0xddac00ff);
		
		try
		{
			String dt = DateFormat.getDateTimeInstance().format(new Date());
			FileOutputStream f = 
				openFileOutput("exception.log", Context.MODE_APPEND);
			f.write(dt.getBytes());
			f.write(box.getText().toString().getBytes());
			f.flush();
			f.close();
			saveFileToSDCard("exception.log", dt);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			box.append(ex.getMessage());
		}
		animate(view);
	}

	public void onClickRead(View view)
	{
		try
		{
			FileInputStream f = openFileInput("exception.log");
			String s = "";
			int length = 32;
			byte[] buf = new byte[length];
			while ((length = f.read(buf)) != -1)
			{
				s += new String(buf, 0, buf.length);
				buf = new byte[s.length() / 2];
			}
			f.close();
			box.setText(s);
		}
		catch (Exception e)
		{
			box.setText(e.toString());
		}
		animate(view);
	}
	
	public void onClickClear(View v)
	{
		try{
			FileOutputStream fo = openFileOutput("exception.log",MODE_PRIVATE);
			fo.write("MODE_PRIVATE".getBytes());
			fo.flush();
			fo.close();
		}catch(Exception e){
			box.setText(e.getMessage());
		}
		animate(v);
	}
	
	public void onClickToggle(View v)
	{
		box.setTextColor(((ToggleButton)v).isChecked()?0xaa093B6A:0x22222222);
		showToast("Color:"+box.getTextColors().toString());
	}

	public void saveFileToSDCard(String filename, String content) throws Exception
	{
		String sd=Environment.getExternalStorageState();
		if (sd.equals(Environment.MEDIA_MOUNTED))
		{
			File dir = Environment.getExternalStorageDirectory();
			File file = new File(dir,filename);
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(content.getBytes());
			fos.close();
			box.setText("save to file:"+file.getPath());
		}
		else
		{
			box.setText(sd);
		}
	}
	
	public void animate(View view){
		// 动画...
        TranslateAnimation animation = new TranslateAnimation(0, 20, 0, 20);
		animation.setDuration(300);
		view.startAnimation(animation);
	}
	public void showToast(String content)
	{
		Toast.makeText(this, content,Toast.LENGTH_LONG);
	}
}
