package KmerSequences;
import java.io.Serializable;

public class Count implements Serializable{

	private String kmer;
	private int count;
	private String regionWt;
	private String type;
	private String regionMut;


	public String getKmer() {
		return kmer;
	}

	public void setKmer(String kmer) {
		this.kmer = kmer;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getRegionWt() {
		return regionWt;
	}

	public void setRegionWt(String regionWt) {
		this.regionWt = regionWt;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRegionMut() {
		return regionMut;
	}

	public void setRegionMut(String regionMut) {
		this.regionMut = regionMut;
	}
	public Count(){
	}

	public Count( String kmer, int count, String regionWt, String regionMut, String type) {
		super();

		this.kmer = kmer;
		this.regionWt = regionWt;
		this.regionMut = regionMut;
		this.count = count;
		this.type = type;

	}





}
