package referral_helper;

import java.util.Comparator;

import actors.Peer.Acquaintance;

public class PeerComparator implements Comparator<Acquaintance> {

	@Override
	public int compare(Acquaintance o1, Acquaintance o2) {

		double score1 = ReferralUtil.getFitnessScore(Utils.getWeightOfSociability(), o1.query, o1.sociability,
				o1.expertise);

		double score2 = ReferralUtil.getFitnessScore(Utils.getWeightOfSociability(), o2.query, o2.sociability,
				o2.expertise);

		if (score1 > score2) {
			return 1;
		} else if (score1 < score2) {
			return -1;
		}
		return 0;
	}

}
