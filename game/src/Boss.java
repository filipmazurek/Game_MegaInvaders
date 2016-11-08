import javafx.scene.Group;

/**
 * Extension of the Enemy abstract class. The Boss class allows the developer to set specific properties of the boss, such as 
 * its health and movement speed. The boss's number of poses must be updated here in case different image files are desired to be 
 * used for the boss. 
 * The boss's movement is limited to horizontal movement. In order to not be too predictable in its movement, there is a random 
 * chance that the boss will switch directions.
 * 
 * Dependencies: all inherited from Enemy.java
 * 
 * @author Filip Mazurek
 */

public class Boss extends Enemy {

	private static final double BOSS_SPEED = 2.5;
	private static final int BOSS_HEALTH = 5;
	private static final int BOSS_POSES = 3;
	private static final double DIRECTION_CHANGE_CHANCE = .02;

	/**
	 * set the specified boss's health in addition to super
	 */
	@Override 
	public void drawEnemy(Group root, double drawX, double drawY, String filename) {
		super.drawEnemy(root, drawX, drawY, filename);
		if (undrawn) {
			undrawn = false;
			this.setHealth(BOSS_HEALTH);
			totalPoses = BOSS_POSES;
		}
	}
	
	/**
	 * Make the boss move horizontally across the screen. Allow the boss to randomly change direction
	 */
	public void updatePosition() {
		xPos = xPos + direction * BOSS_SPEED;
		myEnemy.setX(xPos);
		// add in possibility for random movement
		if (Math.random() < DIRECTION_CHANGE_CHANCE) {
			changeDirection();
		}
	}

}
