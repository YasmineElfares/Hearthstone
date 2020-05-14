package view;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.*;

public class HearthstoneView extends JFrame {

	//MyHero Instance variables

	private JButton HeroPowerA;
	private JButton HeroPowerB;




	public JButton getHeroPowerA() {
		return HeroPowerA;
	}


	public void setHeroPowerA(JButton heroPowerA) {
		HeroPowerA = heroPowerA;
	}


	public JButton getHeroPowerB() {
		return HeroPowerB;
	}


	public void setHeroPowerB(JButton heroPowerB) {
		HeroPowerB = heroPowerB;
	}


	private ArrayList<JButton> welcome1;
	public JPanel getADeck() {
		return ADeck;
	}


	public void setADeck(JPanel aDeck) {
		ADeck = aDeck;
	}


	public JPanel getBDeck() {
		return BDeck;
	}


	public void setBDeck(JPanel bDeck) {
		BDeck = bDeck;
	}


	private ArrayList<JButton> welcome2;
	private JButton APortrait;
	private JButton BPortrait;


	// welcome window
	private JFrame welcome;

	private JPanel ADeck;
	private JPanel BDeck;
	private JButton end ;
	private JButton target ;



	public JButton getTarget() {
		return target;
	}


	public void setTarget(JButton target) {
		this.target = target;
	}


	private JLabel MyHeroHp;
	private JLabel MyHeroMana;
	private JLabel MyHeroCardsLeft;
	private JPanel AMinions ;
	private JPanel ACards ;
	private JPanel AInfo ;
	private JLabel ACardsLeft;

	private JTextArea Acardsleft ;
	private JTextArea Aname ;
	private JTextArea Amana ;
	private JTextArea AhpMana;    

	private JLabel OppHp;
	private JLabel OppMana;
	private JLabel OppHeroCardsLeft;
	private JPanel BMinions ;
	private JPanel BCards ;
	private JPanel BInfo ;
	private JLabel BCardsLeft;

	private JTextArea Bcardsleft ;
	private JTextArea Bname ;
	private JTextArea Bmana ;
	private JTextArea BhpMana; 


	
	
