package messages;

public class RefusalToAnswerMessage implements QueryingMessage {
	public final String message;

	public RefusalToAnswerMessage(String message) {
		this.message = message;
	}
}
