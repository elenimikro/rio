package experiments;


public class SimpleMetric<M>{
	
	private M value;
	private String name;

	public SimpleMetric(String name, M value) {
		this.value = value;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public M getValue() {
		return value;
	}

}
