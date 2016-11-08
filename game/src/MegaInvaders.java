import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import java.util.ArrayList;

/**
 * Runs the Mega Invaders game by executing the step() function repeatedly, as called from Main. Setup functions may
 * be called during gameplay, but the init() initializing function need only be called once.
 * 
 * Dependencies: depends that all other classes are functional and work using the passed in parameters.
 * 
 * @author Filip Mazurek
 */
public class MegaInvaders {

	// User may change the static final numbers at will to change their game experience

	private static final int ENEMY_ROWS = 2; // too large numbers may result in instant loss
	private static final int ENEMY_COLUMNS = 9; // too large numbers may result in instant loss
	private static final int SHIP_SPEED = 3;
	private static final int NUM_ATTACKS_ON_SCREEN = 3;
	private static final int TIME_BETWEEN_ATTACKS = 700;
	private static final int BOSS_POSE_CHANGE_MILLIS = 300;
	private static final int METROID_POSE_CHANGE_MILLIS = 500;
	private static final String SHIP_UP = "W";
	private static final String SHIP_LEFT = "A";
	private static final String	SHIP_DOWN = "S";
	private static final String	SHIP_RIGHT = "D";
	private static final String	SHIP_MISSILE = "J";
	
	private int missileFiringRate = 600;
	
	private ArrayList<Missile> myMissiles;
	private ArrayList<Metroid> myMetroids;
	private ArrayList<String> input;
	private ArrayList<BossAttack_Masterpiece> bossAttacks;
	private Rectangle topBound;
	private Rectangle bottomBound;
	private Rectangle leftBound;
	private Rectangle rightBound;
	private Rectangle leftMetroidBound;
	private Rectangle rightMetroidBound;
	private int screenWidth;
	private int screenHeight;
	private Button progressToBoss;
	private Button restartFromBeginning;
	private boolean gameLost;
	private boolean gameWon;
	private boolean metroidLevel;
	private boolean bossLevel;
	private boolean gameLoop;
	private long lastAttackAtTime = 0;
	private long lastBossPositionSwapAtTime = 0;
	private long lastShotFiredAtTime = 0;
	private long lastMandibleSwapAtTime = 0;
	private String bossFilename = "Ridley_1.png";
	private String gameWonFilename = "normal_game_won.jpg";
	private String normalEnemyFilename_1 = "MetroidLight_1.png";
	private String normalEnemyFilename_2 = "MetroidDark_2.png";
	private String missileFilename = "missile.png";
	private String shipFilename = "myShip.png";
	private String blasterFilename = "Blaster_1.png";
	private String musicFilename = "normal_level_music.wav";
	private boolean invincible = false;
	private Boss myBoss;
	private Scene myScene;
	private ImageView myShip;
	private Group root;

	
	/**
	 * Initialize only on the first time creating the game object
	 */
	public Scene init (int width, int height) {

		gameLost = false;
		gameWon = false;

		root = new Group();

		myMissiles = new ArrayList<Missile>();
		myMetroids = new ArrayList<Metroid>();
		input = new ArrayList<String>();
		bossAttacks = new ArrayList<BossAttack_Masterpiece>();

		topBound = new Rectangle();
		bottomBound = new Rectangle();
		leftBound = new Rectangle();
		rightBound = new Rectangle();
		leftMetroidBound = new Rectangle();
		rightMetroidBound = new Rectangle();

		restartFromBeginning = new Button();
		progressToBoss = new Button();

		// create buttons that can later be shown to progress levels
		progressToBoss.setText("Boss Stage");
		progressToBoss.setOnAction(e -> setupForBoss());
		progressToBoss.setLayoutX(width/2 - progressToBoss.getWidth()/2);
		progressToBoss.setLayoutY(height/3 - progressToBoss.getHeight()/2);

		restartFromBeginning.setText("First Level Again");
		restartFromBeginning.setOnAction(e -> setupForLevelOne());
		restartFromBeginning.setLayoutX(width/2 - restartFromBeginning.getWidth()/2);
		restartFromBeginning.setLayoutY(height/2 - restartFromBeginning.getHeight()/2);

		screenWidth = width;
		screenHeight = height;

		myScene = new Scene(root, width, height, Color.BLACK);

		Image image = new Image(getClass().getClassLoader().getResourceAsStream(shipFilename));
		myShip = new ImageView(image);

		// during initialization, get everything ready for the first level
		setupForLevelOne();
		
		// play background music for the game
		playMusic();
		
		// respond to input
		myScene.setOnKeyReleased(e -> handleByStringReleased(e));
		myScene.setOnKeyPressed(e -> handleByStringPress(e));
		return myScene;
	}
	
