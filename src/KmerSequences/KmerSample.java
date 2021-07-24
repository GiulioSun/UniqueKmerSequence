package KmerSequences;

import java.util.ArrayList;
import java.util.List;
import org.apache.spark.api.java.function.PairFlatMapFunction;

import scala.Tuple2;

public class KmerSample implements PairFlatMapFunction<String, String, Integer> {


	public java.util.Iterator<Tuple2<String,Integer>>	call(String seq){

		int k = 31;

		List <Tuple2<String, Integer>> list = new ArrayList<>();

		for(int i = 0; i < seq.length() - k + 1 ; i++) {

			list.add(new Tuple2<String, Integer>(seq.substring(i, i + k), 1));

		}

		return list.iterator();
	}

}





