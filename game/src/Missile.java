import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Allows for a self-contained missile class. These missiles serve as the player's attack--they are spawned by the player and 
 * fly towards the top of the screen by themselves. Their ImageView may be passed into the game object for purposes of 
 * bounds intersection.
 * 
 * Dependencies: MegaInvaders.java must continually update the missile's position, and must check for any collisions. 
 * 
 * @author Filip Mazurek
 */

public class Missile{
	
	private static final int MISSILE_SPEED = 5;
	private ImageView myMissile;
	private double xPos;
	private double yPos;
	
	/**
	 * draw the missile using the filename. Draw so at the location specified--in front of the player
	 * 
	 * @param root
	 * @param location
	 * @param filename
	 */
	public void drawMissile(Group root, ImageView location, String filename) {
		// get information for the missile sprite
		Image image = new Image(getClass().getClassLoader().getResourceAsStream(filename));
        myMissile = new ImageView(image);
        // get location for missile spawn
        xPos = location.getX() + location.getBoundsInLocal().getWidth() / 2 - 2;
        yPos = location.getY() - location.getBoundsInLocal().getHeight() / 3;
        myMissile.setX(xPos);
        myMissile.setY(yPos);
        // draw the missile
        root.getChildren().add(myMissile);
	}
	
	/**
	 * allow the missile to fly upwards
	 */
	public void updatePos() {
		yPos = yPos - MISSILE_SPEED;
		myMissile.setY(yPos);
	}
	
	/**
	 * remove the missile from the game screen so that it doesn't take up resources when beyond view
	 * @param root
	 */
	public void destroyMissile(Group root) {
		root.getChildren().remove(myMissile);
	}

	public double getX() {
		return xPos;
	}
	
	public double getY() {
		return yPos;
	}
	
	public ImageView getMissileView() {
		return myMissile;
	}
}
