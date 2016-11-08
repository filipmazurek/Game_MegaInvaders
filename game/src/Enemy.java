import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * This abstract class allows for generic enemy creation. It allows for an enemy to be placed in the scene at a specified location.
 * Additionally, this class includes the possibilities for individual enemies to progress through different poses, which will 
 * gives the illusion of motion. This requires that the separate enemy pose files are named the same--the only difference
 * is that immediately before the extension, and underscore and a number appears. Poses must begin at 1 and may continue until
 * the specified upper bound in each class that inherits from this. 
 * 
 * Dependencies: MegaInvaders.java must pass sensible arguments. Functions must be called in the correct order.
 * 
 * @author Filip Mazurek
 */
public abstract class Enemy {
	protected int direction = 1;
	protected boolean undrawn = true; // on object creation, set boolean to execute loop on first time drawing enemy
	protected ImageView myEnemy;
	protected double xPos;
	protected double yPos;
	protected String enemyFilename;
	protected String fileExtension;
	protected int currentPose;
	protected int totalPoses;
	private int health;
	private boolean incrementPose;
	private double enemyWidth;
	private double enemyHeight;	
	
	/**
	 * Draw the desired enemy specified by the filename in the coordinates given in the Group given
	 * extract file name information upon drawing for use by the progressPose() function
	 * 
	 * @param root
	 * @param drawX
	 * @param drawY
	 * @param filename
	 */
	public void drawEnemy(Group root, double drawX, double drawY, String filename) {
		Image image = new Image(getClass().getClassLoader().getResourceAsStream(filename));
        myEnemy = new ImageView(image);
        // set enemy parameters
		enemyWidth = myEnemy.getBoundsInParent().getWidth();
		enemyHeight = myEnemy.getBoundsInParent().getHeight();
        // get location for enemy spawn
        xPos = drawX;
        yPos = drawY;
        myEnemy.setX(xPos);
        myEnemy.setY(yPos);
        // draw the enemy
        root.getChildren().add(myEnemy);
        // get file information for changing poses
		if (undrawn) {
			currentPose = Integer.parseInt(filename.substring(filename.lastIndexOf('_') + 1, filename.lastIndexOf('.')));
			enemyFilename = filename.substring(0, filename.lastIndexOf('_')); // remove position number and file extension
			fileExtension = filename.substring(filename.lastIndexOf('.')); // save only the extension
		}

	}

	/**
	 * Draw a progression of the enemy pose. Most enemies will have multiple poses, which they will change during gameplay
	 * @param root
	 */
	public void progressPose(Group root) {
		
		if (currentPose >= totalPoses) {
			incrementPose = false;
		}
		if (currentPose <= 1) {
			incrementPose = true;
		}
		if (incrementPose) {
			currentPose++;
		}
		else {
			currentPose--;
		}
		
		String newFilename = enemyFilename + "_" + Integer.toString(currentPose) + fileExtension;
		removeEnemy(root);
		drawEnemy(root, xPos, yPos, newFilename);	
	}	

	// method invoked when bouncing off walls
	protected void changeDirection() {
		if (direction == 1) {
			direction = -1;
		}
		else {
			direction = 1;
		}
	}
	
	/**
	 * when hit by a missile, update the enemy health, and remove the enemy if the health reaches 0
	 * 
	 * @param root
	 */
	public void updateHealth(Group root) {
		health--;
		if (health <= 0) {
			removeEnemy(root);
		}
	}
	
	protected void removeEnemy(Group root) {
		root.getChildren().remove(myEnemy);
	}
	
	public int getHealth() {
		return health;
	}
	
	protected void setHealth(int newHealth) {
		health = newHealth;
	}
	
	public double getHeight() {
		return enemyHeight;
	}
	
	public double getWidth() {
		return enemyWidth;
	}
}
