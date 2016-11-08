import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.shape.Circle;

/**
 * Class set up to display a main menu image and display a button which will 
 * trigger an event handler to initialize the main game from Main.
 * Cheats may be entered here thanks to the event handler created in Main.
 * 
 * Dependencies: must be passed in the correct button from Main so that the MegaInvaders game may be started.
 * 
 * @author Filip Mazurek
 */
public class TopMenu {
	
	private static final String TITLE = "Mega Invaders";
	private Group root;
	
	/**
	 * Returns name of the game.
	 * @return
	 */
	public String getTitle () {
		return TITLE;
	}
	
	/**
	 * Loads main menu image and displays it, along with title and instruction text.
	 * 
	 * @param width
	 * @param height
	 * @param exitButton
	 * @return
	 */
	public Scene initMenu(int width, int height, Button exitButton) {
		root = new Group();
		
		Scene myScene = new Scene(root, width, height, Color.BLACK);
		
		ImageView background = makeDesiredImageView("Menu_background.jpg", 0, 0); // allow to fit the screen
		root.getChildren().add(background);
		
		// setup the button that was passed in to have ability to initialize the game		
		exitButton.setLayoutX(width/2 - exitButton.getBoundsInParent().getWidth());
		exitButton.setLayoutY((float) 1/2 * height - exitButton.getBoundsInParent().getHeight());
		root.getChildren().add(exitButton);
		
		Text titleText = makeDesiredText("MEGA INVADERS", width, height, 40);
		root.getChildren().add(titleText);
		
		Text instructionText = makeDesiredText("Move with the WASD keys\nand fire missiles using J.\nShoot all the enemies!", width, height, 25);
		instructionText.setY(instructionText.getY() + 100);
		root.getChildren().add(instructionText);
		
		return myScene;
	}
	
	// centered horizontally, vertical placement looks fine under title image
	private Text makeDesiredText(String message, int width, int height, int fontSize) { 
		Text textToPlace = new Text(message);
		textToPlace.setFont(Font.font("Argentina", FontWeight.BOLD, fontSize));
		textToPlace.setFill(Color.ALICEBLUE);
		textToPlace.setX(width/2 - textToPlace.getBoundsInParent().getWidth() / 2);
		textToPlace.setY((float) 5.5/7 * height - textToPlace.getBoundsInParent().getHeight() / 2);
		return textToPlace;
	}
	
	// creates an ImageView set at the desired location. Must be added to root after return.
	private ImageView makeDesiredImageView(String imageTitle, double posX, double posY) {
		Image image = new Image(getClass().getClassLoader().getResourceAsStream(imageTitle));
		ImageView myImageView = new ImageView(image);
		myImageView.setX(posX);
		myImageView.setY(posY);
		return myImageView;
	}
	
	/**
	 * Visual representation that the SNOOP cheat was enabled
	 * by drawing a green circle in the top left corner of title screen.
	 */
	public void setSnoopConfirmation() {
		makeCircle(20, Color.GREEN);
	}
	
	/**
	 * Visual representation that the BULLET cheat was enabled
	 * by drawing a RED circle in the top left corner of title screen.
	 */
	public void setMachineGunConfirmation() {
		makeCircle(60, Color.RED);
	}
	
	/**
	 * Visual representation that the NOHIT cheat was enabled
	 * by drawing a BLUE circle in the top left corner of title screen.
	 */
	public void setInvincibilityConfirmation() {
		makeCircle(100, Color.BLUE);
	}
	
	private void makeCircle(double centerX, Color myColor) {
		Circle myCircle = new Circle(centerX, 25, 20);
		myCircle.setFill(myColor);
		root.getChildren().add(myCircle);
	}
	
}
