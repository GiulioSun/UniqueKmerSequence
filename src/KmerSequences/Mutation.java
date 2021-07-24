package KmerSequences;

public class Mutation {

	private String genome;
	private int position1;
	private int position2;
	private String sostitution1;
	private String sostitution2;


	public Mutation(String genome, int position1, int position2, String sostitution1, String sostitution2) {
		super();
		this.genome = genome;
		this.position1 = position1;
		this.position2 = position2;
		this.sostitution1 = sostitution1;
		this.sostitution2 = sostitution2;
	}

	public String getGenome() {
		return genome;
	}
	public void setGenome(String genome) {
		this.genome = genome;
	}
	public int getPosition1() {
		return position1;
	}
	public void setPosition1(int position1) {
		this.position1 = position1;
	}
	public int getPosition2() {
		return position2;
	}
	public void setPosition2(int position2) {
		this.position2 = position2;
	}
	public String getSostitution1() {
		return sostitution1;
	}
	public void setSostitution1(String sostitution1) {
		this.sostitution1 = sostitution1;
	}
	public String getSostitution2() {
		return sostitution2;
	}
	public void setSostitution2(String sostitution2) {
		this.sostitution2 = sostitution2;
	}




}
