import javafx.geometry.Bounds;
import javafx.scene.Group;

// This entire file is part of my masterpiece.
// Filip Mazurek

// This class is my masterpiece because of how much more readable and modular it is compared to my original render of it. 
// Rather than trying to set everything up like in the original BossAttack.java class, I split it up into two different
// classes. This top class is more readable to the coder, and executes the attack by help and calculations hidden within
// the newly made BossAttackContainer_Masterpiece.java class. Some meaningless calculations were replaced by named 
// variables, e.g. timePassed, rather than a calculation. Additionally, now if the arguments for this class would be 
// accidentally called out of order, then nothing would happen. The class would be initialized, but it would not 
// interfere with the main game in any way. 
// The biggest drawback of this code is that it still needs to have functions like cleanUp() and must pass the beam bounds
// because of how the game was originally designed.

/**
 * @author Filip Mazurek
 */

public class BossAttack_Masterpiece {

	private static final double BEAM_DELAY_MILLIS = 650; // time before the beam is fired
	private static final double BEAM_TIME_MILLIS = 600; // time the beam is on the screen

	private BossAttackContainer_Masterpiece blastersContainer;
	private long attackStartTime;
	private String nextBlasterFilename;
	private boolean beamStarted;
	

	/**
	 * Terminology: 
	 * 		Blasters: non-gameplay images outside the player's bounds. Serve as a warning to where the beam will be fired.
	 * 		Beam: the part of the attack that resembles a laserbeam across the screen. It may damage the player.
	 * 
	 * The initializer function determines at which vertical position the attack will spawn. It is assumed to be the player's
	 * position for the game, but any position works well. Two images must be in the Images folder. One is of the blasters'
	 * initial look, the second is of the blasters' progressed look. The two images are to be named in the fashion:
	 * 'name_1.extension' and 'name_2.extension', where the names and extensions are the same. 
	 * 
	 * @param yPos: position where the attack will spawn.
	 * @param screenWidth
	 * @param filename: file where the initial blaster image is stored.
	 */
	BossAttack_Masterpiece(double yPos,  double screenWidth, String filename) {
		blastersContainer = new BossAttackContainer_Masterpiece(filename, screenWidth, yPos);
		nextBlasterFilename = makeNextFilename(filename);
	}

	/**
	 * Definition of beginning the attack is for timing's sake. It is here that the attack officially starts, and so the timer
	 * begins to run for the duration of the attack, as defined by the two static final doubles.
	 * 
	 * @param root
	 */
	public void beginAttack(Group root) {
		beamStarted = false;
		blastersContainer.displayAllContents(root);
		attackStartTime = System.currentTimeMillis();
	}

	/**
	 * This is to be called every frame. The time checks allow for the beam to be fired from the blasters, and then for the 
	 * attack to be cleaned up from the screen. 
	 * 
	 * @param root
	 * @return boolean: returns whether the attack is still occuring. 
	 */
	public boolean progressAttack(Group root) {
		double timePassed = System.currentTimeMillis() - attackStartTime;
		double percentBeamDone = (timePassed - BEAM_DELAY_MILLIS) / BEAM_TIME_MILLIS;

		if (!beamStarted && (timePassed >= BEAM_DELAY_MILLIS)) { // execute once after the time has passed by starting the beam
			blastersContainer.cleanUpBlasters(root);
			blastersContainer.setNewBlasterImages(nextBlasterFilename);
			blastersContainer.displayBlasters(root);
			blastersContainer.startBeam();
			beamStarted = true;
		}

		if (beamStarted) { // once the beam is started, continually widen it.
			blastersContainer.calculateAndSetBeamLook(percentBeamDone);
		}

		if (percentBeamDone >= 1) { // after the attack has passed its duration, remove the attack from the screen. 
			blastersContainer.cleanUpAll(root);
			return false;
		}
		return true;
	}

	/**
	 * Accessory function to let the player know when they were hit. 
	 */
	public void setBeamRed() {
		blastersContainer.setBeamRed();
	}
	
	/**
	 * Remove all attack contents from the screen.
	 * 
	 * @param root
	 */
	public void cleanUp(Group root) {
		blastersContainer.cleanUpAll(root);
	}
	
	public Bounds getBeamBounds() {
		return blastersContainer.getBeamBounds();
	}

	// extracts information from the given filename to be used to call the next look of blasters.
	private String makeNextFilename(String filename) {
		String filenameWithoutPosition = filename.substring(0, filename.lastIndexOf('_')); // remove position number 
		String fileExtension = filename.substring(filename.lastIndexOf('.')); 
		String nextFilename = filenameWithoutPosition + "_2" + fileExtension;
		return nextFilename;
	}
}
