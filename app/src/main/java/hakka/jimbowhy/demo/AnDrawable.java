package hakka.jimbowhy.demo;
import android.app.*;
import android.view.*;

public class AnDrawable extends EventAgent
{
	public AnDrawable(Activity ctx)
	{
		this(ctx, false);
	}
	public AnDrawable(Activity ctx, Boolean add)
	{
		super(ctx, R.layout.android_drawable, add);
		setClick(this, "onClick", view);
		setTouch(this, "onTouch", (View)view.getParent(), Gesture.disabled);
	}
	
	public void onClick(View v)
	{
		Alert.toast(context, v.toString());
	}
	
	public Boolean onTouch(View v, MotionEvent me)
	{
		Alert.toast(context, "pointer:"+me.getPointerCount() + v.toString());
		return false;
	}
}
