package engine;

import java.util.ArrayList;

import exceptions.CannotAttackException;
import exceptions.FullFieldException;
import exceptions.FullHandException;
import exceptions.HeroPowerAlreadyUsedException;
import exceptions.InvalidTargetException;
import exceptions.NotEnoughManaException;
import exceptions.NotSummonedException;
import exceptions.NotYourTurnException;
import exceptions.TauntBypassException;
import model.cards.Card;
import model.cards.minions.Minion;
import model.heroes.Hero;
import model.heroes.HeroListener;
import model.heroes.Warlock;

public class Game implements ActionValidator, HeroListener {
	private Hero firstHero;
	private Hero secondHero;
	private Hero currentHero;
	private Hero opponent;

	private GameListener listener;


	public Game(Hero p1, Hero p2) throws FullHandException, CloneNotSupportedException {
		firstHero = p1;
		secondHero = p2;
		firstHero.setListener(this);
		secondHero.setListener(this);
		firstHero.setValidator(this);
		secondHero.setValidator(this);
		int coin = (int) (Math.random() * 2);
		currentHero = coin == 0 ? firstHero : secondHero;
		opponent = currentHero == firstHero ? secondHero : firstHero;
		currentHero.setCurrentManaCrystals(1);
		currentHero.setTotalManaCrystals(1);
		for (int i = 0; i < 3; i++) {
			currentHero.drawCard();
		}
		for (int i = 0; i < 4; i++) {
			opponent.drawCard();
		}
	}

	@Override
	public void validateTurn(Hero user) throws NotYourTurnException {
		if (user == opponent) {
			listener.ExceptionMessage("You can not do any action in your opponent's turn");
			throw new NotYourTurnException("You can not do any action in your opponent's turn");
			
		}
	}

	public void validateAttack(Minion a, Minion t)
			throws TauntBypassException, InvalidTargetException, NotSummonedException, CannotAttackException {
		if (a.getAttack() <= 0) {
			listener.ExceptionMessage("This minion Cannot Attack");
			throw new CannotAttackException("This minion Cannot Attack");
			}
		if (a.isSleeping()) {
			listener.ExceptionMessage("Give this minion a turn to get ready");
			throw new CannotAttackException("Give this minion a turn to get ready");}
		if (a.isAttacked()) {
			listener.ExceptionMessage("This minion has already attacked");
			throw new CannotAttackException("This minion has already attacked");}
		if (!currentHero.getField().contains(a)) {
			listener.ExceptionMessage("You can not attack with a minion that has not been summoned yet");
			throw new NotSummonedException("You can not attack with a minion that has not been summoned yet");}
		if (currentHero.getField().contains(t)) {
			listener.ExceptionMessage("You can not attack a friendly minion");
			throw new InvalidTargetException("You can not attack a friendly minion");}
		if (!opponent.getField().contains(t)) {
			listener.ExceptionMessage("You can not attack a minion that your opponent has not summoned yet");
			throw new NotSummonedException("You can not attack a minion that your opponent has not summoned yet");}
		if (!t.isTaunt()) {
			for (int i = 0; i < opponent.getField().size(); i++) {
				if (opponent.getField().get(i).isTaunt()) {
					listener.ExceptionMessage("A minion with taunt is in the way");
					throw new TauntBypassException("A minion with taunt is in the way");}
			}
			
		}
		listener.soundEffect("attack.wav");

	}

	public void validateAttack(Minion m, Hero t)
			throws TauntBypassException, NotSummonedException, InvalidTargetException, CannotAttackException {
		if (m.getAttack() <= 0) {
			listener.ExceptionMessage("This minion Cannot Attack");
			throw new CannotAttackException("This minion Cannot Attack");}
		if (m.isSleeping()) {
			listener.ExceptionMessage("Give this minion a turn to get ready");
			throw new CannotAttackException("Give this minion a turn to get ready");}
		if (m.isAttacked()) {
			listener.ExceptionMessage("This minion has already attacked");
			throw new CannotAttackException("This minion has already attacked");}
		if (!currentHero.getField().contains(m)) {
			listener.ExceptionMessage("You can not attack with a minion that has not been summoned yet");
			throw new NotSummonedException("You can not attack with a minion that has not been summoned yet");}
		if (t.getField().contains(m)) {
			listener.ExceptionMessage("You can not attack yourself with your minions");
			throw new InvalidTargetException("You can not attack yourself with your minions");}
		for (int i = 0; i < opponent.getField().size(); i++) {
			if (opponent.getField().get(i).isTaunt()) {
				listener.ExceptionMessage("A minion with taunt is in the way");
				throw new TauntBypassException("A minion with taunt is in the way");}
		}
		listener.soundEffect("attack.wav");
	}

