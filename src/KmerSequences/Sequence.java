package KmerSequences;

import java.io.Serializable;

public  class Sequence implements Serializable{
	
	private String regionWt;
	private String regionMut;
	private String typeRegion;
	private String seq;
	private Double median;
	
	public Sequence(String regionWt, String regionMut, String typeRegion, String seq, Double median) {
		super();
		this.regionWt = regionWt;
		this.regionMut = regionMut;
		this.typeRegion = typeRegion;
		this.seq = seq;
		this.median = median;
	}
	public String getRegionWt() {
		return regionWt;
	}
	public void setRegionWt(String regionWt) {
		this.regionWt = regionWt;
	}
	public String getRegionMut() {
		return regionMut;
	}
	public void setRegionMut(String regionMut) {
		this.regionMut = regionMut;
	}
	public String getTypeRegion() {
		return typeRegion;
	}
	public void setTypeRegion(String typeRegion) {
		this.typeRegion = typeRegion;
	}
	public String getSeq() {
		return seq;
	}
	public void setSeq(String seq) {
		this.seq = seq;
	}
	public Double getMedian() {
		return median;
	}
	public void setMedian(Double median) {
		this.median = median;
	}
	
	

}
