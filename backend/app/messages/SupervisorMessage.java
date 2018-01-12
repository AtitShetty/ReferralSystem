package messages;

import java.util.SortedSet;

public class SupervisorMessage {
	public final int numberOfIterations;

	public final SortedSet<String> peers;

	public SupervisorMessage(int numberOfIterations, SortedSet<String> peers) {
		this.numberOfIterations = numberOfIterations;
		this.peers = peers;
	}
}
