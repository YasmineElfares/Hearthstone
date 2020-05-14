package model.cards.minions;

public interface MinionListener {
	public void onMinionDeath(Minion m);

	public void onLivingMinionUpdated(Minion minion);

	

}