	public void validateManaCost(Card c) throws NotEnoughManaException {
		if (currentHero.getCurrentManaCrystals() < c.getManaCost()) {
			listener.ExceptionMessage("I don't have enough mana !!");
			throw new NotEnoughManaException("I don't have enough mana !!");}
		listener.soundEffect("playCard.wav");
	}

	public void validatePlayingMinion(Minion m) throws FullFieldException {
		if (currentHero.getField().size() == 7) {
			listener.ExceptionMessage("No space for this minion");
			throw new FullFieldException("No space for this minion");}
		listener.soundEffect("playCard.wav");
		
	}
	public void validateUsingHeroPower(Hero h) throws NotEnoughManaException, HeroPowerAlreadyUsedException {
		if (h.getCurrentManaCrystals() < 2) {
			listener.ExceptionMessage("I don't have enough mana !!");
			throw new NotEnoughManaException("I don't have enough mana !!");}
		if (h.isHeroPowerUsed()) {
			listener.ExceptionMessage(" I already used my hero power");
			throw new HeroPowerAlreadyUsedException(" I already used my hero power");}
		listener.soundEffect("heroPower.wav");
	}

	@Override
	public void onHeroDeath() {

		listener.onGameOver();
		

	}

	@Override
	public void damageOpponent(int amount) {

		opponent.setCurrentHP(opponent.getCurrentHP() - amount);
	}

	public Hero getCurrentHero() {
		return currentHero;
	}

	public void setListener(GameListener listener) {
		this.listener = listener;
	}

	@Override
	public void endTurn() throws FullHandException, CloneNotSupportedException {
		Hero temp = currentHero;
		currentHero = opponent;
		opponent = temp;
		currentHero.setTotalManaCrystals(currentHero.getTotalManaCrystals() + 1);
		currentHero.setCurrentManaCrystals(currentHero.getTotalManaCrystals());
		currentHero.setHeroPowerUsed(false);
		for (Minion m : currentHero.getField()) {
			m.setAttacked(false);
			m.setSleeping(false);
		}
		currentHero.drawCard();
		listener.soundEffect("endTurn.wav");

	}
	//	public void onCardDrawn(Hero x) {
	//		listener.cardDrawn(x);
	//	}

	public Hero getOpponent() {
		return opponent;
	}

	@Override
	public void onCardDrawn(Card c , Hero h) {

		if (listener != null) {
			listener.cardDrawn(c , h);
			listener.soundEffect("drawCard.wav");
		}


	}

	@Override
	public void onFieldUpated(ArrayList<Minion> field, int i) {
		if (listener != null){
			
			listener.updateField(field,i);
		}
		
	}

	@Override
	public void onLivingMinionChange(ArrayList<Minion> field, int i) {
		listener.updateLivingMinion(field, i);
		
		
	}

	@Override
	public void onHeroUpdated(Hero hero) {
		listener.UpdateHero(hero);
		
	}

	@Override
	public void burnCard(Card c,Hero h) {
	listener.OnCardBurned(c,h);
	listener.soundEffect("fatigue.wav");
	}

	@Override
	public void OnFatigue(Hero hero,int i) {
		listener.Fatigue(hero,i);
		
	}

	@Override
	public void OnUpdateCard(Hero hero, Card c) {
		listener.updateCard(hero,c);
	}

	@Override
	public void specialMinion(String m,int i, Hero hero) {
		listener.specialMinionEffect(m,i,hero);
		
	}
	@Override
	public void exceptions(String string) {
		// TODO Auto-generated method stub
		listener.showexception(string);
		
	}

}