	public HearthstoneView() throws IOException{
		super();
		// HS FONT
		try {
			//create the font to use. Specify the size!
			Font customFont = Font.createFont(Font.TRUETYPE_FONT, new File("src/Fonts/Hearthstone.ttf")).deriveFont(12f);
			
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			//register the font
			ge.registerFont(customFont);



			
				//Getting Dimesnison
				Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

				//To hold Hero Buttons
				welcome1  = new ArrayList<JButton>();
				welcome2  = new ArrayList<JButton>();

				//Welcome JFrame
				welcome = new JFrame();
				welcome.setContentPane(new JLayeredPane());
				welcome.setBounds(0,0,1400,800);
				welcome.setLocation(dim.width/2-welcome.getSize().width/2, dim.height/2-welcome.getSize().height/2);
				welcome.setTitle("Welcome");
				welcome.setDefaultCloseOperation(EXIT_ON_CLOSE);
				welcome.setVisible(true);

				
				JLabel Background = new JLabel();
				Background.setSize(welcome.getSize());
				Background.setIcon(new ImageIcon("src/Images/Welcome Wallpaper.png"));
				welcome.add(Background,0);

				//adding main frame to hold panels
				JPanel welcomeMain = new JPanel();
				welcomeMain.setSize(welcome.getSize());
				welcome.add(welcomeMain,1);
				welcomeMain.setLayout(new BorderLayout());

				//Hero Panels
				JPanel firstHeroes = new JPanel();
				firstHeroes.setLayout(new FlowLayout());

				JPanel secondHeroes = new JPanel();
				secondHeroes.setLayout(new FlowLayout());
				Color orangey = new Color(202,167,118);
				Color darkorangey = new Color(141,108,74);
				Color darkestOrangey= new Color(77,59,40);
			
				firstHeroes.setPreferredSize(new Dimension(welcomeMain.getWidth(),welcomeMain.getHeight()/2));
				secondHeroes.setPreferredSize(new Dimension(welcomeMain.getWidth(),welcomeMain.getHeight()/2));


				//labels
				JLabel select1st = new JLabel();
				select1st.setText("Player 1: Please select your Hero!");
				JLabel select2nd = new JLabel();
				select2nd.setText("Player 2: Please select your Hero!");
				select1st.setSize(firstHeroes.getWidth(),100);
				select2nd.setSize(firstHeroes.getWidth(),100);
				select1st.setFont(customFont);
				select2nd.setFont(customFont);



				firstHeroes.setBackground(orangey);
				secondHeroes.setBackground(orangey);

				welcomeMain.add(firstHeroes,BorderLayout.SOUTH);
				welcomeMain.add(secondHeroes,BorderLayout.NORTH);
				String [] s = {"Mage","Warlock","Paladin","Hunter","Priest"};




				//1st hero buttons
				for(int i = 0; i<5 ; i++) {
					JButton b = new JButton();
					//b.setIcon(new ImageIcon("src/images/Heroes/Hunter.png"));
					welcome1.add(b);
					b.setPreferredSize(new Dimension(205,300));
					b.setVisible(true);
					b.setText(s[i]);
					b.setFont(customFont);
					String loc = "src/images/Heroes/" + s[i] + ".png";
					b.setIcon(new ImageIcon(loc));
//					b.setBorderPainted(false);
					b.setContentAreaFilled(false); 
					b.setFocusPainted(false); 
					b.setOpaque(false);
					

					firstHeroes.add(b);
					firstHeroes.repaint();
					firstHeroes.revalidate();
					b.revalidate();
					b.repaint();

					welcomeMain.revalidate();
					welcomeMain.repaint();
					welcome.revalidate();
					welcome.repaint();
				}
				//2nd hero buttons
				for(int i = 0; i<5 ; i++) {
					JButton b = new JButton();
					b.setPreferredSize(new Dimension(205,300));
					b.setVisible(true);
					b.setText(s[i]);
					b.setFont(customFont);

					secondHeroes.add(b);
					welcome2.add(b);
					String loc = "src/images/Heroes/" + s[i] + ".png";
					b.setIcon(new ImageIcon(loc));
//					b.setBorderPainted(false);
					b.setContentAreaFilled(false); 
					b.setFocusPainted(false); 
					b.setOpaque(false);
					
					
					secondHeroes.repaint();
					secondHeroes.revalidate();
					b.revalidate();
					b.repaint();

					welcomeMain.revalidate();
					welcomeMain.repaint();
					welcome.revalidate();
					welcome.repaint();

					//			

				}
				welcome.revalidate();
				welcome.repaint();
				welcomeMain.revalidate();
				welcomeMain.repaint();
				firstHeroes.revalidate();
				firstHeroes.repaint();
				secondHeroes.revalidate();
				secondHeroes.repaint();
				
			
			//main game

			setPreferredSize(new Dimension(1400,945));
			
			this.setBounds(0,0,1400,945);
			this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
			this.getContentPane().setLayout(new BorderLayout());
			
			JLayeredPane Layers = new JLayeredPane();
			Layers.setSize(new Dimension(1400,900));
			
			this.add(Layers,BorderLayout.CENTER);
			

			JPanel mainP = new JPanel();
			mainP.setSize(new Dimension(1400,900));
			mainP.setLayout(new BorderLayout());
			mainP.setOpaque(true);
			Layers.add(mainP,1);


			setTitle("Hearthstone");
			this.setDefaultCloseOperation(EXIT_ON_CLOSE);
			
			//Both Panels
			JPanel MyHeroPanel = new JPanel();
			JPanel OppPanel = new JPanel();
			mainP.add(MyHeroPanel,BorderLayout.SOUTH);
			mainP.add(OppPanel,BorderLayout.NORTH);
			MyHeroPanel.setPreferredSize(new Dimension(mainP.getWidth(),mainP.getHeight()/2));
			OppPanel.setPreferredSize(new Dimension(mainP.getWidth(),mainP.getHeight()/2)); 
			MyHeroPanel.setBackground(orangey);
			OppPanel.setBackground(orangey);
			
//			MyHeroPanel.setOpaque(false);OppPanel.setOpaque(false);


			//end button
			end = new JButton("END TURN!");  //end
			JPanel borderUp= new JPanel();
			borderUp.setPreferredSize(new Dimension(1400,425));
			JPanel borderDown= new JPanel();
			borderDown.setPreferredSize(new Dimension(1400,425));
			borderUp.setOpaque(false);borderDown.setOpaque(false);
			end.setSize(new Dimension(200,100));
			JPanel endJP = new JPanel();
			endJP.setLayout(new BorderLayout());
			endJP.setSize(new Dimension(1400,900));

			endJP.add(borderUp,BorderLayout.NORTH);
			endJP.add(borderDown,BorderLayout.SOUTH);
			endJP.add(end,BorderLayout.EAST);
			endJP.setOpaque(false);
			Layers.add(endJP ,2);

			//target button
			target = new JButton("Pick Your Target!");
			target.setSize(new Dimension(200,100));
			endJP.add(target,BorderLayout.WEST);


			// current hero power button
			HeroPowerA = new JButton();  //end
			HeroPowerA.setPreferredSize(new Dimension(200,100));
			HeroPowerA.setVisible(true);
			// getContentPane().add(HeroPowerA ,BorderLayout.WEST);
			HeroPowerA.setText("USE HERO POWER");
			HeroPowerA.setFont(customFont);


			//opp hero power button
			HeroPowerB = new JButton();  //end
			HeroPowerB.setPreferredSize(new Dimension(200,100));
			HeroPowerB.setVisible(true);
			// getContentPane().add(HeroPowerA ,BorderLayout.WEST);
			HeroPowerB.setText("USE HERO POWER");
			HeroPowerB.setFont(customFont);

			// layout of current hero
			MyHeroPanel.setLayout(new GridLayout(3,1));
			AMinions = new JPanel();  
			AMinions.setPreferredSize(new Dimension ((int)MyHeroPanel.getWidth(),(int)MyHeroPanel.getHeight()/6 ));
			AMinions.setOpaque(false);
			ACards = new JPanel();
			ACards.setPreferredSize(new Dimension ((int)MyHeroPanel.getWidth(),(int)MyHeroPanel.getHeight()/6 ));
			AInfo = new JPanel();
			AInfo.setPreferredSize(new Dimension ((int)MyHeroPanel.getWidth(),(int)MyHeroPanel.getHeight()/6 ));
			ACards.setBackground(darkorangey);
			AInfo.setBackground(orangey);
			MyHeroPanel.add(AInfo,2,0);
			MyHeroPanel.add(ACards,1,0);
			MyHeroPanel.add(AMinions,0,0);

			AInfo.setLayout(new GridLayout(2,3));
			AInfo.setBackground(Color.DARK_GRAY);
			ADeck = new JPanel();  //Deck img
			ADeck.setPreferredSize(new Dimension(196,76));
			ADeck.setPreferredSize(new Dimension ((int)AInfo.getWidth()/3,(int)AInfo.getHeight()/2));
			JLabel ACardsLeft= new JLabel();
			ACardsLeft.setPreferredSize(new Dimension ((int)AInfo.getWidth()/3,(int)AInfo.getHeight()/2));
			APortrait = new JButton(); //Portrait img
			APortrait.setPreferredSize(new Dimension ((int)AInfo.getWidth()/3,(int)AInfo.getHeight()/2));
			JLabel AName= new JLabel();
			AName.setPreferredSize(new Dimension ((int)AInfo.getWidth()/3,(int)AInfo.getHeight()/2));
			JLabel AHp= new JLabel();
			AHp.setPreferredSize(new Dimension ((int)AInfo.getWidth()/3,(int)AInfo.getHeight()/2));
			JLabel AMana= new JLabel();
			AMana.setPreferredSize(new Dimension ((int)AInfo.getWidth()/3,(int)AInfo.getHeight()/2));

			Acardsleft = new JTextArea () ;
			Acardsleft.setPreferredSize(new Dimension (ACardsLeft.getWidth(),ACardsLeft.getHeight()));
			Acardsleft.setEditable(false);
			Aname =new JTextArea () ;
			Aname.setPreferredSize(new Dimension (AName.getWidth(),AName.getHeight()));
			Aname.setEditable(false);
			Amana =new JTextArea () ;
			Amana.setPreferredSize(new Dimension (AMana.getWidth(),AMana.getHeight()));
			Amana.setEditable(false);
			AhpMana  = new JTextArea () ; 
			AhpMana.setPreferredSize(new Dimension (AHp.getWidth(),AHp.getHeight()));
			AhpMana.setEditable(false);

			ADeck.setSize(AInfo.getWidth()/7,AInfo.getHeight());
			APortrait.setSize(AInfo.getWidth()/7,AInfo.getHeight());
			HeroPowerA.setSize(AInfo.getWidth()/7,AInfo.getHeight());
			Amana.setSize(AInfo.getWidth()/7,AInfo.getHeight());
			Acardsleft.setSize(AInfo.getWidth()/7,AInfo.getHeight());
			Aname.setSize(AInfo.getWidth()/7,AInfo.getHeight());
			AhpMana.setSize(AInfo.getWidth()/7,AInfo.getHeight());
			
			AInfo.add(ADeck);
			AInfo.add(APortrait);
			AInfo.add(HeroPowerA);
			
//			AInfo.add(Amana);
			AInfo.add(Acardsleft);
			Acardsleft.setBackground(darkestOrangey);
			Acardsleft.setForeground(Color.white);
			AInfo.add(Aname);
			Aname.setBackground(darkestOrangey);
			AInfo.add(AhpMana);
			AhpMana.setBackground(darkestOrangey);
			
			AhpMana.setText("hp");
			AhpMana.setFont(customFont);
			AhpMana.setBackground(darkestOrangey);
			AhpMana.setForeground(Color.white);
			Acardsleft.setText("cardsleft");
			Acardsleft.setFont(customFont);
			
			Aname.setText("name");
			Aname.setBackground(darkestOrangey);
			Aname.setFont(customFont);
			Aname.setForeground(Color.white);
			AInfo.revalidate();
			AInfo.repaint();
			
//			JLabel a= new JLabel("Minions: "); //test
//			JLabel b= new JLabel("Cards: ");
//			// JLabel c= new JLabel("info");
//			AMinions.add(a);
//			ACards.add(b);
			//  AInfo.add(c);

			//layout of opp hero
			OppPanel.setLayout(new GridLayout(3,1));
			BMinions = new JPanel(); 
			BMinions.setPreferredSize(new Dimension ((int)MyHeroPanel.getWidth(),(int)MyHeroPanel.getHeight()/6 ));
			BMinions.setOpaque(false);
			BCards = new JPanel();
			BCards.setPreferredSize(new Dimension ((int)MyHeroPanel.getWidth(),(int)MyHeroPanel.getHeight()/6 ));
			BInfo = new JPanel();
			BInfo.setPreferredSize(new Dimension ((int)MyHeroPanel.getWidth(),(int)MyHeroPanel.getHeight()/6 ));
			OppPanel.add(BMinions,0,0);
			OppPanel.add(BCards,1,0);
			OppPanel.add(BInfo,2,0);

			BCards.setBackground(darkorangey);
			BInfo.setBackground(orangey);
			
			BInfo.setLayout(new GridLayout(2,3));
			BDeck = new JPanel();  //Deck img
			BDeck.setPreferredSize(new Dimension ((int)AInfo.getWidth()/3,(int)AInfo.getHeight()/2));     
			JLabel BCardsLeft= new JLabel();
			BCardsLeft.setPreferredSize(new Dimension ((int)AInfo.getWidth()/3,(int)AInfo.getHeight()/2));

			BPortrait = new JButton(); //Portrait img
			BPortrait.setPreferredSize(new Dimension ((int)AInfo.getWidth()/3,(int)AInfo.getHeight()/2));
			JLabel BName= new JLabel();
			BName.setPreferredSize(new Dimension ((int)AInfo.getWidth()/3,(int)AInfo.getHeight()/2));
			JLabel BHp= new JLabel();
			BHp.setPreferredSize(new Dimension ((int)AInfo.getWidth()/3,(int)AInfo.getHeight()/2));
			JLabel BMana= new JLabel();
			BMana.setPreferredSize(new Dimension ((int)AInfo.getWidth()/3,(int)AInfo.getHeight()/2));


			Bcardsleft = new JTextArea () ;
			Bcardsleft.setPreferredSize(new Dimension (BCardsLeft.getWidth(),BCardsLeft.getHeight()));
			Bcardsleft.setEditable(false);
			Bname =new JTextArea () ;
			Bname.setPreferredSize(new Dimension (BName.getWidth(),BName.getHeight()));
			Bname.setEditable(false);
			Bmana =new JTextArea () ;
			Bmana.setPreferredSize(new Dimension (BMana.getWidth(),BMana.getHeight()));
			Bmana.setEditable(false);
			BhpMana  = new JTextArea () ; 
			BhpMana.setPreferredSize(new Dimension (BHp.getWidth(),BHp.getHeight()));
			BhpMana.setEditable(false);

			BInfo.add(BDeck);
			BInfo.add(BPortrait);
			BInfo.add(HeroPowerB);
			// AInfo.add(AMana);
//			BInfo.add(Bmana);
			
			//AInfo.add(ACardsLeft);
			BInfo.add(Bcardsleft);
			
			Bcardsleft.setText("cardsleft");
			Bcardsleft.setFont(customFont);
			Bcardsleft.setBackground(darkestOrangey);
			Bcardsleft.setForeground(Color.white);
			//AInfo.add(AName);
			BInfo.add(Bname);
			Bname.setText("name");
			Bname.setFont(customFont);
			Bname.setBackground(darkestOrangey);
			Bname.setForeground(Color.white);
			//AInfo.add(AHp);
			BInfo.add(BhpMana);
			
			BhpMana.setText("hp");
			BhpMana.setFont(customFont);
			BhpMana.setBackground(darkestOrangey);
			BhpMana.setForeground(Color.white);

//			JLabel c= new JLabel("Minions: "); //test
//			JLabel d= new JLabel("Cards: ");
//			// JLabel c= new JLabel("info");
//			BMinions.add(c);
//			BCards.add(d);
			//  AInfo.add(c);
			this.pack();
			this.revalidate();
			this.repaint();
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch(FontFormatException e) {
			e.printStackTrace();
		}
	}


	// setters and getters

	public JLabel getMyHeroHp() {
		return MyHeroHp;
	}
	public void setMyHeroHp(JLabel myHeroHp) {
		MyHeroHp = myHeroHp;
	}
	public JLabel getMyHeroMana() {
		return MyHeroMana;
	}
	public void setMyHeroMana(JLabel myHeroMana) {
		MyHeroMana = myHeroMana;
	}
	public JLabel getMyHeroCardsLeft() {
		return MyHeroCardsLeft;
	}
	public void setMyHeroCardsLeft(JLabel myHeroCardsLeft) {
		MyHeroCardsLeft = myHeroCardsLeft;
	}
	public JLabel getACardsLeft() {
		return ACardsLeft;
	}
	public void setACardsLeft(JLabel aCardsLeft) {
		ACardsLeft = aCardsLeft;
	}
	public JTextArea getAcardsleft() {
		return Acardsleft;
	}
	public void setAcardsleft(JTextArea acardsleft) {
		Acardsleft = acardsleft;
	}
	public JTextArea getAname() {
		return Aname;
	}
	public void setAname(JTextArea aname) {
		Aname = aname;
	}
	public JTextArea getAmana() {
		return Amana;
	}
	public void setAmana(JTextArea amana) {
		Amana = amana;
	}
	public JTextArea getAhpMana() {
		return AhpMana;
	}
	public void setAhpMana(JTextArea ahp) {
		AhpMana = ahp;
	}
	public JLabel getOppHp() {
		return OppHp;
	}
	public void setOppHp(JLabel oppHp) {
		OppHp = oppHp;
	}
	public JLabel getOppMana() {
		return OppMana;
	}
	public void setOppMana(JLabel oppMana) {
		OppMana = oppMana;
	}
	public JLabel getOppHeroCardsLeft() {
		return OppHeroCardsLeft;
	}
	public void setOppHeroCardsLeft(JLabel oppHeroCardsLeft) {
		OppHeroCardsLeft = oppHeroCardsLeft;
	}
	public JLabel getBCardsLeft() {
		return BCardsLeft;
	}
	public void setBCardsLeft(JLabel bCardsLeft) {
		BCardsLeft = bCardsLeft;
	}
	public JTextArea getBcardsleft() {
		return Bcardsleft;
	}
	public void setBcardsleft(JTextArea bcardsleft) {
		Bcardsleft = bcardsleft;
	}
	public JTextArea getBname() {
		return Bname;
	}
	public void setBname(JTextArea bname) {
		Bname = bname;
	}
	public JTextArea getBmana() {
		return Bmana;
	}
	public void setBmana(JTextArea bmana) {
		Bmana = bmana;
	}
	public JTextArea getBhpMana() {
		return BhpMana;
	}
	public void setBhp(JTextArea bhp) {
		BhpMana = bhp;
	}

	public JFrame getWelcome() {
		return welcome;
	}
	public void setWelcome(JFrame welcome) {
		this.welcome = welcome;
	}
	public void setAMinions(JPanel aMinions) {
		AMinions = aMinions;
	}
	public void setACards(JPanel aCards) {
		ACards = aCards;
	}
	public void setAInfo(JPanel aInfo) {
		AInfo = aInfo;
	}

	public void setBMinions(JPanel bMinions) {
		BMinions = bMinions;
	}
	public void setBCards(JPanel bCards) {
		BCards = bCards;
	}
	public void setBInfo(JPanel bInfo) {
		BInfo = bInfo;
	}

	public ArrayList<JButton> getWelcome1() {
		return welcome1;
	}
	public void setWelcome1(ArrayList<JButton> welcome1) {
		this.welcome1 = welcome1;
	}
	public ArrayList<JButton> getWelcome2() {
		return welcome2;
	}
	public void setWelcome2(ArrayList<JButton> welcome2) {
		this.welcome2 = welcome2;
	}

	//Getters
	public JPanel getAMinions() {
		return AMinions;
	}
	public JPanel getACards() {
		return ACards;
	}
	public JPanel getAInfo() {
		return AInfo;
	}

	public JPanel getBMinions() {
		return BMinions;
	}
	public JPanel getBCards() {
		return BCards;
	}
	public JPanel getBInfo() {
		return BInfo;
	}

	public JButton getAPortrait() {
		return APortrait;
	}


	public void setAPortrait(JButton aPortrait) {
		APortrait = aPortrait;
	}


	public JButton getBPortrait() {
		return BPortrait;
	}


	public void setBPortrait(JButton bPortrait) {
		BPortrait = bPortrait;
	}
	public JButton getEnd() {
		return end;
	}


	public void setEnd(JButton end) {
		this.end = end;
	}

	
}
