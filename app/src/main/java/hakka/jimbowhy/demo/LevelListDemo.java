package hakka.jimbowhy.demo;
import android.app.*;
import android.view.*;
import android.widget.*;
import android.graphics.drawable.*;
import java.util.*;
import android.os.*;

public class LevelListDemo extends EventAgent
{
	
	private ImageView iv;
	private LevelListDrawable lld;
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message m)
		{
			if( m.what==0x1234 ){
				int level = lld.getLevel()+10;
				if( level>40 ) lld.setLevel(10);
				level = level>40? 10:level;
				iv.setImageLevel(level);
			}
			super.handleMessage(m);
		}
		
	};
	
	public LevelListDemo(Activity ctx)
	{
		this(ctx, false);
	}
	public LevelListDemo(Activity ctx, Boolean add)
	{
		super(ctx, R.layout.standard, add);
		iv = (ImageView)view.findViewById(R.id.standardImageView);
		lld = (LevelListDrawable)iv.getDrawable();

		new Timer().schedule(new TimerTask() {
				@Override
				public void run()
				{
					handler.sendEmptyMessage(0x1234);
				}
			}, 0, 200);
	}
}
