package utilities;

import com.google.common.base.CaseFormat;
import org.apache.commons.text.CaseUtils;

public class StringUtilities {
	
	public static void main(String[] args) {
		
		String inputString = "To Lower Case";
				
		System.out.println(toMixedCase(inputString));
		
	}
	
	public static String toCamelCase (String inputString) {
		
		//split the inputString
		String[] stringArray = inputString.split(" ");
		
		StringBuffer buf = new StringBuffer();
		
		for (int i = 0; i < stringArray.length; i++) {
			
			buf.append(CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, stringArray[i]).replace("-", "_"));
			
		}
		
		
		
		return buf.toString();
	}
	
	public static String toLowerCase (String inputString) {
		
		//split the inputString
		String[] stringArray = inputString.split(" ");
		
		StringBuffer buf = new StringBuffer();
		
		for (int i = 0; i < stringArray.length; i++) {
			
			buf.append(CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, stringArray[i]).replace("-", "_"));
			
		}
		
		
		
		return buf.toString();
	}
	
	public static String toMixedCase (String inputString) {		
		
		String mixedCase = null;
		
		if (inputString.contains(" ")) {
		
			mixedCase = CaseUtils.toCamelCase(inputString, false, " ".toCharArray());
		} else {
			char c[] = inputString.toCharArray();
			c[0] += 32;
			inputString = new String(c);
			mixedCase = inputString;
		}
		
		return mixedCase;
		
	}

}
