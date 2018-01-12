package drools;

import java.util.Date;

public class Exchanges {

	public static final String HOME_CONTROLLER = "HomeController";

	public static final String HELPER_ACTOR = "HelperActor";

	public static final String ACTOR = "Actor";

	public static final boolean SEND = true;

	public static final boolean TELL = true;

	public static final String ASK_FOR_ACTOR_REF = "AskForActorRef";

	public static final String RECEIVED_ACTOR_REF = "ReceivedActorRef";

	public static final String QUERY = "Query";

	public static final String ANSWER = "Answer";

	public static final String REFUSE_TO_ANSWER = "RefuseToAnswer";

	public static final String REFERRAL = "Referral";

	public static final String RESPONSE = "Response";

	private Date date;

	private String actorName;

	private boolean send;

	private String type;

	private boolean akkaTell;

	public Exchanges(Date date, String actorName, boolean send, String type, boolean akkaTell) {
		this.date = date;
		this.actorName = actorName;
		this.send = send;
		this.type = type;
		this.akkaTell = akkaTell;
	}

	public Date getDate() {
		return date;
	}

	public String getActorname() {
		return actorName;
	}

	public boolean isSend() {
		return send;
	}

	public String getType() {
		return type;
	}

	public boolean isAkkaTell() {
		return akkaTell;
	}

	public String getActorName() {
		return actorName;
	}

	public void setActorName(String actorName) {
		this.actorName = actorName;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setSend(boolean send) {
		this.send = send;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setAkkaTell(boolean akkaTell) {
		this.akkaTell = akkaTell;
	}

	@Override
	public String toString() {
		String result = "";

		if (this.date != null) {
			result += this.date + " ";
		}

		if (this.getActorname() != null) {
			result += this.getActorname() + " ";
		}

		if (this.isSend()) {
			result += "SEND" + " ";
		} else {
			result += "RECV" + " ";
		}

		if (this.getType() != null) {
			result += this.getType() + " ";
		}

		if (this.isAkkaTell()) {
			result += "NoResponseExpected" + " ";
		}

		return result;
	}
}
