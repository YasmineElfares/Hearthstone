package controller;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import engine.Game;
import engine.GameListener;
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
import model.cards.Rarity;
import model.cards.minions.Icehowl;
import model.cards.minions.Minion;
import model.cards.spells.AOESpell;
import model.cards.spells.FieldSpell;
import model.cards.spells.HeroTargetSpell;
import model.cards.spells.LeechingSpell;
import model.cards.spells.MinionTargetSpell;
import model.cards.spells.Spell;
import model.heroes.*;
import view.HearthstoneView;

public class HearthstoneController implements ActionListener, GameListener{

	//Instance variables
	private HearthstoneView view;
	private Game model;
	private Hero selectedHero1;
	private Hero selectedHero2;
	private JButton cardplayed ;
	ArrayList<JButton> Ahandbuttons = new ArrayList();
	ArrayList<JButton> Bhandbuttons = new ArrayList();
	ArrayList<JButton> AFieldButtons = new ArrayList();
	ArrayList<JButton> BFieldButtons = new ArrayList();
	private JButton selectedTarget;



	//Getters & Setters
	public HearthstoneView getView() {
		return view;
	}
	public void setView(HearthstoneView view) {
		this.view = view;
	}
	public Game getModel() {
		return model;
	}
	public void setModel(Game model) {
		this.model = model;
	}

	//Constructor
	AudioInputStream audioIn;
	Clip welcomeMusic;
	Clip Background;
	
