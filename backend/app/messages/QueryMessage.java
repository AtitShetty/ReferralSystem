package messages;

public class QueryMessage implements QueryingMessage {
	public double[] query;

	public String parentName;

	public QueryMessage(double[] query, String parentName) {
		this.query = query;
		this.parentName = parentName;
	}
}
