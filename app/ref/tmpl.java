package com.jimbowhy.demo;
import android.app.*;
import android.os.*;
import android.view.View.*;
import android.view.*;
import java.lang.reflect.*;
import android.content.*;
import android.util.*;
import android.widget.*;

public class ReflectionDemo extends Activity
{
	private TextView box;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.reflection);
		setViewClickListenner(
			R.id.reflectionButtonCotors, 
			"onClick_reflectionButtonCotors");
			
		
		box = (TextView)findViewById(R.id.reflectionTextViewLog);
	}
	public void setViewClickListenner(int id, final String methodName) 
	{
		try{
		final Context ctx = this;
		View v = findViewById(id);
		final Method m = getClass().getMethod(methodName, View.class);
		if(v==null) return;
		v.setOnClickListener(
			new View.OnClickListener(){
				@Override
				public void onClick(View v) 
				{
					try{
						m.invoke(ctx, v);
					}catch(Exception e){
						Log.d(getPackageName(),e.getMessage());
					}
				}
			}
		);
		}catch(Exception e){ 
			Log.w(getPackageName(), e.getMessage());
		}
	}
	
	public void onClick_reflectionButtonCotors(View v)
	{
		box.setText(v.toString());
	}
	public void onClick_reflectionButtonField(View v)
	{
		
	}
	public void onClick_reflectionButtonMethod(View v)
	{

	}
	public void onClick_reflectionButtonInvoke(View v)
	{

	}
	
}