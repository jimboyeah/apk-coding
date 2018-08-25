package hakka.jimbowhy.demo;
import android.util.*;
import android.view.*;
import android.content.*;
import java.lang.reflect.*;
import android.app.*;
import android.widget.*;
import android.appwidget.*;
import java.io.*;
import android.view.View.*;
import android.widget.AdapterView.*;
import android.view.GestureDetector.*;
import android.gesture.*;

public class EventAgent
{
	public Activity context;
	public View view;
	
	public EventAgent(){}
	
	public EventAgent( Activity ctx, int layoutId, Boolean add)
	{
		context = ctx;
		view = View.inflate(ctx,layoutId,null);
		FrameLayout.LayoutParams paras = new FrameLayout.LayoutParams(
			FrameLayout.LayoutParams.MATCH_PARENT, 
			FrameLayout.LayoutParams.MATCH_PARENT);    
		if(add){
			context.addContentView(view,paras);
		}else{
			context.setContentView(view);
		}
		setClick(view, this, R.id.standardTextView,"onTextLongClick");
	}
	public void onTextLongClick(View v)
	{
		PopupMenu pm = new PopupMenu(context, view.findViewById(R.id.standardTextView));
		pm.inflate(R.menu.comtext);
		setPopupMenuClick(pm, this, "onPopupMenuClick");
		pm.show();
	}
	public Boolean onPopupMenuClick (MenuItem mi)
	{
		switch( mi.getItemId() )
		{
			case R.id.contextCopy:
			ClipboardManager cm = (ClipboardManager)
				context.getSystemService(Activity.CLIPBOARD_SERVICE);
			TextView tv = (TextView)view.findViewById(R.id.standardTextView);
			cm.setText(tv.getText());
			break;
			
			case R.id.contextVerbose:
			case R.id.contextSimplify:
				mi.setChecked(!mi.isChecked());
				break;
		}
		return false;
	}
	
	// listenner bind for view
	static public void setClick(View v, EventAgent ea, int id, String handlerName)
	{
		setClick(ea, handlerName, v.findViewById(id));
	}
	static public void setClick(Object ctx, String handlerName, View v)
	{
		Class[] cParas = {View.class};
		EventObsever ev = new EventObsever();
		ev.initializeHandler(ctx,handlerName,cParas);
		ev.initializeListenner(v,"setOnClickListener",View.OnClickListener.class);
	}
	
	static public void setLongClick(View v, EventAgent ea, int id, String handlerName)
	{
		setClick(ea, handlerName, v.findViewById(id));
	}
	static public void setLongClick(Object ctx, String handlerName, View v)
	{
		Class[] cParas = {View.class};
		EventObsever ev = new EventObsever();
		ev.initializeHandler(ctx,handlerName,cParas);
		ev.initializeListenner(v,"setOnLongClickListener",View.OnLongClickListener.class);
	}
	
	// for View & Activity
	static public void setClick(Activity ctx, int id, String handlerName) 
	{
		setClick(ctx, handlerName, ctx.findViewById(id));
	}
	static public void setClick(View v, int id, String handlerName)
	{
		setClick(v, handlerName, v.findViewById(id));
	}
	

	// for ListView
	static public void setItemClick(View v, EventAgent ea, int id, String handlerName)
	{
		setItemClick(ea, handlerName, (ListView)v.findViewById(id));
	}
	static public void setItemClick(Object ctx, String handlerName, ListView lv)
	{
		Class[] cParas = {AdapterView.class,View.class,int.class,long.class};
		EventObsever ev = new EventObsever();
		ev.initializeHandler(ctx,handlerName,cParas);
		ev.initializeListenner(lv,"setOnItemClickListener",ListView.OnItemClickListener.class);
	}
	
	static public void setItemLongClick(View v, EventAgent ea, int id, String handlerName)
	{
		setItemLongClick(ea, handlerName, (ListView)v.findViewById(id));
	}
	static public void setItemLongClick(Object ctx, String handlerName, ListView lv)
	{
		Class[] cParas = {AdapterView.class,View.class,int.class,long.class};
		EventObsever ev = new EventObsever();
		ev.initializeHandler(ctx,handlerName,cParas);
		ev.initializeListenner(lv,"setOnItemLongClickListener",
			ListView.OnItemLongClickListener.class);
	}
	
	// touch events
	static public void setTouch(View v, EventAgent ea, int id, String handlerName)
	{
		setTouch(ea, handlerName, v.findViewById(id), Gesture.disabled);
	}
	static public void setGesture(View v, EventAgent ea, int id,
		String handlerName, int gestureTypes )
	{
		setTouch(ea, handlerName, v.findViewById(id), gestureTypes);
	}
	static public void setTouch(Object ctx, String handlerName, View v, int gestureTypes)
	{
		Class[] cParas = {View.class, MotionEvent.class};
		EventObsever ev = new EventObsever();
		ev.initializeHandler(ctx,handlerName,cParas);
		ev.initializeListenner(v,"setOnTouchListener",View.OnTouchListener.class);
		ev.setGestureTypes(gestureTypes);
	}
	
