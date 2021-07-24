package KmerSequences;

import java.util.ArrayList;
import java.util.List;

import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.sql.catalyst.expressions.Concat;

import scala.Tuple2;


public class NewGen {

	public List<Tuple2<String, String>>	genMutation(List<Mutation> m, String g, int k){

		String[] mutation = g.split("");
		String[] wt = g.split("");

		List<Tuple2<String, String>> listFrequences = new ArrayList<Tuple2<String, String>>();

		for (Mutation i : m) {

			mutation[i.getPosition1()-1] = i.getSostitution1();

			mutation[i.getPosition2()-1] = i.getSostitution2();

			String s = "";
			String s1 = "";

			for (int j = i.getPosition1() - (k + 1) ; j < i.getPosition2() + k; j++ ) {  

				s += mutation[j];
				s1 += wt[j];
			}

			listFrequences.add(new Tuple2<String, String>(s1, s));

		}

		return listFrequences;

	}

}