    private void playMusic() {
    	int s = Integer.MAX_VALUE;
        AudioClip audio = new AudioClip(getClass().getResource(musicFilename).toExternalForm());
        audio.setVolume(0.5f);
        audio.setCycleCount(s);
        audio.play();
    }
    
    // convert key presses to Strings added to the input ArrayList
	private void handleByStringPress(KeyEvent e) {
		String stringCode = e.getCode().toString();
		// only add once to prevent duplicates
		if (!input.contains(stringCode) )
			input.add(stringCode);
	}
	
	// remove input when key is released
	private void handleByStringReleased(KeyEvent e) {
		String stringCode = e.getCode().toString();
		input.remove(stringCode);
	}
	
	private void setupForLevelOne() {
		// clean up in case returning to the first level after a loss
		cleanUp();
		gameLoop = true;

		// put the ship in the player bounding box
		myShip.setX(screenWidth / 2 - myShip.getBoundsInLocal().getWidth() / 2);
		myShip.setY(screenHeight - myShip.getBoundsInLocal().getHeight() - 50);
		root.getChildren().add(myShip);

		// creates player bound box as well as invisible barriers for initial enemies
		createBoundsLevel_1(screenWidth, screenHeight, 0, (float) 4/5);

		spawnMetroids(screenWidth, screenHeight);

		// reset booleans for correct game loop
		metroidLevel = true;
		bossLevel = false;
	}


	/**
	 * update frame by frame
	 */
	public void step () {
		if (gameLoop) { // don't step through game loop at certain points--e.g., during game over
			updateMissiles();
			checkBoundingBox();
			updateMyShip();

			if(metroidLevel) {
				moveMetroids();
				checkMetroidPlayerCollision();
				checkMetroidMissileCollision();
			}
			if (bossLevel) {
				attack();
				moveBoss();
				checkAttackCollision();
				checkBossMissileCollision();
			}
			checkLevelProgression();
			checkGameCondition();
		}
	}


	private void updateMissiles() { // make missiles fly upwards. If beyond upper bound of screen, destroy
		int missileToDestroy = -1; // for which missile to destroy
		for (int i = 0; i < myMissiles.size(); i++) { // for each missile in the arrayList
			if (myMissiles.get(i).getY() < 0) { // if out of bounds, destroy the missile
				missileToDestroy = i;
			}
			myMissiles.get(i).updatePos(); // animate each missile to fly upwards
		}
		if (missileToDestroy >= 0) { // if there is a missile to destroy, destroy it
			myMissiles.get(missileToDestroy).destroyMissile(root);
			myMissiles.remove(missileToDestroy);
		}
	}

	private void checkBoundingBox() { // if player ship touches a bound, don't allow them to move past it by removing the command
		checkBoundingBoxHelper(topBound, SHIP_UP);
		checkBoundingBoxHelper(bottomBound, SHIP_DOWN);
		checkBoundingBoxHelper(leftBound, SHIP_LEFT);
		checkBoundingBoxHelper(rightBound, SHIP_RIGHT);
	}

