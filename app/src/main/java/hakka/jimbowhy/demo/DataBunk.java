package hakka.jimbowhy.demo;
import java.util.*;
import android.provider.*;

public class DataBunk
{
	static public String[]
		strings = {
		"Negative for nouns and adjectives",
		"For nouns and na-adjectives: Attach 「じゃない」 to the end",
		"Example",
		"元気＋じゃない＝元気じゃない",
		"きれい＋じゃない＝きれいじゃない",
		"For i-adjectives: Drop the 「い」 at the end and replace with 「くない」",
		"Example",
		"忙しい＋くない＝忙しくない",
		"かわいい＋くない＝かわいくない",
		"Exceptions: 「いい」 conjugates from 「よい」",
		"いい → よい＋くない＝よくない",
		"かっこいい → かっこよい＋くない＝かっこよくない",
		"Note: Similar to i-adjectives, you must never use the declarative 「だ」 with the negative.",

		"Example",

		"サラダ – salad",
		"ステーキ – steak",
		"あまり – not very (when used with negative)",
		"この – this",
		"本 【ほん】 – book",
		"面白い 【おも・しろ・い】(i-adj) – interesting",
		"今年 【ことし】 – this year",
		"冬 【ふゆ】 – winter",
		"寒い 【さむ・い】(i-adj) – cold"
	};
	static public ArrayList<String> getContacts()
	{
		return new ArrayList<String>(
			//ContactsContract.Data.DISPLAY_NAME
		);
	}
}
