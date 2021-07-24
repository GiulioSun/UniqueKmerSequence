package FilterAndReader;

import org.apache.spark.api.java.function.Function;

public class Filter implements Function<String, Boolean> {

	@Override
	public Boolean call(String v1) throws Exception {
		
		if(v1.substring(0).contains("@") ||
				v1.substring(0).contains("5") ||
				v1.substring(0).contains("+") ||
				v1.substring(0).contains(">"))
		
		{
			return false;
			
		}else {
			
			return true;
		}
	}

}