	public HearthstoneController() throws FullHandException, CloneNotSupportedException, IOException {

		view = new HearthstoneView();
		try {
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File("src/Audio/welcomeMusic.wav").getAbsoluteFile());
			welcomeMusic = AudioSystem.getClip();
			welcomeMusic.open(audioIn);
			welcomeMusic.start();
		
		
		
		view.getWelcome().revalidate();
		view.getWelcome().repaint();
		view.revalidate();
		view.repaint();
		
		this.playSound("src/Audio/intro.wav");
		for(int i = 0; i<5 ; i++) {
			view.getWelcome1().get(i).addActionListener(this);
			view.getWelcome2().get(i).addActionListener(this);

		}
		view.getEnd().addActionListener(this);
		} catch (UnsupportedAudioFileException | LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	
	//Main
	public static void main(String[] args) throws FullHandException, CloneNotSupportedException, IOException {
		HearthstoneController x = new HearthstoneController();

	}

	public void onGameOver() {
		Background.stop();
		soundEffect("gameOver");
		if(selectedHero1.getCurrentHP()<= 0) {
			view.getAPortrait().removeAll();
			view.getAPortrait().setIcon(new ImageIcon("src/images/Heroes/Heroes/GameHeroes/destroyed.png"));
			JOptionPane.showMessageDialog(view, "Player 2 is the Winner!");
			
			view.dispatchEvent(new WindowEvent(view, WindowEvent.WINDOW_CLOSING));
		}
		if(selectedHero2.getCurrentHP()<= 0) {
			view.getBPortrait().removeAll();
			view.getBPortrait().setIcon(new ImageIcon("src/images/Heroes/Heroes/GameHeroes/destroyed.png"));
			JOptionPane.showMessageDialog(view, "Player 1 is the Winner!");
			
			view.dispatchEvent(new WindowEvent(view, WindowEvent.WINDOW_CLOSING));
		}
		
	}

	public void cardDrawn(Card c , Hero h) {


		cardplayed = new JButton();


		if(c instanceof Minion) {
			Minion m = (Minion) c;
			String div;
			if(m.isDivine()) {
				div = "DivineShield";
			}
			else {
				div = "";
			}
			String taunt;
			if(m.isTaunt()) {
				taunt = "Taunt";
			}
			else {
				taunt = "";
			}
			String sleeping;

			if(m.isSleeping()) {
				sleeping = "Sleeping";
			}
			else {
				sleeping = "Ready To Attack";
			}

			cardplayed.setIcon(new ImageIcon("src/images/Cards/"+c.getName()+" copy.png"));
			cardplayed.setSize(98,135);
			cardplayed.setToolTipText(m.getName()+ " \n ManaCost:" + m.getManaCost() + " \n" + m.getRarity() + " \n Attack:" + 
					m.getAttack() + " \n HP:" + m.getCurrentHP() + " \n" + div + " \n" + 
					taunt + " \n" + sleeping + " \n");
		}
		else {
			Spell s = (Spell) c;
			if(s.getName()=="Shadow Word: Death")
				cardplayed.setIcon(new ImageIcon("src/images/Cards/Shadow Word Death copy.png"));
			else
				cardplayed.setIcon(new ImageIcon("src/images/Cards/"+c.getName()+" copy.png"));
			cardplayed.setSize(90,135);
			cardplayed.setToolTipText(s.getName() + " \n ManaCost:" + s.getManaCost() + " \n" + s.getRarity() + " \n");

		}
		if (h==selectedHero1) {
			Ahandbuttons.add(cardplayed);

			cardplayed.setVisible(true);
			view.getACards().add(cardplayed);
		}
		else {

			Bhandbuttons.add(cardplayed);
			cardplayed.setVisible(true);
			view.getBCards().add(cardplayed);
		}
		cardplayed.addActionListener(this);
		cardplayed.setContentAreaFilled(false); 
		cardplayed.setFocusPainted(false); 
		cardplayed.setOpaque(false);
		
		//}
		view.revalidate();
		view.repaint();
	}

	boolean TargetAquired = false;
	boolean TargetClicked = false;
	public void actionPerformed(ActionEvent e){ 
		JButton clicked = (JButton) e.getSource();


		//picking a target

		if(TargetClicked == true) {
			
			selectedTarget = clicked;
			if (clicked.getText()== "END TURN!") {
				JOptionPane.showMessageDialog(view, "You can't target the end turn, silly!");
				TargetClicked = false  ;
				clicked = null ;
			}
			else{	
			TargetAquired=true;
			TargetClicked = false;
			view.getTarget().setText("Your Target is: "+ selectedTarget.getToolTipText());
			}
		}
		if (clicked!=null) {
		if(clicked.getActionCommand() == "Pick Your Target!") {
			TargetAquired=false;
			TargetClicked = true;
			view.getTarget().setText("Pick Your Target!");


		}
		}


		//welcome
		if(view.getWelcome1().contains(clicked)) {
			String s1 = e.getActionCommand();
			switch (s1){
			case "Mage":
				try {
					selectedHero1 = new Mage();
				} catch (IOException | CloneNotSupportedException e5) {
					// TODO Auto-generated catch block
					e5.printStackTrace();
				}break;
			case "Paladin":
				try {
					selectedHero1 = new Paladin();
				} catch (IOException | CloneNotSupportedException e4) {
					// TODO Auto-generated catch block
					e4.printStackTrace();
				}break;
			case "Hunter":
				try {
					selectedHero1 = new Hunter();
				} catch (IOException | CloneNotSupportedException e3) {
					// TODO Auto-generated catch block
					e3.printStackTrace();
				}break;
			case "Priest":
				try {
					selectedHero1 = new Priest();
				} catch (IOException | CloneNotSupportedException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}break;
			case "Warlock":
				try {
					selectedHero1 = new Warlock();
				} catch (IOException | CloneNotSupportedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}break;

			}

			//clicked.setIcon(null);
		}

		if(view.getWelcome2().contains(clicked)) {
			String s1 = e.getActionCommand();
			switch (s1){
			case "Mage":
				try {
					selectedHero2 = new Mage();
				} catch (IOException | CloneNotSupportedException e5) {
					// TODO Auto-generated catch block
					e5.printStackTrace();
				}break;
			case "Paladin":
				try {
					selectedHero2 = new Paladin();
				} catch (IOException | CloneNotSupportedException e4) {
					// TODO Auto-generated catch block
					e4.printStackTrace();
				}break;
			case "Hunter":
				try {
					selectedHero2 = new Hunter();
				} catch (IOException | CloneNotSupportedException e3) {
					// TODO Auto-generated catch block
					e3.printStackTrace();
				}break;
			case "Priest":
				try {
					selectedHero2 = new Priest();
				} catch (IOException | CloneNotSupportedException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}break;
			case "Warlock":
				try {
					selectedHero2 = new Warlock();
				} catch (IOException | CloneNotSupportedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}break;

			}
		}

		//Initializing a new game
		if(selectedHero1 != null && selectedHero2 != null && model == null) {
			view.getWelcome().setVisible(false);
			view.setVisible(true);
			
			try {
				view.getHeroPowerA().addActionListener(this);
				view.getHeroPowerB().addActionListener(this);
				view.getAPortrait().addActionListener(this);
				view.getBPortrait().addActionListener(this);
				
				view.getAPortrait().setToolTipText(selectedHero1.getName());
				view.getBPortrait().setToolTipText(selectedHero2.getName());
				
				model = new Game(selectedHero1,selectedHero2);
				model.setListener(this);
				view.getTarget().addActionListener(this);
				view.getTarget().setActionCommand("Pick Your Target!");


				if(selectedHero1 == model.getCurrentHero()) {
					view.getAname().setText(selectedHero1.getName() + "\n Your Turn!");
					view.getBname().setText(selectedHero2.getName() + "\n Opponent's Turn!");
				}

				if(selectedHero2 == model.getCurrentHero()) {
					view.getBname().setText(selectedHero2.getName() + "\n Your Turn!");
					view.getAname().setText(selectedHero1.getName() + "\n Opponent's Turn!");

				}



				for(int i = 0; i<3; i++) {

					JButton b = new JButton();
					//					b.setPreferredSize(new Dimension(120,130));
					//					b.setMargin(new Insets(0, 0, 0, 0));
					Hero h = model.getCurrentHero();
					Card c = (Card)h.getHand().get(i);
					if(c instanceof Minion) {
						Minion m = (Minion) c;
						String div;
						if(m.isDivine()) {
							div = "DivineShield";
						}
						else {
							div = "";
						}
						String taunt;
						if(m.isTaunt()) {
							taunt = "Taunt";
						}
						else {
							taunt = "";
						}
						String sleeping;
						if(m.isSleeping()) {
							sleeping = "Sleeping";
						}
						else {
							sleeping = "Ready To Attack";
						}
						b.setIcon(new ImageIcon("src/images/Cards/"+c.getName()+" copy.png"));
						b.setSize(98,135);
						b.setToolTipText(m.getName()+ " \n ManaCost:" + m.getManaCost() + " \n" + m.getRarity() + " \n Attack:" + 
								m.getAttack() + " \n HP:" + m.getCurrentHP() + " \n" + div + " \n" + 
								taunt + " \n" + sleeping + " \n");
					}
					else {
						Spell s = (Spell) c;
						if(s.getName()=="Shadow Word: Death")
							b.setIcon(new ImageIcon("src/images/Cards/Shadow Word Death copy.png"));
						else
							b.setIcon(new ImageIcon("src/images/Cards/"+c.getName()+" copy.png"));
						b.setSize(98,135);
						b.setToolTipText(s.getName() + " \n ManaCost:" + s.getManaCost() + " \n" + s.getRarity() + " \n");

					}
					b.addActionListener(this);
					b.setContentAreaFilled(false); 
					b.setFocusPainted(false); 
					b.setOpaque(false);
				

					if (h== selectedHero1) { // bs b3rf mkano (al t7t y3ni)
						view.getACards().add(b); 
						Ahandbuttons.add(b);
					}
					if (h== selectedHero2) { // dah makano al fo2
						view.getBCards().add(b); 
						Bhandbuttons.add(b);
					}

					view.revalidate();
					view.repaint();
				}


				for(int i = 0; i<4; i++) {
					JButton b = new JButton();
					b.setSize(70,100);
					Hero h= model.getOpponent() ;
					Card c = (Card)h.getHand().get(i);
					if(c instanceof Minion) {
						Minion m = (Minion) c;
						String div;
						if(m.isDivine()) {
							div = "DivineShield";
						}
						else {
							div = "";
						}
						String taunt;
						if(m.isTaunt()) {
							taunt = "Taunt";
						}
						else {
							taunt = "";
						}
						String sleeping;
						if(m.isSleeping()) {
							sleeping = "Sleeping";
						}
						else {
							sleeping = "Ready To Attack";
						}
						b.setIcon(new ImageIcon("src/images/Cards/"+c.getName()+" copy.png"));
						b.setSize(98,135);
						b.setToolTipText(m.getName()+ " \n ManaCost:" + m.getManaCost() + " \n" + m.getRarity() + " \n Attack:" + 
								m.getAttack() + " \n HP:" + m.getCurrentHP() + " \n" + div + " \n" + 
								taunt + " \n" + sleeping + " \n");
					}
					else {
						Spell s = (Spell) c;
						if(s.getName()=="Shadow Word: Death")
							b.setIcon(new ImageIcon("src/images/Cards/Shadow Word Death copy.png"));
						else
							b.setIcon(new ImageIcon("src/images/Cards/"+c.getName()+" copy.png"));
						b.setSize(98,135);
						b.setToolTipText(s.getName() + " \n ManaCost:" + s.getManaCost() + " \n" + s.getRarity() + " \n");

					}
					b.addActionListener(this);
					b.setContentAreaFilled(false); 
					b.setFocusPainted(false); 
					b.setOpaque(false);
					
					if (h== selectedHero1) { // bs b3rf mkano (al t7t y3ni)
						view.getACards().add(b); 
						Ahandbuttons.add(b);
					}
					if (h== selectedHero2) { // dah makano al fo2
						view.getBCards().add(b); 
						Bhandbuttons.add(b);
					}
					view.revalidate();
					view.repaint();

				}


				view.getAcardsleft().setText("Cards Left In Deck: "+ selectedHero1.getDeck().size() + "\n Cards in Hand: " + selectedHero1.getHand().size());
				view.getBcardsleft().setText("Cards Left In Deck: "+ selectedHero2.getDeck().size() + "\n Cards in Hand: " + selectedHero2.getHand().size());

				view.getAhpMana().setText("HP: "+ selectedHero1.getCurrentHP()+"\n"+
						"Mana Crystals: "+selectedHero1.getCurrentManaCrystals() + "/"+ selectedHero1.getTotalManaCrystals());
				view.getBhpMana().setText("HP: "+ selectedHero2.getCurrentHP()+"\n"+
						"Mana Crystals: "+selectedHero2.getCurrentManaCrystals() + "/"+ selectedHero2.getTotalManaCrystals());


				view.getAhpMana().setText("HP: "+ selectedHero1.getCurrentHP()+"\n"+
						"Mana Crystals: "+selectedHero1.getCurrentManaCrystals() + "/"+ selectedHero1.getTotalManaCrystals());

				String mana2 = "Mana Crystals: " + selectedHero2.getCurrentManaCrystals() + " / " + 
						selectedHero2.getTotalManaCrystals();
				view.getBhpMana().setText("HP: "+ selectedHero2.getCurrentHP()+"\n"+
						"Mana Crystals: "+selectedHero2.getCurrentManaCrystals() + "/"+ selectedHero2.getTotalManaCrystals());

				String loc1 = "src/images/Heroes/Heroes/GameHeroes/" + selectedHero1.getName()+ ".png";
				String loc2 = "src/images/Heroes/Heroes/GameHeroes/" + selectedHero2.getName()+ ".png";
				JLabel h1 = new JLabel();
				h1.setVisible(true);
				view.getAPortrait().add(h1);
				view.getAPortrait().setText(selectedHero1.getName());
				view.getAPortrait().addActionListener(this);
				view.getAPortrait().revalidate();
				view.getAPortrait().repaint();
				h1.setIcon(new ImageIcon(loc1));
				JLabel h2 = new JLabel();
				h2.setVisible(true);
				view.getBPortrait().add(h2);
				view.getBPortrait().setText(selectedHero2.getName());
				h2.setIcon(new ImageIcon(loc2));
				view.getBPortrait().addActionListener(this);
				JLabel d1 = new JLabel();
				d1.setVisible(true);
				view.getADeck().add(d1);
				d1.setIcon(new ImageIcon("src/images/deck.png"));
				view.getHeroPowerA().setIcon(new ImageIcon("src/images/HeroPowers/"+selectedHero1.getName()+".png"));
				view.getHeroPowerB().setIcon(new ImageIcon("src/images/HeroPowers/"+selectedHero2.getName()+".png"));

				JLabel d2 = new JLabel();
				d2.setVisible(true);
				view.getBDeck().add(d2);
				d2.setIcon(new ImageIcon("src/images/deck.png"));
				
			

				view.revalidate();
				view.repaint();
				welcomeMusic.stop();

				AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File("src/Audio/Background.wav").getAbsoluteFile());
				Background = AudioSystem.getClip();
				Background.open(audioIn);
				Background.start();
				
				
				this.soundEffect("intro");

			} catch (FullHandException | CloneNotSupportedException | UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		//--------------------------------------------------------------------------------------------------
		//End Turn
		if ( clicked == (view.getEnd())){
			try {
				model.endTurn();
				for(int i = 0 ; i< AFieldButtons.size(); i++) {
					updateLivingMinion(selectedHero1.getField(),i);
				}
				for(int i = 0 ; i< BFieldButtons.size(); i++) {
					updateLivingMinion(selectedHero2.getField(),i);
				}
				if(selectedHero1 == model.getCurrentHero()) {
					view.getAname().setText(selectedHero1.getName() + "\n Your Turn!");
					view.getBname().setText(selectedHero2.getName() + "\n Opponent's Turn!");
				}

				if(selectedHero2 == model.getCurrentHero()) {
					view.getBname().setText(selectedHero2.getName() + "\n Your Turn!");
					view.getAname().setText(selectedHero1.getName() + "\n Opponent's Turn!");

				}
				
				view.getAhpMana().setText("HP: "+ selectedHero1.getCurrentHP()+"\n"+
						"Mana Crystals: "+selectedHero1.getCurrentManaCrystals() + "/"+ selectedHero1.getTotalManaCrystals());

				String mana2 = "Mana Crystals: " + selectedHero2.getCurrentManaCrystals() + " / " + 
						selectedHero2.getTotalManaCrystals();
				view.getBhpMana().setText("HP: "+ selectedHero2.getCurrentHP()+"\n"+
						"Mana Crystals: "+selectedHero2.getCurrentManaCrystals() + "/"+ selectedHero2.getTotalManaCrystals());
				view.getAcardsleft().setText("Cards Left: "+ selectedHero1.getDeck().size() + "\n Cards in Hand: " + selectedHero1.getHand().size());
				view.getBcardsleft().setText("Cards Left: "+ selectedHero2.getDeck().size() + "\n Cards in Hand: " + selectedHero2.getHand().size());
				//				view.revalidate();
				//				view.repaint();
			} catch (FullHandException | CloneNotSupportedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}



		}


		//Attack A

		if(AFieldButtons.contains(clicked)) {
			if(selectedTarget==null) {

			}
			else {
				Minion attacker =selectedHero1.getField().get(AFieldButtons.indexOf(clicked));
				


				if(selectedTarget == view.getBPortrait()) {
					try {
						selectedHero1.attackWithMinion(attacker, selectedHero2);
						if (attacker.getCurrentHP()>0)
						updateLivingMinion(selectedHero1.getField(), AFieldButtons.indexOf(clicked));

					} catch (CannotAttackException | NotYourTurnException | TauntBypassException | NotSummonedException
							| InvalidTargetException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				else {
					if(BFieldButtons.contains(selectedTarget)) {
						Minion target =selectedHero2.getField().get(BFieldButtons.indexOf(selectedTarget));

						try {
							selectedHero1.attackWithMinion(attacker, target);
							if (attacker.getCurrentHP()>0)
							updateLivingMinion(selectedHero1.getField(), AFieldButtons.indexOf(clicked));
							if (target.getCurrentHP()>0)
							updateLivingMinion(selectedHero2.getField(), BFieldButtons.indexOf(selectedTarget));
							
						} catch (CannotAttackException | NotYourTurnException | TauntBypassException | InvalidTargetException
								| NotSummonedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}

					else {
						if(AFieldButtons.contains(selectedTarget)) {

						}
						else {
							JOptionPane.showMessageDialog(view, "Invalid Target!");
						}
					}

				}
			}
		}
		// Playing A card A
		if ( Ahandbuttons.contains(clicked)) {


			if (model.getCurrentHero() == selectedHero1) {

				int i = Ahandbuttons.indexOf(clicked);
				Card c = model.getCurrentHero().getHand().get(i); // del lma kant opponent kant 8alat

				if (c instanceof Minion) {
					try {
						model.getCurrentHero().playMinion((Minion)c);
						view.getAcardsleft().setText("Cards Left: "+ selectedHero1.getDeck().size() + "\n Cards in Hand: " + selectedHero1.getHand().size());
						Minion m = (Minion) c;

						view.getAMinions().add(clicked);
						view.getACards().remove(clicked);
						Ahandbuttons.remove(clicked);
						AFieldButtons.add(clicked);
						
						
						if(c.getName()=="Prophet Velen") {
							specialMinionEffect(c.getName(),1, selectedHero1);
						}
						if(c.getName()=="Kalycgos") {
							specialMinionEffect(c.getName(),1, selectedHero1);
						}
						view.getAhpMana().setText("HP: "+ selectedHero1.getCurrentHP()+"\n"+
								"Mana Crystals: "+selectedHero1.getCurrentManaCrystals() + "/"+ selectedHero1.getTotalManaCrystals());
						
						String div="";
						String sleeping="";
						String taunt="";
						if(m.isTaunt())
							taunt="Taunt";
						if(m.isDivine())
							div="Divine";
						if(m.isSleeping()==false)
							sleeping="Ready";
						else 
							sleeping="";
						clicked.setText("<html>" +"Atk:" +m.getAttack()+ "<br>"+
								"\nHP:"+m.getCurrentHP()+ "<br>"+ 
								div +"<br>" +
								taunt + "<br>" +
								sleeping +"<br>" +"</html>"); //multiline code from Coderanch
						view.revalidate();
						view.repaint();

					} catch (NotYourTurnException | NotEnoughManaException | FullFieldException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

				if (c instanceof Spell) {
					if(c instanceof AOESpell) {
						view.getAcardsleft().setText("Cards Left: "+ selectedHero1.getDeck().size() + "\n Cards in Hand: " + selectedHero1.getHand().size());

						int index = Ahandbuttons.indexOf(clicked);

						try {
							selectedHero1.castSpell((AOESpell)selectedHero1.getHand().get(index),model.getOpponent().getField());
							Ahandbuttons.remove(clicked);
							view.getACards().remove(clicked);
							view.getAhpMana().setText("HP: "+ selectedHero1.getCurrentHP()+"\n"+
									"Mana Crystals: "+selectedHero1.getCurrentManaCrystals() + "/"+ selectedHero1.getTotalManaCrystals());

						} catch (NotYourTurnException | NotEnoughManaException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

					}
				}
				if(c instanceof FieldSpell) {

					int index = Ahandbuttons.indexOf(clicked);

					try {
						selectedHero1.castSpell((FieldSpell)selectedHero1.getHand().get(index));
						Ahandbuttons.remove(clicked);
						view.getACards().remove(clicked);
						view.getAhpMana().setText("HP: "+ selectedHero1.getCurrentHP()+"\n"+
								"Mana Crystals: "+selectedHero1.getCurrentManaCrystals() + "/"+ selectedHero1.getTotalManaCrystals());

					} catch (NotYourTurnException | NotEnoughManaException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				if(c instanceof LeechingSpell) {
					if(selectedTarget==null) {
						JOptionPane.showMessageDialog(view, "Pick a Target First!");
					}
					else {
						Minion tmp;
						if(AFieldButtons.contains(selectedTarget))
							tmp=selectedHero1.getField().get(AFieldButtons.indexOf(selectedTarget));
						else if(BFieldButtons.contains(selectedTarget))
							tmp=selectedHero2.getField().get(BFieldButtons.indexOf(selectedTarget));
						else {
							JOptionPane.showMessageDialog(view, "Invalid Target!");
							return;}

						try {
							int index = Ahandbuttons.indexOf(clicked);
							selectedHero1.castSpell((LeechingSpell)selectedHero1.getHand().get(index),tmp);
							view.getACards().remove(clicked);
							view.getAhpMana().setText("HP: "+ selectedHero1.getCurrentHP()+"\n"+
									"Mana Crystals: "+selectedHero1.getCurrentManaCrystals() + "/"+ selectedHero1.getTotalManaCrystals());
							Ahandbuttons.remove(clicked);

						} catch (NotYourTurnException | NotEnoughManaException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}

				}
				if(c instanceof MinionTargetSpell) {
					if(selectedTarget==null) {
						JOptionPane.showMessageDialog(view, "Pick a Target First!");
					}
					else {
						Minion tmp=null;
						if(AFieldButtons.contains(selectedTarget))
							tmp=selectedHero1.getField().get(AFieldButtons.indexOf(selectedTarget));
						else if(BFieldButtons.contains(selectedTarget))
							tmp=selectedHero2.getField().get(BFieldButtons.indexOf(selectedTarget));
						else {
							if (!(c instanceof HeroTargetSpell))
							JOptionPane.showMessageDialog(view, "Invalid Target!");

						}
						if(tmp!=null) {
							try {
								int index = Ahandbuttons.indexOf(clicked);
								selectedHero1.castSpell((MinionTargetSpell)selectedHero1.getHand().get(index),tmp);
								view.getACards().remove(clicked);
								Ahandbuttons.remove(clicked);
								view.getAhpMana().setText("HP: "+ selectedHero1.getCurrentHP()+"\n"+
										"Mana Crystals: "+selectedHero1.getCurrentManaCrystals() + "/"+ selectedHero1.getTotalManaCrystals());
								view.revalidate();
								view.repaint();
								return;

							} catch (NotYourTurnException | NotEnoughManaException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (InvalidTargetException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}

					}

				}
				if(c instanceof HeroTargetSpell) {
					if(selectedTarget==null) {
						JOptionPane.showMessageDialog(view, "Pick a Target First!");
					}
					else {
						Hero tmp;
						if(selectedTarget==view.getAPortrait()) {
							tmp=selectedHero1;
							//System.out.println("awel if");
						}
						else {if(selectedTarget==view.getBPortrait()) {
							tmp=selectedHero2;
							//System.out.println("awel else");
						}
						else {
							//System.out.println("tany else");
							if (!(c instanceof MinionTargetSpell))
							JOptionPane.showMessageDialog(view, "Invalid Target!");
							return;
						}
						}
						try {
							int index = Ahandbuttons.indexOf(clicked);
							selectedHero1.castSpell((HeroTargetSpell)selectedHero1.getHand().get(index),tmp);
							view.getACards().remove(clicked);
							Ahandbuttons.remove(clicked);
							view.getAhpMana().setText("HP: "+ selectedHero1.getCurrentHP()+"\n"+
									"Mana Crystals: "+selectedHero1.getCurrentManaCrystals() + "/"+ selectedHero1.getTotalManaCrystals());
							view.revalidate();
							view.repaint();
							return;

						} catch (NotYourTurnException | NotEnoughManaException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} 
					}

				}
			}
			else {
				try {
					model.validateTurn(selectedHero1);
				} catch (NotYourTurnException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		//Use Hero Power A
		if(clicked == view.getHeroPowerA()) {


			if(selectedHero1 instanceof Paladin) {
				try {
					selectedHero1.useHeroPower();
					Minion m = new Minion("Silver Hand Recruit", 1, Rarity.BASIC, 1, 1, false, false, false);
					String div;
					if(m.isDivine()) {
						div = "DivineShield";
					}
					else {
						div = "";
					}
					String taunt;
					if(m.isTaunt()) {
						taunt = "Taunt";
					}
					else {
						taunt = "";
					}
					String sleeping;
					if(m.isSleeping()) {
						sleeping = "Sleeping";
					}
					else {
						sleeping = "Ready To Attack";
					}

					JButton b = new JButton();
					b.setIcon(new ImageIcon("src/images/Cards/"+m.getName()+" copy.png"));
					b.setSize(98,135);
					b.setToolTipText(m.getName()+ " \n ManaCost:" + m.getManaCost() + " \n" + m.getRarity() + " \n Attack:" + 
							m.getAttack() + " \n HP:" + m.getCurrentHP() + " \n" + div + " \n" + 
							taunt + " \n" + sleeping + " \n");
					
					b.addActionListener(this);
					b.setContentAreaFilled(false); 
					b.setFocusPainted(false); 
					b.setOpaque(false);
					AFieldButtons.add(b);
					view.getAMinions().add(b);
					
					if(div=="DivineShield")
						div="Divine";
					if(sleeping=="Ready To Attack")
						sleeping="Ready";
					else 
						sleeping="";
					b.setText("<html>" +"Atk:" +m.getAttack()+ "<br>"+
							"\nHP:"+m.getCurrentHP()+ "<br>"+ 
							div +"<br>" +
							taunt + "<br>" +
							sleeping +"<br>" +"</html>"); //multiline code from Coderanch

				} catch (NotEnoughManaException | HeroPowerAlreadyUsedException | NotYourTurnException
						| FullHandException | FullFieldException | CloneNotSupportedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}				
			}


			if(selectedHero1 instanceof Warlock) {
				try {
					selectedHero1.useHeroPower();

				} catch (NotEnoughManaException | HeroPowerAlreadyUsedException | NotYourTurnException
						| FullHandException | FullFieldException | CloneNotSupportedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}				
			}

			if(selectedHero1 instanceof Hunter) {
				try {
					selectedHero1.useHeroPower();

				} catch (NotEnoughManaException | HeroPowerAlreadyUsedException | NotYourTurnException
						| FullHandException | FullFieldException | CloneNotSupportedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}				
			}
			if(selectedHero1 instanceof Mage) {
				if(selectedTarget==null) {
					JOptionPane.showMessageDialog(view, "Pick a Target First!");
				}
				else {

					if(selectedTarget == view.getBPortrait()) {
						try {

							((Mage)selectedHero1).useHeroPower(selectedHero2);

						} catch (NotYourTurnException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (NotEnoughManaException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (HeroPowerAlreadyUsedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (FullHandException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (FullFieldException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (CloneNotSupportedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					if(selectedTarget == view.getAPortrait()) {
						try {

							((Mage)selectedHero1).useHeroPower(selectedHero1);

						} catch (NotYourTurnException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (NotEnoughManaException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (HeroPowerAlreadyUsedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (FullHandException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (FullFieldException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (CloneNotSupportedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					if(AFieldButtons.contains(selectedTarget)) {
						try {
							Minion target = selectedHero1.getField().get(AFieldButtons.indexOf(selectedTarget));
							((Mage)selectedHero1).useHeroPower(target);
							//updateLivingMinion(selectedHero1.getField(), AFieldButtons.indexOf(clicked));

						} catch (NotYourTurnException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (NotEnoughManaException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (HeroPowerAlreadyUsedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (FullHandException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (FullFieldException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (CloneNotSupportedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					if(BFieldButtons.contains(selectedTarget)) {
						try {
							Minion target = selectedHero2.getField().get(BFieldButtons.indexOf(selectedTarget));
							((Mage)selectedHero1).useHeroPower(target);
							//updateLivingMinion(selectedHero1.getField(), AFieldButtons.indexOf(clicked));

						} catch (NotYourTurnException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (NotEnoughManaException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (HeroPowerAlreadyUsedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (FullHandException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (FullFieldException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (CloneNotSupportedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}

				}

			}
			if(selectedHero1 instanceof Priest) {
				if(selectedTarget==null) {
					JOptionPane.showMessageDialog(view, "Pick a Target First!");
				}
				else {

					if(selectedTarget == view.getBPortrait()) {
						try {

							((Priest)selectedHero1).useHeroPower(selectedHero2);

						} catch (NotYourTurnException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (NotEnoughManaException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (HeroPowerAlreadyUsedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (FullHandException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (FullFieldException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (CloneNotSupportedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					if(selectedTarget == view.getAPortrait()) {
						try {

							((Priest)selectedHero1).useHeroPower(selectedHero1);

						} catch (NotYourTurnException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (NotEnoughManaException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (HeroPowerAlreadyUsedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (FullHandException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (FullFieldException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (CloneNotSupportedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					if(AFieldButtons.contains(selectedTarget)) {
						try {
							Minion target = selectedHero1.getField().get(AFieldButtons.indexOf(selectedTarget));
							((Priest)selectedHero1).useHeroPower(target);
							//updateLivingMinion(selectedHero1.getField(), AFieldButtons.indexOf(clicked));

						} catch (NotYourTurnException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (NotEnoughManaException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (HeroPowerAlreadyUsedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (FullHandException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (FullFieldException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (CloneNotSupportedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					if(BFieldButtons.contains(selectedTarget)) {
						try {
							Minion target = selectedHero2.getField().get(BFieldButtons.indexOf(selectedTarget));
							((Priest)selectedHero1).useHeroPower(target);
							//updateLivingMinion(selectedHero1.getField(), AFieldButtons.indexOf(clicked));

						} catch (NotYourTurnException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (NotEnoughManaException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (HeroPowerAlreadyUsedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (FullHandException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (FullFieldException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (CloneNotSupportedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}

				}

			}

			//End of hero Power
			view.getAhpMana().setText("HP: "+ selectedHero1.getCurrentHP()+"\n"+
					"Mana Crystals: "+selectedHero1.getCurrentManaCrystals() + "/"+ selectedHero1.getTotalManaCrystals());

		}


		//Attack B

		if(BFieldButtons.contains(clicked)) {
			if(selectedTarget==null) {

			}
			else {
				Minion attacker =selectedHero2.getField().get(BFieldButtons.indexOf(clicked));
				
				


				if(selectedTarget == view.getAPortrait()) {
					try {
						selectedHero2.attackWithMinion(attacker, selectedHero1);
						if (attacker.getCurrentHP()>0)
						updateLivingMinion(selectedHero2.getField(), BFieldButtons.indexOf(clicked));

					} catch (CannotAttackException | NotYourTurnException | TauntBypassException | NotSummonedException
							| InvalidTargetException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				else {
					if(AFieldButtons.contains(selectedTarget)) {
						Minion target =selectedHero1.getField().get(AFieldButtons.indexOf(selectedTarget));

						try {
							selectedHero2.attackWithMinion(attacker, target);
							if (attacker.getCurrentHP()>0)
							updateLivingMinion(selectedHero2.getField(), BFieldButtons.indexOf(clicked));
							if (target.getCurrentHP()>0)
							updateLivingMinion(selectedHero1.getField(), AFieldButtons.indexOf(selectedTarget));

						} catch (CannotAttackException | NotYourTurnException | TauntBypassException | InvalidTargetException
								| NotSummonedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}

					else {
						if(BFieldButtons.contains(selectedTarget)) {

						}
						else {
							JOptionPane.showMessageDialog(view, "Invalid Target!");
						}
					}

				}
			}
		}
		// Playing A card B
		if ( Bhandbuttons.contains(clicked)) {


			if (model.getCurrentHero() == selectedHero2) {

				int i = Bhandbuttons.indexOf(clicked);
				Card c = model.getCurrentHero().getHand().get(i); // del lma kant opponent kant 8alat

				if (c instanceof Minion) {
					try {
						model.getCurrentHero().playMinion((Minion)c);
						view.getBcardsleft().setText("Cards Left: "+ selectedHero2.getDeck().size() + "\n Cards in Hand: " + selectedHero2.getHand().size());

						Minion m= (Minion) c;
						view.getBMinions().add(clicked);
						view.getBCards().remove(clicked);
						Bhandbuttons.remove(clicked);
						BFieldButtons.add(clicked);
						
						
						if(c.getName()=="Prophet Velen") {
							specialMinionEffect(c.getName(),1, selectedHero2);
						}
						if(c.getName()=="Kalycgos") {
							specialMinionEffect(c.getName(),1, selectedHero2);
						}
						
						view.getBhpMana().setText("HP: "+ selectedHero2.getCurrentHP()+"\n"+
								"Mana Crystals: "+selectedHero2.getCurrentManaCrystals() + "/"+ selectedHero2.getTotalManaCrystals());
						String div="";
						String sleeping="";
						String taunt="";
						if(m.isTaunt())
							taunt="Taunt";
						if(m.isDivine())
							div="Divine";
						if(m.isSleeping()==false)
							sleeping="Ready";
						else 
							sleeping="";
						clicked.setText("<html>" +"Atk:" +m.getAttack()+ "<br>"+
								"\nHP:"+m.getCurrentHP()+ "<br>"+ 
								div +"<br>" +
								taunt  +"<br>" +
								sleeping +"<br>" +"</html>"); //multiline code from Coderanch
						view.revalidate();
						view.repaint();

					} catch (NotYourTurnException | NotEnoughManaException | FullFieldException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

				if (c instanceof Spell) {
					if(c instanceof AOESpell) {
						view.getBcardsleft().setText("Cards Left: "+ selectedHero2.getDeck().size() + "\n Cards in Hand: " + selectedHero2.getHand().size());

						int index = Bhandbuttons.indexOf(clicked);

						try {
							selectedHero2.castSpell((AOESpell)selectedHero2.getHand().get(index),model.getOpponent().getField());
							Bhandbuttons.remove(clicked);
							view.getBCards().remove(clicked);
							view.getBhpMana().setText("HP: "+ selectedHero2.getCurrentHP()+"\n"+
									"Mana Crystals: "+selectedHero2.getCurrentManaCrystals() + "/"+ selectedHero2.getTotalManaCrystals());

						} catch (NotYourTurnException | NotEnoughManaException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

					}
				}
				if(c instanceof FieldSpell) {

					int index = Bhandbuttons.indexOf(clicked);

					try {
						selectedHero2.castSpell((FieldSpell)selectedHero2.getHand().get(index));
						Bhandbuttons.remove(clicked);
						view.getBCards().remove(clicked);
						view.getBhpMana().setText("HP: "+ selectedHero2.getCurrentHP()+"\n"+
								"Mana Crystals: "+selectedHero2.getCurrentManaCrystals() + "/"+ selectedHero2.getTotalManaCrystals());

					} catch (NotYourTurnException | NotEnoughManaException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				if(c instanceof LeechingSpell) {
					if(selectedTarget==null) {
						JOptionPane.showMessageDialog(view, "Pick a Target First!");
					}
					else {
						Minion tmp;
						if(BFieldButtons.contains(selectedTarget))
							tmp=selectedHero2.getField().get(BFieldButtons.indexOf(selectedTarget));
						else if(AFieldButtons.contains(selectedTarget))
							tmp=selectedHero1.getField().get(AFieldButtons.indexOf(selectedTarget));
						else {
							JOptionPane.showMessageDialog(view, "Invalid Target!");
							return;}

						try {
							int index = Bhandbuttons.indexOf(clicked);
							selectedHero2.castSpell((LeechingSpell)selectedHero2.getHand().get(index),tmp);
							view.getBCards().remove(clicked);
							view.getBhpMana().setText("HP: "+ selectedHero2.getCurrentHP()+"\n"+
									"Mana Crystals: "+selectedHero2.getCurrentManaCrystals() + "/"+ selectedHero2.getTotalManaCrystals());
							Bhandbuttons.remove(clicked);

						} catch (NotYourTurnException | NotEnoughManaException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}

				}
				if(c instanceof MinionTargetSpell) {
					if(selectedTarget==null) {
						JOptionPane.showMessageDialog(view, "Pick a Target First!");
					}
					else {
						Minion tmp=null;
						if(BFieldButtons.contains(selectedTarget))
							tmp=selectedHero2.getField().get(BFieldButtons.indexOf(selectedTarget));
						else if(AFieldButtons.contains(selectedTarget))
							tmp=selectedHero1.getField().get(AFieldButtons.indexOf(selectedTarget));
						else {
							if (!(c instanceof HeroTargetSpell))
							JOptionPane.showMessageDialog(view, "Invalid Target!");

						}
						if(tmp!=null) {
							try {
								int index = Bhandbuttons.indexOf(clicked);
								selectedHero2.castSpell((MinionTargetSpell)selectedHero2.getHand().get(index),tmp);
								view.getBCards().remove(clicked);
								Bhandbuttons.remove(clicked);
								view.getBhpMana().setText("HP: "+ selectedHero2.getCurrentHP()+"\n"+
										"Mana Crystals: "+selectedHero2.getCurrentManaCrystals() + "/"+ selectedHero2.getTotalManaCrystals());

								view.revalidate();
								view.repaint();
							} catch (NotYourTurnException | NotEnoughManaException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (InvalidTargetException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}

					}

				}
				if(c instanceof HeroTargetSpell) {
					if(selectedTarget==null) {
						JOptionPane.showMessageDialog(view, "Pick a Target First!");
					}
					else {
						Hero tmp;
						if(selectedTarget==view.getBPortrait()) {
							tmp=selectedHero2;
							//System.out.println("awel if");
						}
						else {if(selectedTarget==view.getAPortrait()) {
							tmp=selectedHero1;
							//System.out.println("awel else");
						}
						else {
							if (!(c instanceof HeroTargetSpell))
							JOptionPane.showMessageDialog(view, "Invalid Target!");
							return;
						}
						}
						try {
							int index = Bhandbuttons.indexOf(clicked);
							selectedHero2.castSpell((HeroTargetSpell)selectedHero2.getHand().get(index),tmp);
							view.getBCards().remove(clicked);
							Bhandbuttons.remove(clicked);
							view.getBhpMana().setText("HP: "+ selectedHero2.getCurrentHP()+"\n"+
									"Mana Crystals: "+selectedHero2.getCurrentManaCrystals() + "/"+ selectedHero2.getTotalManaCrystals());

							view.revalidate();
							view.repaint();
						} catch (NotYourTurnException | NotEnoughManaException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} 
					}

				}
			}
			else {
				try {
					model.validateTurn(selectedHero2);
				} catch (NotYourTurnException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		//Use Hero Power B
		if(clicked == view.getHeroPowerB()) {


			if(selectedHero2 instanceof Paladin) {
				try {
					selectedHero2.useHeroPower();
					Minion m = new Minion("Silver Hand Recruit", 1, Rarity.BASIC, 1, 1, false, false, false);
					String div;
					if(m.isDivine()) {
						div = "DivineShield";
					}
					else {
						div = "";
					}
					String taunt;
					if(m.isTaunt()) {
						taunt = "Taunt";
					}
					else {
						taunt = "";
					}
					String sleeping;
					if(m.isSleeping()) {
						sleeping = "Sleeping";
					}
					else {
						sleeping = "Ready To Attack";
					}

					JButton b = new JButton();
					b.setIcon(new ImageIcon("src/images/Cards/"+m.getName()+" copy.png"));
					b.setSize(98,135);
					b.setToolTipText(m.getName()+ " \n ManaCost:" + m.getManaCost() + " \n" + m.getRarity() + " \n Attack:" + 
							m.getAttack() + " \n HP:" + m.getCurrentHP() + " \n" + div + " \n" + 
							taunt + " \n" + sleeping + " \n");
					
					if(div=="DivineShield")
						div="Divine";
					if(sleeping=="Ready To Attack")
						sleeping="Ready";
					else 
						sleeping="";
					b.setText("<html>" +"Atk:" +m.getAttack()+ "<br>"+
							"\nHP:"+m.getCurrentHP()+ "<br>"+ 
							div +"<br>" +
							taunt + "<br>" +
							sleeping +"<br>" +"</html>"); //multiline code from Coderanch
					
					b.addActionListener(this);
					b.setContentAreaFilled(false); 
					b.setFocusPainted(false); 
					b.setOpaque(false);
					BFieldButtons.add(b);
					view.getBMinions().add(b);

				} catch (NotEnoughManaException | HeroPowerAlreadyUsedException | NotYourTurnException
						| FullHandException | FullFieldException | CloneNotSupportedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}				
			}


			if(selectedHero2 instanceof Warlock) {
				try {
					selectedHero2.useHeroPower();

				} catch (NotEnoughManaException | HeroPowerAlreadyUsedException | NotYourTurnException
						| FullHandException | FullFieldException | CloneNotSupportedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}				
			}

			if(selectedHero2 instanceof Hunter) {
				try {
					selectedHero2.useHeroPower();

				} catch (NotEnoughManaException | HeroPowerAlreadyUsedException | NotYourTurnException
						| FullHandException | FullFieldException | CloneNotSupportedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}				
			}
			if(selectedHero2 instanceof Mage) {
				if(selectedTarget==null) {
					JOptionPane.showMessageDialog(view, "Pick a Target First!");
				}
				else {

					if(selectedTarget == view.getAPortrait()) {
						try {

							((Mage)selectedHero2).useHeroPower(selectedHero1);

						} catch (NotYourTurnException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (NotEnoughManaException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (HeroPowerAlreadyUsedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (FullHandException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (FullFieldException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (CloneNotSupportedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					if(selectedTarget == view.getBPortrait()) {
						try {

							((Mage)selectedHero2).useHeroPower(selectedHero2);

						} catch (NotYourTurnException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (NotEnoughManaException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (HeroPowerAlreadyUsedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (FullHandException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (FullFieldException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (CloneNotSupportedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					if(BFieldButtons.contains(selectedTarget)) {
						try {
							Minion target = selectedHero2.getField().get(BFieldButtons.indexOf(selectedTarget));
							((Mage)selectedHero2).useHeroPower(target);
							

						} catch (NotYourTurnException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (NotEnoughManaException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (HeroPowerAlreadyUsedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (FullHandException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (FullFieldException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (CloneNotSupportedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					if(AFieldButtons.contains(selectedTarget)) {
						try {
							Minion target = selectedHero1.getField().get(AFieldButtons.indexOf(selectedTarget));
							((Mage)selectedHero2).useHeroPower(target);
							

						} catch (NotYourTurnException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (NotEnoughManaException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (HeroPowerAlreadyUsedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (FullHandException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (FullFieldException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (CloneNotSupportedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}

				}

			}
			if(selectedHero2 instanceof Priest) {
				if(selectedTarget==null) {
					JOptionPane.showMessageDialog(view, "Pick a Target First!");
				}
				else {

					if(selectedTarget == view.getAPortrait()) {
						try {

							((Priest)selectedHero2).useHeroPower(selectedHero1);

						} catch (NotYourTurnException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (NotEnoughManaException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (HeroPowerAlreadyUsedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (FullHandException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (FullFieldException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (CloneNotSupportedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					if(selectedTarget == view.getBPortrait()) {
						try {

							((Priest)selectedHero2).useHeroPower(selectedHero2);

						} catch (NotYourTurnException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (NotEnoughManaException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (HeroPowerAlreadyUsedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (FullHandException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (FullFieldException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (CloneNotSupportedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					if(BFieldButtons.contains(selectedTarget)) {
						try {
							Minion target = selectedHero2.getField().get(BFieldButtons.indexOf(selectedTarget));
							((Priest)selectedHero2).useHeroPower(target);
						

						} catch (NotYourTurnException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (NotEnoughManaException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (HeroPowerAlreadyUsedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (FullHandException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (FullFieldException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (CloneNotSupportedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					if(AFieldButtons.contains(selectedTarget)) {
						try {
							Minion target = selectedHero1.getField().get(AFieldButtons.indexOf(selectedTarget));
							((Priest)selectedHero2).useHeroPower(target);
						

						} catch (NotYourTurnException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (NotEnoughManaException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (HeroPowerAlreadyUsedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (FullHandException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (FullFieldException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (CloneNotSupportedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}

				}

			}

			//End of hero Power
			view.getBhpMana().setText("HP: "+ selectedHero2.getCurrentHP()+"\n"+
					"Mana Crystals: "+selectedHero2.getCurrentManaCrystals() + "/"+ selectedHero2.getTotalManaCrystals());

		}

		view.revalidate();
		view.repaint();
	}


	public void soundEffect(String op) {
		String loc= "src/Audio/";
		try {	
		if (op=="intro") {
		
			Thread.sleep(1000);
			this.playSound("src/Audio/"+selectedHero1.getName()+".wav");
			Thread.sleep(1000);
			this.playSound("src/Audio/vs.wav");
			Thread.sleep(1000);
			this.playSound("src/Audio/"+selectedHero2.getName()+".wav");
		}
		else if(op=="gameOver"){
			playSound(loc+"victory.wav");
			playSound(loc+"crack.wav");
			Thread.sleep(500);
			playSound(loc+"explode.wav");
			Thread.sleep(500);
			
		}
		else {
			op= "src/Audio/"+op;
			playSound(op);
			
		}
		
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void playSound(String filePath) {
		try {
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File(filePath).getAbsoluteFile());
			Clip clip = AudioSystem.getClip();
			clip.open(audioIn);
			clip.start();
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void updateField(ArrayList<Minion> field, int i) { //for removing dead minions
		
		if(field == selectedHero1.getField()) {
			soundEffect("minionDeath.wav");
			JButton tmp = AFieldButtons.remove(i);
			view.getAMinions().remove(tmp);

		}
		if(field == selectedHero2.getField()) {
			soundEffect("minionDeath.wav");
			JButton tmp = BFieldButtons.remove(i);
			view.getBMinions().remove(tmp);

		}


	}		
	
	public void updateLivingMinion(ArrayList<Minion> field, int i) {

		if(field == selectedHero1.getField()) {
			Minion m = selectedHero1.getField().get(i);
			String div;
			if(m.isDivine()) {
				div = "DivineShield";
			}
			else {
				div = "";
			}
			String taunt;
			if(m.isTaunt()) {
				taunt = "Taunt";
			}
			else {
				taunt = "";
			}
			String sleeping;
			if(m.isSleeping()) {
				sleeping = "Sleeping";
			}
			else {
				sleeping = "Ready To Attack";
			}
			String attack ; 
			
			if (!(m.isAttacked()) && m.isSleeping()==false) {
				sleeping = "Ready To Attack";
			}
			else if (m.isAttacked()) {
				sleeping = "Sleeping";
			}
			
				

			
			AFieldButtons.get(i).setToolTipText(m.getName()+ " \n ManaCost:" + m.getManaCost() + " \n" + m.getRarity() + " \n Attack:" + 
					m.getAttack() + " \n HP:" + m.getCurrentHP() + " \n" + div + " \n" + 
					taunt + " \n" + sleeping + " \n");
//			AFieldButtons.get(i).setIcon(null);
			
			if(div=="DivineShield")
				div="Divine";
			if(sleeping=="Ready To Attack")
				sleeping="Ready";
			else 
				sleeping="";
			AFieldButtons.get(i).setText("<html>" +"Atk:" +m.getAttack()+ "<br>"+
					"\nHP:"+m.getCurrentHP()+ "<br>"+ 
					div +"<br>" +
					taunt + "<br>" +
					sleeping +"<br>" +"</html>"); //multiline code from Coderanch
			
			AFieldButtons.get(i).setIcon(new ImageIcon("src/images/Cards/"+m.getName()+" copy.png"));
//			AFieldButtons.get(i).setIcon(new ImageIcon("src/images/Cards/"+AFieldButtons.get(i).getName()+" copy.png"));
			//System.out.println(m.getCurrentHP());
		}
		if(field == selectedHero2.getField()) {

			Minion m = selectedHero2.getField().get(i);
			String div;
			if(m.isDivine()) {
				div = "DivineShield";
			}
			else {
				div = "";
			}
			String taunt;
			if(m.isTaunt()) {
				taunt = "Taunt";
			}
			else {
				taunt = "";
			}
			String sleeping;
			if(m.isSleeping()) {
				sleeping = "Sleeping";
			}
			else {
				sleeping = "Ready To Attack";
			}
			String attack ; 
			if (m.isAttacked()) {
				sleeping = "Sleeping";
			}
			if (!(m.isAttacked())) {
				sleeping = "Ready To Attack";
			}
			
			
			
//					"\nDivine: "+m.isDivine()+ "<br>"+
//					"\nReady: "+!(m.isSleeping())+ "</html>"); //multiline text code from coderanch.com
			
			BFieldButtons.get(i).setToolTipText(m.getName()+ " \n ManaCost:" + m.getManaCost() + " \n" + m.getRarity() + " \n Attack:" + 
					m.getAttack() + " \n HP:" + m.getCurrentHP() + " \n" + div + " \n" + 
					taunt + " \n" + sleeping + " \n");
			
			if(div=="DivineShield")
				div="Divine";
			if(sleeping=="Ready To Attack")
				sleeping="Ready";
			else 
				sleeping="";
			BFieldButtons.get(i).setText("<html>" +"Atk:" +m.getAttack()+ "<br>"+
					"\nHP:"+m.getCurrentHP()+ "<br>"+ 
					div +"<br>" +
					taunt + "<br>" +
					sleeping +"<br>" +"</html>");
//			BFieldButtons.get(i).setIcon(null);
			BFieldButtons.get(i).setIcon(new ImageIcon("src/images/Cards/"+m.getName()+" copy.png"));
			
		}




	}
	@Override
	public void UpdateHero(Hero hero) {
		if(hero == selectedHero1) {
			view.getAhpMana().setText("HP: "+ hero.getCurrentHP()+"\n"+
					"Mana Crystals: "+selectedHero1.getCurrentManaCrystals() + "/"+ selectedHero1.getTotalManaCrystals());
		}
		if(hero == selectedHero2) {
			view.getBhpMana().setText("HP: "+ selectedHero2.getCurrentHP()+"\n"+
					"Mana Crystals: "+selectedHero2.getCurrentManaCrystals() + "/"+ selectedHero2.getTotalManaCrystals());
		}

	}
	@Override
	public void OnCardBurned(Card c,Hero h) {
		if(selectedHero1 == h) {
			view.getAcardsleft().setText("Cards Left: "+ selectedHero1.getDeck().size() + 
					"\n Cards in Hand: " + selectedHero1.getHand().size());

			//			view.getADeck().removeAll();
			//			JLabel tmp= new JLabel();
			//			tmp.setPreferredSize(view.getADeck().getSize());
			//			tmp.setIcon(new ImageIcon("src/images/burnt.png"));
			//			view.getADeck().add(tmp);

			JOptionPane.showMessageDialog(view, "The Burned Card: "+c.getName());
		}
		if(selectedHero2 == h) {
			view.getBcardsleft().setText("Cards Left: "+ selectedHero2.getDeck().size() + 
					"\n Cards in Hand: " + selectedHero2.getHand().size());

			//			view.getBDeck().removeAll();
			//			JLabel tmp= new JLabel();
			//			tmp.setPreferredSize(view.getBDeck().getSize());
			//			tmp.setIcon(new ImageIcon("src/images/burnt.png"));
			//			view.getBDeck().add(tmp);

			JOptionPane.showMessageDialog(view, "The Burned Card: "+c.getName());
		}
	}
	@Override
	public void Fatigue(Hero h,int i) {
		if(selectedHero1 == h) {
			if(h.getDeck().size()==0) {
			view.getADeck().removeAll();
			JLabel tmp= new JLabel();
			tmp.setPreferredSize(view.getADeck().getSize());
			tmp.setIcon(new ImageIcon("src/images/burnt.png"));
			view.getADeck().add(tmp);
			}
			if(i>0) {
			JOptionPane.showMessageDialog(view, "You took: "+i+" damage from Fatigue!");
			playSound("src/Audio/fatigue.wav");
			}
		}
		if(selectedHero2 == h) {

			if(h.getDeck().size()==0) {
				
			view.getBDeck().removeAll();
			JLabel tmp= new JLabel();
			tmp.setPreferredSize(view.getBDeck().getSize());
			tmp.setIcon(new ImageIcon("src/images/burnt.png"));
			view.getBDeck().add(tmp);
			}
			if(i>0) {
			JOptionPane.showMessageDialog(view, "You took: "+i+" damage from Fatigue!");
			playSound("src/Audio/fatigue.wav");
			}
		}
	}
	@Override
	public void updateCard(Hero hero, Card c) {
		if(hero == selectedHero1 && c instanceof Minion) {
			Minion m = (Minion) c;
			String div;
			if(m.isDivine()) {
				div = "DivineShield";
			}
			else {
				div = "";
			}
			String taunt;
			if(m.isTaunt()) {
				taunt = "Taunt";
			}
			else {
				taunt = "";
			}
			String sleeping;
			if(m.isSleeping()) {
				sleeping = "Sleeping";
			}
			else {
				sleeping = "Ready To Attack";
			}
			String attack ; 
			if (m.isAttacked()) {
				sleeping = "Sleeping";
			}
			if (!(m.isAttacked())) {
				sleeping = "Ready To Attack";
			}
				

			if ((Minion)c==selectedHero1.getHand().get(selectedHero1.getHand().size()-1)) {
			Ahandbuttons.get(Ahandbuttons.size()-1).setToolTipText(m.getName()+ " \n ManaCost:" + m.getManaCost() + " \n" + m.getRarity() + " \n Attack:" + 
					m.getAttack() + " \n HP:" + m.getCurrentHP() + " \n" + div + " \n" + 
					taunt + " \n" + sleeping + " \n");
			view.getAcardsleft().setText("Cards Left In Deck: "+ selectedHero1.getDeck().size() + "\n Cards in Hand: " + selectedHero1.getHand().size());
			}
			if ((Minion)c==selectedHero1.getHand().get(selectedHero1.getHand().size()-2)) {
				Ahandbuttons.get(Ahandbuttons.size()-2).setToolTipText(m.getName()+ " \n ManaCost:" + m.getManaCost() + " \n" + m.getRarity() + " \n Attack:" + 
						m.getAttack() + " \n HP:" + m.getCurrentHP() + " \n" + div + " \n" + 
						taunt + " \n" + sleeping + " \n");
			view.getAcardsleft().setText("Cards Left In Deck: "+ selectedHero1.getDeck().size() + "\n Cards in Hand: " + selectedHero1.getHand().size());
				}
		
	}
		if(hero == selectedHero2 && c instanceof Minion) {
			Minion m = (Minion) c;
			String div;
			if(m.isDivine()) {
				div = "DivineShield";
			}
			else {
				div = "";
			}
			String taunt;
			if(m.isTaunt()) {
				taunt = "Taunt";
			}
			else {
				taunt = "";
			}
			String sleeping;
			if(m.isSleeping()) {
				sleeping = "Sleeping";
			}
			else {
				sleeping = "Ready To Attack";
			}
			String attack ; 
			if (m.isAttacked()) {
				sleeping = "Sleeping";
			}
			if (!(m.isAttacked())) {
				sleeping = "Ready To Attack";
			}
			if ((Minion)c==selectedHero2.getHand().get(selectedHero2.getHand().size()-1)) {
			Bhandbuttons.get(Bhandbuttons.size()-1).setToolTipText(m.getName()+ " \n ManaCost:" + m.getManaCost() + " \n" + m.getRarity() + " \n Attack:" + 
					m.getAttack() + " \n HP:" + m.getCurrentHP() + " \n" + div + " \n" + 
					taunt + " \n" + sleeping + " \n");
			view.getBcardsleft().setText("Cards Left In Deck: "+ selectedHero2.getDeck().size() + "\n Cards in Hand: " + selectedHero2.getHand().size());
			}
			if ((Minion)c==selectedHero2.getHand().get(selectedHero2.getHand().size()-2)) {
				Bhandbuttons.get(Bhandbuttons.size()-2).setToolTipText(m.getName()+ " \n ManaCost:" + m.getManaCost() + " \n" + m.getRarity() + " \n Attack:" + 
						m.getAttack() + " \n HP:" + m.getCurrentHP() + " \n" + div + " \n" + 
						taunt + " \n" + sleeping + " \n");
				view.getBcardsleft().setText("Cards Left In Deck: "+ selectedHero2.getDeck().size() + "\n Cards in Hand: " + selectedHero2.getHand().size());
			}
	}
			
}
	public void ExceptionMessage(String message) {
		ImageIcon icon = new ImageIcon("src/images/Exception.jpg");
		//System.out.println(icon);
		soundEffect("error.wav");
	     JOptionPane.showMessageDialog(view, message , null, 0,icon );
	     
	     view.revalidate();
		view.repaint();
	}
	@Override
	public void specialMinionEffect(String string, int i,Hero h) {
		if (string=="Prophet Velen") {
			if(h==selectedHero1)
				if(i==1)
				view.getHeroPowerA().setIcon(new ImageIcon("src/images/HeroPowers/velen.png"));
				else
					view.getHeroPowerA().setIcon(new ImageIcon("src/images/HeroPowers/Anduin Wrynn.png"));
			else
				if(i==1)
					view.getHeroPowerB().setIcon(new ImageIcon("src/images/HeroPowers/velen.png"));
					else
					view.getHeroPowerB().setIcon(new ImageIcon("src/images/HeroPowers/Anduin Wrynn.png"));
			}
		
		
	
}
	@Override
	public void showexception(String string) {
		// TODO Auto-generated method stub
		ExceptionMessage(string);
	}
	
}
