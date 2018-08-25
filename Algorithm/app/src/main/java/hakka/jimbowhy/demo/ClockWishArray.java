package hakka.jimbowhy.demo;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.PopupMenu.*;
import android.content.*;
import android.widget.*;
import java.util.regex.*;
import java.util.*;
import java.lang.reflect.*;


public class ClockWishArray extends Standard
{
	private int row = 0, col = 0;
	private int current = 0;
	private int [][] matrix;
	private int [][] mt = {	
		{1, 2, 3, 4},
		{12,13,14,5},
		{11,16,15,6},
		{10,9, 8, 7}};
	private int [][] mt1 = {	
		{1, 2, 3},
		{12,13,4},
		{11,14,5},
		{10,15,6},
		{ 9, 8,7}};
	
	private int [][] mt2 = {	
		{1, 2, 3, 4, 5, 6},
		//{16,17,18,19,20,7},
		{15,24,23,22,21,8},
		{14,13,12,11,10,9}};
	private int [][] mt3 = {	
		{1, 2, 3, 4},
		{16,17,18,5},
		{15,24,19,6},
		{14,23,20,7},
		{13,22,21,8},
		{12,11,10,9}};
	private int [][] mt4 = {{1, 2, 3, 4, 5}};
	private int [][] mt5 = {{1,2},{10,3},{9,4},{8,5},{7,6}};
	private int [][] mt6 = {{1, 2, 3, 4, 5},{10,9,8,7,6}};
	private int [][] mt7 = {{1},{2},{3},{4},{5}};
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		print("");
		print("Type row & column in textbox below");
	}

	@Override
	public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState)
	{
		super.onCreate(savedInstanceState, persistentState);
		print("onCreate(Bundle, PersistableBundle)");
	}

	@Override
	public void onClickEnter(Button view)
	{
		String in = cmd.getText().toString();
		print(in);
		if(in.isEmpty()){
			row = 0;
			col = 0;
			current = 0;
			print("reset, retry again");
		} else if(row == 0 | col == 0){
		
			try
			{

				Pattern p = Pattern.compile("^(\\d{1,2})\\s+(\\d{1,2})$");
				Matcher m = p.matcher(in);
				//print("regexp pattern "+p.pattern());
				m.find();
				row = Integer.parseInt(m.group(1));
				col = Integer.parseInt(m.group(2));
				print(String.format(
						  "ok, then type data row by row (%s,%s)", row, col));
				matrix = new int[row][col];
				current = 0;
			}
			catch (Exception e)
			{
				print("Type two numbers rows & columns for example 3 4");
				print(e.getMessage());
			}
		}
		else if (current < row)
		{

			try
			{
				Pattern p = Pattern.compile(("(\\d+\\s?){" + col + "}"));
				Matcher m = p.matcher(in);
				m.find();
				//print("eegex pattern "+p.pattern());
				//print("match group "+m.groupCount());
				String[] s = m.group().split("\\s");
				for (int i=0; i < col; i++)
				{
					matrix[current][i] = Integer.parseInt(s[i]);
				}
				print(m.group() + " data accept, and next more...");
				if (current == row - 1)
				{
					print("matrix:");
					for (int i=0; i < row; i++)
					{
						print(Arrays.toString(matrix[i]));
					}
					print("click Test to execute algorithm code");
				}
				current ++;
			}
			catch (Exception e)
			{
				print(e.getMessage());
				print("error try again");
			}

		}
		cmd.setText("");
	}

	@Override
	public void onClickRun(Button view)
	{
		if (row == 0 && col == 0)
		{
			int[][][] list = {mt,mt1,mt2,mt3,mt4,mt5,mt6,mt7};
			print("test example");
			Random r = new Random();
			int ran = r.nextInt(20);
			if( ran>=list.length ){
				clockwish(ranMatrix(r));
			}else{
				clockwish(list[ran]);
			}
		}
		else
		{
			clockwish(matrix);
		}
	}
	
	public int[][] ranMatrix(Random r)
	{
		int row = r.nextInt(10)+1;
		int col = r.nextInt(10)+1;
		int [][] m = new int[row][col];
		for(int i=0; i<row; i++){
			for(int j=0; j<col; j++){
				m[i][j] = r.nextInt(100);
			}
		}
		return m;
	}

	public void clockwish_v2(int[][] m)
	{
		int row = m.length;
		int col = m[0].length;
		int dir = 0;
		int[] mat = {
			0,     0, row-1, row-1,
			0,     1,     0,    -1,
			0, col-1, col-1,     0,
			1,     0,    -1,     0,
			col,row-1,col-1, row-2
			};
		
		print("data matrix:");
		for(int i=0; i<row; i++) print(Arrays.toString(m[i]));
		
		for(int i=0; i<row*col; i++)
		{
			int c = mat[dir%4]--;
			if( c==0 ) {
				int d = dir%4;
				int e = (d+1)%+16;
				mat[d] += mat[d+4];
				mat[d+8] += mat[d+12];
				mat[e] = 
				dir ++;
			}
		}
	}
	
	public void clockwish(int[][] m)
	{
		int row = m.length;
		int col = m[0].length;
		print("data matrix:");
		for(int i=0; i<row; i++) print(Arrays.toString(m[i]));
		
		int len = col>row? row/2+row%2 : col / 2 + col % 2;
		for (int i=0; i < len; i++)
		{
			int t = i, b = row - i, l = i, r = col - i;
			for (int j=l; j < r; j++) print(m[t][j]); 
			//print(" left to right");
			for (int j=t+1; j < b; j++) print(m[j][r - 1]);
			//print(" top to bottom");
			for (int j=r - 2; j >= l & b>t+1; j--) print(m[b - 1][j]);
			//print(" right to left");
			for (int j=b - 2; j > t & l<r-1; j--) print(m[j][l]); 
			//print(" bottom to top");
		}
	}
}