	private void checkBoundingBoxHelper(Rectangle bound, String commandString) { 
		if (bound.getBoundsInParent().intersects(myShip.getBoundsInParent())) {
			input.remove(commandString);
		}
	}

	// create smoother movement by reading items from an input list
	private void updateMyShip() { 
		if (input.contains(SHIP_UP)) {
			myShip.setY(myShip.getY() - SHIP_SPEED);
		}
		if (input.contains(SHIP_LEFT)) {
			myShip.setX(myShip.getX() - SHIP_SPEED);
		}
		if (input.contains(SHIP_DOWN)) {
			myShip.setY(myShip.getY() + SHIP_SPEED);
		}
		if (input.contains(SHIP_RIGHT)) {
			myShip.setX(myShip.getX() + SHIP_SPEED);
		}
		if (input.contains(SHIP_MISSILE)) {		
			if ((System.currentTimeMillis() - lastShotFiredAtTime) > missileFiringRate) {
				lastShotFiredAtTime = System.currentTimeMillis();
				Missile oneMissile = new Missile();
				oneMissile.drawMissile(root, myShip, missileFilename);
				myMissiles.add(oneMissile);
			}
		}
	}

	private void moveMetroids() {
		for(Metroid metroid : myMetroids) { // if enemies hit side of screen, move them down a row
			if ((metroid.myEnemy.getBoundsInParent().intersects(leftMetroidBound.getBoundsInParent())) || 
					(metroid.myEnemy.getBoundsInParent().intersects(rightMetroidBound.getBoundsInParent()))) {
				metroid.updatePositionVertical();
				metroid.updatePositionHorizontal();
			}
			else {
				metroid.updatePositionHorizontal();
			}
			swapMetroidMandibles();
		}
	}

	private void swapMetroidMandibles() { // change look of enemy every some time
		if(System.currentTimeMillis() - lastMandibleSwapAtTime > METROID_POSE_CHANGE_MILLIS) {
			for(Metroid i : myMetroids) {
				i.progressPose(root);
			}
			lastMandibleSwapAtTime = System.currentTimeMillis();
		}
	}

	private void moveBoss() { // if boss hits side of screen, make it bounce off
		if (myBoss.myEnemy.getBoundsInParent().intersects(leftMetroidBound.getBoundsInParent())) {
			myBoss.myEnemy.setX(1); // just move the guy out and change direction
			myBoss.changeDirection();
		}
		if (myBoss.myEnemy.getBoundsInParent().intersects(rightMetroidBound.getBoundsInParent())) {
			myBoss.myEnemy.setX(screenWidth - myBoss.myEnemy.getBoundsInParent().getWidth() - 1); 
			myBoss.changeDirection();
		}

		myBoss.updatePosition();
		bossPoseChange();
	}

	private void bossPoseChange() {
		if(System.currentTimeMillis() - lastBossPositionSwapAtTime > BOSS_POSE_CHANGE_MILLIS) {
			myBoss.progressPose(root);
			lastBossPositionSwapAtTime = System.currentTimeMillis();
		}
	}

	// only attack when criteria are met--certain time between attacks and a max amount of attacks on screen at one time
	private void attack() {
		if ((bossAttacks.size() <= NUM_ATTACKS_ON_SCREEN) && ((System.currentTimeMillis() - lastAttackAtTime) >= TIME_BETWEEN_ATTACKS)) {
			BossAttack_Masterpiece myAttack = new BossAttack_Masterpiece(myShip.getY(), screenWidth, blasterFilename);
			myAttack.beginAttack(root);
			bossAttacks.add(myAttack);
			lastAttackAtTime = System.currentTimeMillis();
		}
	
		// when attacks expire, remove them from the List
		int attackToRemove = -1;
		for (int i = 0; i < bossAttacks.size(); i++) {
			if(!bossAttacks.get(i).progressAttack(root)) {
				attackToRemove = i;
			}
		}
		if (attackToRemove >= 0) {
			bossAttacks.remove(attackToRemove);
		}
	}

