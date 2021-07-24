package KmerSequences;

import java.util.ArrayList;
import java.util.List;

import org.apache.spark.api.java.function.PairFlatMapFunction;

import scala.Tuple2;

public class KmerRif implements PairFlatMapFunction<Tuple2<String, String>,  String, String> {

	public java.util.Iterator<Tuple2<String,String>>	call(Tuple2<String, String> seq){

		int k = 10;

		List <Tuple2<String, String>> list = new ArrayList<>();


		for(int i = 0; i < seq._1.length() - k + 1 ; i++) {

			list.add(new Tuple2<String, String>(seq._1.substring(i, i + k), seq._1 +  "," + seq._2 + ",WildType"));
			list.add(new Tuple2<String, String>(seq._2.substring(i, i + k), seq._1 +  "," + seq._2 + ",Mutation"));
		}

		return list.iterator();

	}


}
