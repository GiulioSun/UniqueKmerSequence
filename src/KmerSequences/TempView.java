package KmerSequences;

import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.Dataset;

class MyExe extends Exception{}

public class TempView{

	public void createView(Dataset d, String a) {

		try {if(!verifyEmpty(d))

			d.createTempView(a);

		} catch (Exception e) {

			System.out.println("Dataset empty!");

			e.printStackTrace();

		}

	} public boolean verifyEmpty(Dataset d) throws MyExe {

		if(d.isEmpty()) 

			throw new MyExe();

		return false;
	}





}

