package hakka.jimbowhy.demo;
import android.app.*;
import android.os.*;
import android.view.View.*;
import android.view.*;
import java.lang.reflect.*;
import android.content.*;
import android.util.*;
import android.widget.*;

public class ThemeDemo extends Activity
{
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);

		setContentView(R.layout.theme_demo);
		EventAgent.setClick(this,
			R.id.themedemoButtonColorful, 
			"onClick_themedemoButtonColorful");
		EventAgent.setClick(this,
			R.id.themedemoButtonGray,
			"onClick_themedemoButtonGray");

	}

	public void onClick_themedemoButtonColorful(View v)
	{
		((Button)v).setTextColor(0x4488dd);
	}
	public void onClick_themedemoButtonGray(View v)
	{

	}
	

}
