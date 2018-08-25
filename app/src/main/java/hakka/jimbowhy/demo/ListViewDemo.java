package hakka.jimbowhy.demo;
import android.app.*;
import android.os.*;
import android.view.*;
import android.content.*;
import android.widget.*;
import java.util.*;
import android.util.*;

public class ListViewDemo extends EventAgent
{
	private ListView lv;
	private TextView box;
	
	public ListViewDemo(Activity ctx, Boolean add)
	{
		super(ctx, R.layout.listview_demo, add);
		
		//LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		lv = (ListView)view.findViewById(R.id.listviewdemoListView1);
		box = (TextView)view.findViewById(R.id.listviewdemoTextView1);
		initStringList();
		
		setItemClick(view,this,
			R.id.listviewdemoListView1,
			"onClick_listviewdemoListView1");
		setItemLongClick(view, this,
			R.id.listviewdemoListView1,
			"onLongClick_listviewdemoListView1");
		setClick(view,this,
			R.id.listviewdemoButtonFill,
			"onClick_listviewdemoButtonFill");
		setClick(view, this, 
			R.id.listviewdemoButtonSimple,
			"onClick_listviewdemoButtonSimple");
		setClick(view, this,
			R.id.listviewdemoButtonAdvanced,
			"onClick_listviewdemoButtonAdvanced");
		setClick(view,this,
			R.id.listviewdemoButtonReorder,
			"onClick_listviewdemoButtonReorder");
		setClick(view,this,
			R.id.listviewdemoButtonClear,
			"onClick_listviewdemoButtonClear");
	}
	
	public void initStringList()
	{
		ArrayAdapter<String> la = new ArrayAdapter<String>(context,
			android.R.layout.simple_list_item_1, // Use android's layout TextView#text1
			DataBunk.strings);
		lv.setAdapter(la);
	}
	
	public void initSimpleList()
	{
		String[] columns = {"index","content"};
		int[] textViewIds = {android.R.id.text1,android.R.id.text2};
		int layoutId = android.R.layout.simple_expandable_list_item_2;
		List<ArrayMap<String,String>> data = new ArrayList<ArrayMap<String,String>>();
		int index = 0;
		for( String s : DataBunk.strings ){
			ArrayMap<String,String> m = new ArrayMap<String,String>();
			m.put(columns[0],number2Nihongo( ++index,0 ));
			m.put(columns[1],s);
			data.add(m);
		}
		SimpleAdapter sa = new SimpleAdapter( context, data, 
			layoutId, columns, textViewIds);
		lv.setAdapter(sa);
	}
	
	public String number2Nihongo(int i, int s)
	{
		String[] map = {
			"","いち","に","さん","よう","ご","るく","なな","はち","きゅう","じゅう"
		};
		String[] postfix = {
			"じゅう","ひゃく","せん","まん","じゅう","ひゃく","せん","おく"
		};
		String x = map[i%10];
		if(s>0) x = i%10==1? postfix[s-1]:x+postfix[s-1];
		if(i/10>0) return number2Nihongo(i/10,++s) + x;
		return x;
	}
	
	public void initCustomList()
	{
		CustomAdapter ca = new CustomAdapter(context,DataBunk.strings);
		lv.setAdapter(ca);
	}
	
	public void onClick_listviewdemoListView1(AdapterView parent, View view, int position, long id)
	{
		box.setText("ClickItem:"+parent.getItemAtPosition(position));
	}
	public boolean onLongClick_listviewdemoListView1(AdapterView parent, View view, int position, long id)
	{
		box.setText("Select Item:"+parent.getItemAtPosition(position));
		parent.setSelection(position);
		return true;
	}
	public void onClick_listviewdemoButtonFill(View v)
	{
		initStringList();
	}
	
	public void onClick_listviewdemoButtonSimple(View v)
	{
		initSimpleList();
	}
	
	public void onClick_listviewdemoButtonAdvanced(View v)

	{
		initCustomList();
	}
	
	public void onClick_listviewdemoButtonReorder(View v)
	{
		Alert.show(context,"oooh","no implementation now");
	}
	
	public void onClick_listviewdemoButtonClear(View v)
	{
		
	}
}

class CustomAdapter extends BaseAdapter
{
	String[] data;
	int position;
	LayoutInflater inflater;
	public CustomAdapter(Context ctx, String[] s)
	{
		data = s;
		position = 0;
		inflater = (LayoutInflater)ctx.getSystemService(ctx.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public long getItemId(int posi)
	{
		return position;
	}

	@Override
	public int getCount()
	{
		return data.length;
	}

	@Override
	public View getView(int posi, View v, ViewGroup vg)
	{
		v = inflater.inflate(R.layout.list_template,null);
		ImageView iv = (ImageView) v.findViewById(R.id.listtemplateImageView);
		EditText et = (EditText) v.findViewById(R.id.listtemplateEditText);
		et.setText(data[posi]);
		iv.setImageResource(android.R.drawable.star_on);
		return v;
	}

	@Override
	public Object getItem(int posi)
	{
		return data[posi];
	}

}
