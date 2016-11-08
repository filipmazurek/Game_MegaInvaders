import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;

/**
 * Main of the Mega Invaders game. Sets up the main menu and the main game
 * 
 * @author Filip Mazurek
 */

public class Main extends Application {
	private static final int SIZE = 700;
	private static final int FRAMES_PER_SECOND = 60;
	private static final int MILLISECOND_DELAY = 1000 / FRAMES_PER_SECOND;
	String keysEntered;

	/**
	 * Sets up by creating a main menu and displaying it, then on button press, will run the game.
	 * Cheat keys may be entered on the main menu screen.
	 */
	@Override
	public void start (Stage s) {
		// make the stage not resizable
		s.setResizable(false);
		TopMenu myMenu = new TopMenu();
		MegaInvaders myGame = new MegaInvaders();
		s.setTitle(myMenu.getTitle());
		
		// create button to start default game
		Button startGameButton = new Button("Start Game");
		startGameButton.setOnAction(e -> initializeGame(s, myGame));

		Scene menuScene = myMenu.initMenu(SIZE, SIZE, startGameButton);
		
		// catch keys in background in main menu for use in cheats
		menuScene.setOnKeyPressed(e -> checkCheat(e.getCode(), s, myGame, myMenu));

		s.setScene(menuScene);
		s.show();
	}
	
	// if keys pressed spell a phrase, update some game information for cheat
	private void checkCheat(KeyCode code, Stage s, MegaInvaders myGame, TopMenu menu) {
		keysEntered += code.toString();
		System.out.println(keysEntered);
		if (keysEntered.contains("BULLET")) {
			menu.setMachineGunConfirmation();
			myGame.setMachineGunMode();
		}
		if (keysEntered.contains("NOHIT")) {
			menu.setInvincibilityConfirmation();
			myGame.setInvincibilityMode();
		}
	}

	/**
	 * Starts what will be the game loop through the repeating event handler
	 * 
	 * @param s
	 * @param myGame
	 */
	public void initializeGame(Stage s, MegaInvaders myGame) {
		Scene gameScene = myGame.init(SIZE, SIZE);
		s.setScene(gameScene);
		s.show();

		// sets the game's loop
		KeyFrame frame = new KeyFrame(Duration.millis(MILLISECOND_DELAY),
				e -> myGame.step());
		Timeline animation = new Timeline();
		animation.setCycleCount(Timeline.INDEFINITE);
		animation.getKeyFrames().add(frame);
		animation.play();
	}

	/**
	 * Start the program.
	 */
	public static void main (String[] args) {
		launch(args);
	}
}
