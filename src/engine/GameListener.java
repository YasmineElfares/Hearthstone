package engine;

import java.util.ArrayList;

import model.cards.Card;
import model.cards.minions.Minion;
import model.heroes.Hero;
import model.heroes.Warlock;

public interface GameListener {
	public void onGameOver();

	public void cardDrawn(Card c , Hero h);

	public void updateField(ArrayList<Minion> field, int i);

	public void updateLivingMinion(ArrayList<Minion> field, int i);

	public void UpdateHero(Hero hero);

	public void OnCardBurned(Card c, Hero h);

	public void Fatigue(Hero hero, int i);

	public void updateCard(Hero hero, Card c);
	public void ExceptionMessage(String message);

	public void soundEffect(String string);

	

	

	public void specialMinionEffect(String string, int i,Hero h);

	public void showexception(String string);

	
}
