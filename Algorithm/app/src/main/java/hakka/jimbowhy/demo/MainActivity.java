package hakka.jimbowhy.demo;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.PopupMenu.*;
import android.content.*;

public class MainActivity extends Activity 
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
		UncaughtAgent.installHandler(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.options,menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		Intent i;
		switch(item.getItemId())
		{
			case R.id.itemCWA:
				i = new Intent(this,ClockWishArray.class);
				startActivity(i);
				break;
			case R.id.itemRegExp:
				i = new Intent(this,RegExp.class);
				startActivity(i);
				break;
			default:
		}
		return super.onOptionsItemSelected(item);
	}
	
}
