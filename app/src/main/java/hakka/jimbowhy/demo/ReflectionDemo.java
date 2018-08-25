package hakka.jimbowhy.demo;
import android.app.*;
import android.os.*;
import android.view.View.*;
import android.view.*;
import java.lang.reflect.*;
import android.content.*;
import android.util.*;
import android.widget.*;
import java.lang.annotation.*;


public class ReflectionDemo extends Activity
{
	private TextView box;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.reflection);
		EventAgent.setClick(this,
			R.id.reflectionButtonCotors, 
			"onClick_reflectionButtonCotors");
		EventAgent.setClick(this,
			R.id.reflectionButtonField, 
			"onClick_reflectionButtonField");
		EventAgent.setClick(this,
			R.id.reflectionButtonMethod, 
			"onClick_reflectionButtonMethod");
		EventAgent.setClick(this,
			R.id.reflectionButtonInvoke, 
			"onClick_reflectionButtonInvoke");
		
		box = (TextView)findViewById(R.id.reflectionTextViewLog);
		box.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){ v.setBackgroundColor(0x88ddccdd);}
		});
	}
	
	// Reflection of View
	public void onClick_reflectionButtonCotors(View v)
	{
		try{
			String classPath = "android.view.View";
			Class c = Class.forName(classPath);

			Constructor[] cos = c.getConstructors();
			String buf = "All public constructors:\n";
			for( Constructor co : cos )
			{
				buf += co.toString() .replace(View.class.getPackage().getName()+".","")+ "\n";
			}
			buf += "\nAll constructors:\n";
			cos = c.getDeclaredConstructors();
			for( Constructor co : cos )
			{
				//co.setAccessible(True);
				buf += co.toString().replace(View.class.getPackage().getName()+".","") + "\n";
			}
			
			//Constructor defC = c.getConstructor(null);
			Class[] paraTypes = {Context.class,AttributeSet.class};
			Constructor con = c.getConstructor(paraTypes); // fecth View(Context, AttributeSet)
			buf += "\nAnnotations: \n";
			Annotation[] ans = con.getAnnotations();
			for(Annotation an : ans )
			{
				buf += an.toString().replace(View.class.getPackage().getName()+".","")+"\n";
			}
			buf += "\n\nNew Instance: " + con.newInstance(this,null).toString();
			box.setText(buf);
		}catch(Exception e){
			UncaughtAgent.d(e.getMessage());
		}
	}
	
	public void onClick_reflectionButtonMethod(View v)
	{
		try{
			Class c = box.getClass();
			Method[] ms = c.getMethods();
			Method mark = null;
			String buf = "All method:\n";
			for( Method m : ms )
			{
				buf += m.toString().replace(View.class.getName()+".","") + "\n";
				if( !m.isAccessible() ) mark = m;
			}
			buf += "\nTry to invoke any private member\n";
			try{
				mark.setAccessible(true);
				mark.invoke(c,null);
			}catch(Exception e){
				buf += e.getMessage()+"\n";
			}
			box.setText(buf);
		}catch(Exception e){
			UncaughtAgent.d( e.getMessage());
		}		
	}
	public void onClick_reflectionButtonField(View v)
	{
		try{
			Class c = TextView.class;
			Field[] fs = c.getDeclaredFields();
			String buf = "All fields:\n";
			for( Field f : fs )
			{
				buf += String.format("%s \n",
					f.toString().replace(TextView.class.getName()+".",""));
			}
			box.setText(buf);
		}catch(Exception e){
			UncaughtAgent.d( e.getMessage());
		}
	}
	public void onClick_reflectionButtonInvoke(View v)
	{
		box.setText("Nothing to invoke here");
	}
	
}
