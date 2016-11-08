import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.geometry.Bounds;

// This entire file is part of my masterpiece.
// Filip Mazurek

// This class is part of my masterpiece because it helps implement the BossAttack_Materpiece.java class. Here is where the more
// complex calculations are hidden. All the code is just small functions that are accessory to the BossAttack_Masterpiece.java
// class.

public class BossAttackContainer_Masterpiece {
	private static final int BEAM_THICKNESS = 16; // height of the beam when it shoots across the screen
	
	private ImageView blasterLeft;
	private ImageView blasterRight;
	private Rectangle beam;
	private double blasterWidth;
	private double blasterHeight;
	private double beamWidth;

	/**
	 * Initializer for the container retrieves necessary images and sets to where they would be. They are not displayed yet.
	 * 
	 * @param filename
	 * @param screenWidth
	 * @param yPos
	 */
	BossAttackContainer_Masterpiece(String filename, double screenWidth, double yPos) {
		loadAndSetImageViews(filename);
		
		blasterWidth = blasterLeft.getBoundsInParent().getWidth();
		blasterHeight = blasterLeft.getBoundsInParent().getHeight();
		setBlastersY(yPos);
		setBlastersX(screenWidth - blasterWidth);
		
		initializeBeam(screenWidth);
	}
	
	/**
	 * BossAttack_Materpiece.java decides to update the look of the blasters, this allows just the look to switch, but for the
	 * x and y positions to remain as they were.
	 * 
	 * @param filename
	 */
	public void setNewBlasterImages(String filename) {
		double yPos= blasterLeft.getY();
		double xPosRight = blasterRight.getX();

		loadAndSetImageViews(filename);

		blasterLeft.setY(yPos);
		blasterLeft.setX(0);

		blasterRight.setY(yPos);
		blasterRight.setX(xPosRight);
	}
	
	/**
	 * For the first half of the attack, the beam expands. After, the beam remains at full width. 
	 * 
	 * @param fractionDone
	 */
	public void calculateAndSetBeamLook(double fractionDone) {
		if (fractionDone <= 0.5) {
			beam.setHeight(BEAM_THICKNESS * fractionDone * 2);
			beam.setY(blasterLeft.getY() + blasterHeight/2 - fractionDone * blasterHeight / 2);
		}
	}
	
	/**
	 * remove all attack images from the screen.
	 * 
	 * @param root
	 */
	public void cleanUpAll(Group root) {
		root.getChildren().remove(beam);
		cleanUpBlasters(root);
	}
	
	/**
	 * remove only the blasters from the screen.
	 * 
	 * @param root
	 */
	public void cleanUpBlasters(Group root) {
		root.getChildren().remove(blasterLeft);
		root.getChildren().remove(blasterRight);
	}
	
	/**
	 * allow all attack components to show up on the screen.
	 * 
	 * @param root
	 */
	public void displayAllContents(Group root) {
		root.getChildren().add(beam);
		displayBlasters(root);
	}
	
	/**
	 * allow only the blasters to show up on the screen.
	 * 
	 * @param root
	 */
	public void displayBlasters(Group root) {
		root.getChildren().add(blasterLeft);
		root.getChildren().add(blasterRight);
	}
	
	public double getWidth() {
		return blasterWidth;
	}

	public double getHeight() {
		return blasterHeight;
	}
	
	public Bounds getBeamBounds() {
		return beam.getBoundsInParent();
	}
	
	public void setBeamRed() {
		beam.setFill(Color.RED);
	}
	
	public void startBeam() {
		beam.setWidth(beamWidth);
		beam.setHeight(0);
	}
	
	// Get the rectangle that is the beam ready for deployment
	private void initializeBeam(double screenWidth) {
		beam = new Rectangle();
		double beamY = blasterLeft.getY() + blasterHeight / 2;
		beam.setY(beamY);
		beam.setX(blasterWidth);
		beamWidth = screenWidth - blasterWidth * 2;
		beam.setFill(Color.WHITESMOKE);
	}
	
	// take the blaster image and load it. The blaster on the right is the same blaster look as on the left, but mirrored.
	private ImageView[] loadImages(String filename) {
		Image image1 = new Image(getClass().getClassLoader().getResourceAsStream(filename));
		ImageView blaster_left = new ImageView(image1);
		Image image2 = new Image(getClass().getClassLoader().getResourceAsStream(filename));
		ImageView blaster_right = new ImageView(image2);
		blaster_right.setScaleX(-1);
		ImageView[] blasters = new ImageView[]{blaster_left, blaster_right};
		return blasters;
	}
	
	// assign the left and right blsaters after loading.
	private void loadAndSetImageViews(String filename) {
		ImageView[] blasterImages = loadImages(filename);
		blasterLeft = blasterImages[0];
		blasterRight = blasterImages[1];
	}

	// modularized way to assign the y position of both blasters
	private void setBlastersY(double y) {
		blasterLeft.setY(y);
		blasterRight.setY(y);
	}

	// modular way to set the right blaster to the correct position--left blaster remains on the edge.
	private void setBlastersX(double x) {
		blasterLeft.setX(0);
		blasterRight.setX(x);
	}
	
}