	// check if boss attack hits player
	private void checkAttackCollision() {
		for (BossAttack_Masterpiece ba : bossAttacks) {
			if (ba.getBeamBounds().intersects(myShip.getBoundsInParent())) {
				System.out.println("Ship hit");
				ba.setBeamRed();
				if(invincible) { // don't game over if cheat is on
					return;
				}
				gameLost = true;
			}
		}
	}

	// check if player's missiles hit the level 1 enemies
	private void checkMetroidMissileCollision() {
		// if there are no checks, don't bother checking
		if (myMetroids.isEmpty() || myMissiles.isEmpty()) {
			return;
		}
		for (int i = 0; i < myMetroids.size(); i ++) {
			for (int j = 0; j < myMissiles.size(); j++) {
				if (myMetroids.get(i).myEnemy.getBoundsInParent().intersects(myMissiles.get(j).getMissileView().getBoundsInParent())) {
					myMetroids.get(i).updateHealth(root);
					myMetroids.remove(i);
					myMissiles.get(j).destroyMissile(root);
					myMissiles.remove(j);
				}
			}
		}
	}

	private void checkBossMissileCollision() {
		for (int j = 0; j < myMissiles.size(); j++) {
			if (myBoss.myEnemy.getBoundsInParent().intersects(myMissiles.get(j).getMissileView().getBoundsInParent())) {
				myBoss.updateHealth(root);
				myMissiles.get(j).destroyMissile(root);
				myMissiles.remove(j);
			}
		}
	}

	// check if level 1 enemies reached the player
	private void checkMetroidPlayerCollision() {
		for (int i = 0; i < myMetroids.size(); i++) {
			if (myMetroids.get(i).myEnemy.getBoundsInParent().intersects(topBound.getBoundsInParent())) {
				gameLost = true;
			}
		}
	}

	private void checkLevelProgression() {
		if (metroidLevel && myMetroids.isEmpty()) {
			metroidLevel = false;
			root.getChildren().add(progressToBoss);

		}
		if (bossLevel && myBoss.getHealth() <= 0) {

			bossLevel = false;

			bossAttacks.clear(); // clean up
			gameWon = true;
		}
	}

	// execute special actions on game over or game win
	private void checkGameCondition() {
		if(gameLost) {
			gameLost = false;
			gameLoop = false;
			if(bossLevel) {
				root.getChildren().add(progressToBoss);
			}
			root.getChildren().add(restartFromBeginning);
		}
		if(gameWon) {
			gameLoop = false;
			cleanUp();
			Text winText = makeDesiredText("YOU WIN!", screenWidth, screenHeight);
			Image image = new Image(getClass().getClassLoader().getResourceAsStream(gameWonFilename));
			ImageView gameWonImage = new ImageView(image);
			root.getChildren().add(gameWonImage);
			root.getChildren().add(winText);
		}
	}

	// populate level 1 with enemies
	private void spawnMetroids(int width, int height) {
		float offset = 50;
		float spawnX = 50;
		float spawnY = offset;
		int numRows = ENEMY_ROWS;
		int numColumns = ENEMY_COLUMNS;
		String metroidFilename;
		double metroidHeight = 0;

		for(int row = 0; row < numRows; row++) {
			if(row % 2 == 0) {
				metroidFilename = normalEnemyFilename_1;
			}
			else {
				metroidFilename = normalEnemyFilename_2;
			}
			for (int i = 0; i < numColumns; i++) {
				Metroid oneMetroid = new Metroid();
				oneMetroid.drawEnemy(root, spawnX, spawnY, metroidFilename);
				myMetroids.add(oneMetroid);
				spawnX += oneMetroid.getWidth();
				metroidHeight = oneMetroid.getHeight();
			}
			spawnY += metroidHeight * 2;
			spawnX = offset;
		}
	}

