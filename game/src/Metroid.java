import javafx.scene.Group;

/**
 * This class is for the basic level 1 enemies, and it extends the Enemy class. When drawn, these level 1 enemies are given 1 health
 * so that they are immediately defeated upon being hit by the player. The class gives tools to the game class in the form of updating
 * horizontal and vertical position; when the main game detects that the enemies reach an obstacle, it may tell the enemies to move 
 * down and then change their movement direction. 
 * 
 * Dependencies: all from Enemy.java. Also, MegaInvaders.java must manually call the changeDirection() method when it detects a 
 * collision.
 * 
 * @author Filip Mazurek
 */
public class Metroid extends Enemy{
	private static final double METROID_SPEED = 2.5;
	private static final int METROID_POSES = 2;


	/**
	 * add functionality to previous code by setting specific health to the level 1 enemies
	 */
	@Override
	public void drawEnemy(Group root, double drawX, double drawY, String filename) {
		super.drawEnemy(root, drawX, drawY, filename);
		if (undrawn) {
			undrawn = false;
			this.setHealth(1);
			totalPoses = METROID_POSES;
		}
	}

	/**
	 * Move the level 1 enemy horizontally in one direction
	 */
	public void updatePositionHorizontal() {
		xPos = xPos + direction * METROID_SPEED;
		myEnemy.setX(xPos);
	}

	/**
	 * upon hitting a wall, as detected in the class file, call the function to make the enemies move down 
	 */
	public void updatePositionVertical() {
		yPos = yPos + this.getHeight();
		myEnemy.setY(yPos);
		// while at it, also change the horizontal direction
		changeDirection();
	}	
}
