package messages;

public class AnswerMessage implements QueryingMessage {
	public final boolean isReferral;

	public final double[] answer;

	public final String referral;

	public AnswerMessage(boolean isReferral, double[] answer, String referral) {
		this.isReferral = isReferral;
		this.answer = answer;
		this.referral = referral;
	}
}