	// tear down and set up for the boss level
	private void setupForBoss() {
		gameLoop = true;
		cleanUp();
	
		// move player ship to center
		myShip.setX(screenWidth / 2 - myShip.getBoundsInLocal().getWidth() / 2); // put ship in center
		myShip.setY(screenHeight/2 - myShip.getBoundsInLocal().getHeight() / 2); 
		root.getChildren().add(myShip);
		
		// redraw bounds for a bigger box
		createBounds(screenWidth, screenHeight, 60, (float) 1/4);
	
		bossLevel = true;
		myBoss = new Boss();
		myBoss.drawEnemy(root, screenWidth/2, 0, bossFilename); // use which boss filename
	}

	// creates the invisible walls which the level 1 enemies and the boss bounce off of
	private void createBoundsLevel_1(int width, int height, int borderOffset, float topBoundMultiplier) {
		setupRectangle(rightMetroidBound, -5, 0, 5, height);
		setupRectangle(leftMetroidBound, width, 0, 5, height);
		createBounds(width, height, borderOffset, topBoundMultiplier);
	}

	private void createBounds(int width, int height, int borderOffset, float topBoundMultiplier) {
		int borderThickness = 5;
		float topCornerHeight = topBoundMultiplier * height;
		float botLeftCornerHeight = height - borderOffset - borderThickness;
		float boundsHeight = botLeftCornerHeight - topCornerHeight;
		float boundsWidth = width - 2*borderOffset;
		float topRightCornerX = width - borderOffset - borderThickness;

		setupRectangle(topBound, borderOffset, topCornerHeight, boundsWidth, borderThickness);
		setupRectangle(bottomBound, borderOffset, botLeftCornerHeight, boundsWidth, borderThickness);
		setupRectangle(leftBound, borderOffset, topCornerHeight, borderThickness, boundsHeight);
		setupRectangle(rightBound, topRightCornerX, topCornerHeight, borderThickness, boundsHeight);
	}

	// creates boundaries for player ship
	private void setupRectangle(Rectangle r, double X, double Y, double width, double height) {
		root.getChildren().remove(r);
		r.setX(X);
		r.setY(Y);
		r.setWidth(width);
		r.setHeight(height);
		r.setFill(Color.WHITESMOKE);
		root.getChildren().add(r);
	}

	// complete teardown: remove all enemies, boss, boss attacks, player ship, missiles, player bounds
	private void cleanUp() {
		for (Metroid met : myMetroids) {
			met.removeEnemy(root);
		}
		myMetroids.clear();

		for (Missile mis : myMissiles) {
			mis.destroyMissile(root);
		}
		myMissiles.clear();

		if (!(myBoss == null)) {
			myBoss.removeEnemy(root);
		}

		for (BossAttack_Masterpiece ba : bossAttacks) {
			ba.cleanUp(root);
		}
		bossAttacks.clear();

		root.getChildren().remove(progressToBoss);
		root.getChildren().remove(restartFromBeginning);
		root.getChildren().remove(myShip);
		root.getChildren().remove(topBound);
		root.getChildren().remove(bottomBound);
		root.getChildren().remove(leftBound);
		root.getChildren().remove(rightBound);

	}

	// method to make win game message
	private Text makeDesiredText(String message, int width, int height) { 
		Text textToPlace = new Text(message);
		textToPlace.setFont(Font.font("Argentina", FontWeight.BOLD, 50));
		textToPlace.setFill(Color.ORANGERED);
		textToPlace.setX(width/2 - textToPlace.getBoundsInParent().getWidth() / 2);
		textToPlace.setY((float) 6/7 * height - textToPlace.getBoundsInParent().getHeight() / 2);
		return textToPlace;
	}
	
	// enable machine gun-like fire with the machine gun cheat
	public void setMachineGunMode() {
		missileFiringRate = 50 ;
	}
	
	// prevent boss attacks from hitting player with the invincibility cheat enabled
	public void setInvincibilityMode() {
		invincible = true;
	}

}
