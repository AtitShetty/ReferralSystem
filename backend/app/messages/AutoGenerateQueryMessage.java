package messages;

public class AutoGenerateQueryMessage implements QueryGenerationMessage {
	public final int numberOfQueries;

	public AutoGenerateQueryMessage(int numberOfQueries) {
		this.numberOfQueries = numberOfQueries;
	}
}
