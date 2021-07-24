package KmerSequences;

import FilterAndReader.*;

import java.util.ArrayList;
import java.util.List;



import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.neo4j.driver.AuthToken;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;

import scala.Tuple2;
import scala.Tuple4;

public class Main {

	public static void main(String[] args) {

		FileReader d = new FileReader();

		// Lettura del file del genoma di riferimento tramite la classe file reader

		String ref = d.fileReader2("data/chrT.txt");

		// Lettura del file delle mutazioni tramite la classe file reader

		List<String> readMutation = d.fileReader("data/variants.txt");
		List<Mutation> mutation = new ArrayList<>();

		for (String x : readMutation) {

			mutation.add(new Mutation(x.split("	")[0], Integer.parseInt(x.split("	")[1]),
					Integer.parseInt(x.split("	")[2]), x.split("	")[3], x.split("	")[4]));

		}

		// Inserimeto delle mutazioni nel genoma di riferimento

		NewGen ng = new NewGen();

		// Fisso il valore di k per la lunghezza dei k mer

		int k = 31;

		List<Tuple2<String, String>> region = ng.genMutation(mutation, ref, k);

		// Configurazione della connessione Spark

		Logger.getLogger("org").setLevel(Level.ERROR);

		Logger.getLogger("akka").setLevel(Level.ERROR);

		SparkConf sc = new SparkConf();

		sc.setAppName("k-mer sequences");

		sc.setMaster("local[*]");

		JavaSparkContext jsc = new JavaSparkContext(sc);

		//Caricamento delle regioni in distribuito

		JavaRDD<Tuple2<String, String>> region1 = jsc.parallelize(region);

		//Creazione dei kmers unici per le regioni del riferimento

		JavaPairRDD<String, String> region2 = region1.flatMapToPair(new KmerRif()).distinct();

		//Importazione dei genoma tumore e normale 

		JavaRDD<String> normal = jsc.textFile("data/normal.txt");
		JavaRDD<String> tumor = jsc.textFile("data/tumor.txt");

		//I file importati contengono caratteri da eliminare, questa pulizia Ë effettuata tramite la classe Filter

		JavaRDD<String> normClean = normal.filter(new Filter());
		JavaRDD<String> tumorClean = tumor.filter(new Filter());

		//Calcolo dei kmer dei due campioni attraverso la classe KmerSample e calcolo dei counts attraverso un reducebykey

		JavaPairRDD<String, Integer> CountNormal = normClean.flatMapToPair(new KmerSample()).reduceByKey((x, y) -> x + y);

		JavaPairRDD<String, Integer> CountTumor = tumorClean.flatMapToPair(new KmerSample()).reduceByKey((x, y) -> x + y);

		// Calcolo dei count dei campioni sul riferimento attraverso una join

		JavaPairRDD<String, Tuple2<Integer, String>> regionNormal = CountNormal.join(region2);

		JavaPairRDD<String, Tuple2<Integer, String>> regionTum = CountTumor.join(region2);

		// Creazione della connessione con sql

		SparkSession spark = SparkSession.builder().appName("Dataframe").config(jsc.getConf()).getOrCreate();

		TempView tV = new TempView();

		//Creazione del dataset per il campione normale e calcolo della mediana dei count su ciascuna regione

		JavaRDD<Count> counts = regionNormal.map(x -> new Count(x._1, x._2._1, x._2._2.split(",")[0], x._2._2.split(",")[1], x._2._2.split(",")[2]));

		Dataset<Row> NormWTdf = spark.createDataFrame(counts, Count.class);

		tV.createView(NormWTdf, "counts");

		String query = ("select regionwt, regionmut, type,  percentile(count, 0.5) as MedianNorm from counts group by regionwt, regionmut, type");

		Dataset<Row> MedianNormWT = spark.sql(query);

		tV.createView(MedianNormWT, "counts1");

		//Creazione del dataset per il campione normale e calcolo della mediana dei count su ciascuna regione

		JavaRDD<Count> counts2 = regionTum.map(y -> new Count(y._1, y._2._1, y._2._2.split(",")[0], y._2._2.split(",")[1], y._2._2.split(",")[2]));

		Dataset<Row> TumWTdf = spark.createDataFrame(counts2, Count.class);

		tV.createView(TumWTdf, "counts2");

		String query2 = ("select regionwt, regionmut, type, percentile(count, 0.5) as MedianTum from counts2 group by regionwt, regionmut, type");

		Dataset<Row> MedianTumWT = spark.sql(query2);

		tV.createView(MedianTumWT, "counts3");

		//Creazione di un dataframe contente le regioni wt e mutate, le mediane dei count di ciascun campione e il riferimento per ciascuna mediana

		String query3 = "SELECT counts1.regionwt, counts1.regionmut, counts1.type, counts1.MedianNorm, counts3.MedianTum\r\n"
				+ "FROM counts1\r\n"
				+ "INNER JOIN counts3 ON counts3.regionwt=counts1.regionwt AND counts3.regionmut = counts1.regionmut AND counts3.type = counts1.type\r\n order by counts3.regionwt, counts3.type";

		Dataset<Row> interoutput = spark.sql(query3);

		//Passaggio dal dataframe a due RDD di oggetti, Normal e Tumor

		JavaRDD<Tumor> dataTumor = interoutput.toJavaRDD().map(new Function<Row, Tumor>() {
			@Override
			public Tumor call(Row row) {
				Tumor tum = new Tumor(row.getString(0), row.getString(1), row.getString(2), row.getDouble(4));

				return tum;
			}
		});

		JavaRDD<Normal> dataNormal = interoutput.toJavaRDD().map(new Function<Row, Normal>() {
			@Override
			public Normal call(Row row) {
				Normal norm = new Normal(row.getString(0), row.getString(1), row.getString(2), row.getDouble(3));

				return norm;
			}
		});

		//Calcolo della differenza tra le mediane della stessa regione (wt-mut) per ciascun campione

		JavaPairRDD<String, Double> reduceNorm = dataNormal.mapToPair(x -> new Tuple2<String, Double>(x.getRegionWt() + "," + x.getRegionMut() + "," + x.getSeq(), x.getMedian()))
				.reduceByKey((x, y) -> x - y);


		JavaPairRDD<String, Double> reduceTumor = dataTumor.mapToPair(x -> new Tuple2<String, Double>(x.getRegionWt() + "," + x.getRegionMut() + "," + x.getSeq(), x.getMedian()))
				.reduceByKey((x, y) -> x - y);

		//Test di ipotesi binomiale sulle differenze prima calcolate

		List<String> binomiaListTumor = new ArrayList<>();

		List<String> binomiaListNorm = new ArrayList<>();

		List<Tuple2<String, Double>> reduceTumor2 = reduceTumor.collect();

		List<Tuple2<String, Double>> reduceNorm2 = reduceNorm.collect();

		BinomialT bT = new BinomialT();

		for(Tuple2<String, Double> s : reduceTumor2) {

			int abs = (int) Math.abs(s._2());

			binomiaListTumor.add(s._1 + "{ " + bT.binomialTest(0.0003, abs , 16000, 0.01) + " }");

		}

		for(Tuple2<String, Double> s : reduceNorm2) {

			int abs = (int) Math.abs(s._2());

			binomiaListNorm.add(s._1 + "{ " + bT.binomialTest(0.0003, abs , 16000, 0.01) + " }");

		}

		for (String i : binomiaListTumor ) {

			System.out.println(i);
		}

		for (String i : binomiaListNorm ) {

			System.out.println(i);
		}

		// Connessione a neo4j

		String uri = "bolt://localhost:7687";
		AuthToken token = AuthTokens.basic("neo4j", "Prova");
		Driver driver = GraphDatabase.driver(uri, token);
		Session s = driver.session();
		System.out.println("Connessione stabilita!");
		Result result;

		//Collect necessari per la creazioni di nodi ed archi

		List<Tuple4<String, String, String, String>> regionForNeo =  region2.map(x-> new Tuple4<String, String, String, String>(x._1, x._2.split(",")[0], x._2.split(",")[1], x._2.split(",")[2])).collect();
		List<Tuple2<String, Integer>> tumorNode = CountTumor.map(x-> new Tuple2<String, Integer>(x._1, x._2)).collect();
		List<Tuple2<String, Integer>> normalNode = CountNormal.map(x-> new Tuple2<String, Integer>(x._1, x._2)).collect();

		// Creazione dei nodi Riferimento, Normale, Tumore

		String cql2 = "Create (:GenomeRif {gen:'GenomRif'}), (:GenomeTumor {person: 1}), (:GenomeNormal {person: 1})";
		result = s.run(cql2);


		// Creazione degli archi tra il riferimento e le regioni wt e mutate

		for (Tuple2<String, String> node : region ) {

			String cql = "Match (n:GenomeRif) Create (n)-[:region]->(n1: WildType { seq: $wt}), (n)-[:Region]->(n2: Mutated { seq: $mut})";
			String wt = node._1;
			String mut = node._2;

			result = s.run(cql, Values.parameters("wt", wt, "mut", mut));

		}

		// Creazione degli archi tra Normale e i suoi k mer

		for (Tuple2<String, Integer> node : normalNode) {

			String cql = "Match (n:GenomeNormal) Create (n)-[:Kmers]->(n1: Normal{ seq: $kmer, count: $count})";
			String kmer = node._1;
			int count = node._2;

			result = s.run(cql, Values.parameters("kmer", kmer, "count", count));

		}

		for (Tuple2<String, Integer> node : tumorNode) {


			String cql = "Match (n:GenomeTumor) Create (n)-[:Kmers]->(n1: Tumor{ seq: $kmer, count: $count})";
			String kmer = node._1;
			int count = node._2;

			result = s.run(cql, Values.parameters("kmer", kmer, "count", count));

		}

		for (Tuple4<String, String, String, String> a: regionForNeo) {
			
			String regionwt = a._2().toString();
			String regionmut = a._3().toString();
			String seq = a._1().toString();
			String b = a._4().toString();

			if(b.equals("WildType")) {

				String cql = "Match (n:WildType) Where n.seq = $regionwt Create (n)-[:Kmers]->(:Kmer {seq: $seq}) ";
				result = s.run(cql, Values.parameters("regionwt", regionwt, "seq", seq ));
			}else {
				String cql = "Match (n:Mutated) Where n.seq = $regionmut Create (n)-[:Kmers]->(:Kmer {seq: $seq}) ";
				result = s.run(cql, Values.parameters("regionmut", regionmut, "seq", seq ));

			}

		}	
		String cql = "Match (n:Kmer), (m:Tumor) Where n.seq = m.seq Create (n)-[:Meet]->(m) ";
		result = s.run(cql);


		s.close();

		System.out.println("Connessione chiusa!");


		jsc.close();

	}

}