	// popupmenu
	static public void setPopupMenuClick(PopupMenu pm, EventAgent ea, String handlerName)
	{
		Class[] cParas = {MenuItem.class};
		EventObsever ev = new EventObsever();
		ev.initializeHandler(ea,handlerName,cParas);
		ev.initializeListenner(pm,"setOnMenuItemClickListener",
			PopupMenu.OnMenuItemClickListener.class);
	}
}

class Gesture
{
	static public int disabled = 0;
	static public int onContextClick = 1;
	static public int onDown = 2;
	static public int onDoubleTap = 4;
	static public int onDoubleTapEvent = 8;
	static public int onFling = 16;
	static public int onScroll = 32;
	static public int onShowPress = 64;
	static public int onSingleTapConfirmed = 128;
	static public int onSingleTapUp = 256;
	private Gesture(){ }
}

class EventObsever implements 
View.OnClickListener, View.OnLongClickListener, 
ListView.OnItemClickListener, ListView.OnItemLongClickListener,
View.OnTouchListener, 
GestureDetector.OnGestureListener, 
GestureDetector.OnDoubleTapListener, 
GestureDetector.OnContextClickListener,
PopupMenu.OnMenuItemClickListener
{
	private Method handler;
	private Object context;
	private int gesture = Gesture.disabled;
	private GestureDetector gd;

	public void initializeListenner(Object ctx, String methodName, Class cListenner)
	{
		try
		{
			Method m = ctx.getClass().getMethod(methodName, cListenner);
			m.invoke(ctx, this); // register listenner with EventObserver
		}
		catch (InvocationTargetException ex)
		{
			UncaughtAgent.log(ex.getTargetException());
		}
		catch (Exception e)
		{
			UncaughtAgent.log(e);
		}
	}
	public void initializeHandler(Object ctx, String methodName, Class[] paras)
	{
		try
		{
			context = ctx;
			handler = ctx.getClass().getMethod(methodName, paras);
		}
		catch (Exception e)
		{
			UncaughtAgent.log(e);
		}
	}
	
	public void setGestureTypes(int types)
	{
		gesture = types;
		if( gesture!=Gesture.disabled && gd==null )
			gd = new GestureDetector(this);
	}

	@Override
	public void onClick(View view)
	{
		try
		{
			handler.invoke(context, view);
		}
		catch (InvocationTargetException ex)
		{
			UncaughtAgent.log(ex.getTargetException());
		}
		catch (Exception e)
		{
			UncaughtAgent.log(e);
		}
	}

	@Override
	public boolean onLongClick(View view)
	{
		try
		{
			return handler.invoke(context, view);
		}
		catch (InvocationTargetException ex)
		{
			UncaughtAgent.log(ex.getTargetException());
		}
		catch (Exception e)
		{
			UncaughtAgent.log(e);
		}
		return false; // true avoid onClick
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		try
		{
			handler.invoke(context, parent, view, position, id);
		}
		catch (InvocationTargetException ex)
		{
			UncaughtAgent.log(ex.getTargetException());
		}
		catch (Exception e)
		{
			UncaughtAgent.log(e);
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
	{
		try {
			return handler.invoke(context, parent, view, position, id);
		}
		catch (InvocationTargetException ex)
		{
			UncaughtAgent.log(ex.getTargetException());
		}
		catch (Exception e)
		{
			UncaughtAgent.log(e);
		}
		return false; // true avoid onClick
	}

	@Override
	public boolean onTouch(View view, MotionEvent me)
	{
		try {
			return handler.invoke(context, view, me);
		}
		catch (InvocationTargetException ex)
		{
			UncaughtAgent.log(ex.getTargetException());
		}
		catch (Exception e)
		{
			UncaughtAgent.log(e);
		}
		return false; // true for event interception
	}


	// for motion geatures

	@Override
	public boolean onContextClick(MotionEvent p1)
	{
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean onDown(MotionEvent p1)
	{
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean onDoubleTap(MotionEvent p1)
	{
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent p1)
	{
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean onFling(MotionEvent p1, MotionEvent p2, float p3, float p4)
	{
		// TODO: Implement this method
		return false;
	}

	@Override
	public void onLongPress(MotionEvent p1)
	{
		// TODO: Implement this method
	}

	@Override
	public void onShowPress(MotionEvent p1)
	{
		// TODO: Implement this method
	}

	@Override
	public boolean onScroll(MotionEvent p1, MotionEvent p2, float p3, float p4)
	{
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean onSingleTapUp(MotionEvent p1)
	{
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent p1)
	{
		// TODO: Implement this method
		return false;
	}

	// for PopupMenu
	@Override
	public boolean onMenuItemClick(MenuItem item)
	{
		try {
			return handler.invoke(context, item);
		}
		catch (InvocationTargetException ex)
		{
			UncaughtAgent.log(ex.getTargetException());
		}
		catch (Exception e)
		{
			UncaughtAgent.log(e);
		}
		return false;
	}


	//@Override 

}
