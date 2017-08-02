package p;

public class StringUtil {

	public static boolean isNull(String str) {
		boolean isNull = false;
		if (str == null || str.equals("") || str.isEmpty()) {
			isNull = true;
		}
		return isNull;
	}
}
