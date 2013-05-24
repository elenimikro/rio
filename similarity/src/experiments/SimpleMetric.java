package experiments;

public class SimpleMetric<M> {

	private final M value;
	private final String name;

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

	@Override
	public String toString() {
		String toReturn = name + ": " + value.toString();
		return toReturn;
	}

}
