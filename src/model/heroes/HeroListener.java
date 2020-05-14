package model.heroes;

import java.util.ArrayList;

import exceptions.FullHandException;
import model.cards.Card;
import model.cards.minions.Minion;

public interface HeroListener {
	public void onHeroDeath();

	public void damageOpponent(int amount);

	public void endTurn() throws FullHandException, CloneNotSupportedException;


	public void onCardDrawn(Card c, Hero h);

	public void onFieldUpated(ArrayList<Minion> field, int i);

	public void onLivingMinionChange(ArrayList<Minion> field, int i);

	public void onHeroUpdated(Hero hero);

	public void burnCard(Card c, Hero hero);

	public void OnFatigue(Hero hero, int fatigueDamage);

	public void OnUpdateCard(Hero hero, Card c);

	

	public void specialMinion(String m, int i, Hero hero);

	public void exceptions(String string);

	


}
