package messages;

public class CustomQueryMessage implements QueryGenerationMessage {
	public final double[] query;

	public CustomQueryMessage(double[] query) {
		this.query = query;
	}
}
