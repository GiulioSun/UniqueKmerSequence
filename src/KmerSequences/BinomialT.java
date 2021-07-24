package KmerSequences;

import org.apache.commons.math3.stat.inference.AlternativeHypothesis;


public class BinomialT extends org.apache.commons.math3.stat.inference.BinomialTest {
	
	
	public String binomialTest(double ipotesiNulla, int successi,int numeroProve, double alpha) {
	
	BinomialT bT = new BinomialT();
	
	AlternativeHypothesis ipotesiAlternativa = AlternativeHypothesis.TWO_SIDED;
	 
	// p.value
	
	double significativita = bT.binomialTest(numeroProve, successi, ipotesiNulla,
			ipotesiAlternativa);
	
	// rigetto H0
	
	boolean rigettata = bT.binomialTest(numeroProve, successi, ipotesiNulla,
	        ipotesiAlternativa, alpha);
	 
	return "rigettata: " + rigettata + ", Significatività: " + significativita;
	
	}
}
