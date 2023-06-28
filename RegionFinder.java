import java.awt.*;
import java.awt.image.*;
import java.util.*;

/**
 * Region growing algorithm: finds and holds regions in an image.
 * Each region is a list of contiguous points with colors similar to a target color.
 * Scaffold for PS-1, Dartmouth CS 10, Fall 2016
 * 
 * @author Chris Bailey-Kellogg, Winter 2014 (based on a very different structure from Fall 2012)
 * @author Travis W. Peters, Dartmouth CS 10, Updated Winter 2015
 * @author CBK, Spring 2015, updated for CamPaint
 * @author aryandawer, Dartmouth CS 10, Fall 2022
 */
public class RegionFinder {
	private static final int maxColorDiff = 30;				// how similar a pixel color must be to the target color, to belong to a region
	private static final int minRegion = 20; 				// how many points in a region to be worth considering

	private BufferedImage image;                            // the image in which to find regions
	private BufferedImage recoloredImage;                   // the image with identified regions recolored

	private ArrayList<ArrayList<Point>> regions;			// a region is a list of points
															// so the identified regions are in a list of lists of points

	private BufferedImage visited;							// an image the size of the actual image to check visited pixels
															// through color representation

	// Constructors ---------------------------------------------------------------
	public RegionFinder() {
		this.image = null;
	}
	public RegionFinder(BufferedImage image) {
		this.image = image;		
	}
	// ----------------------------------------------------------------------------

	// Setters --------------------------------------------------------------------
	public void setImage(BufferedImage image) {
		this.image = image;
	}
	// ----------------------------------------------------------------------------

	// Getters --------------------------------------------------------------------
	public BufferedImage getImage() {
		return image;
	}
	public BufferedImage getRecoloredImage() {
		return recoloredImage;
	}
	// ----------------------------------------------------------------------------

	/**
	 * Sets regions to the flood-fill regions in the image, similar enough to the trackColor.
	 */
	public void findRegions(Color targetColor) {
		// TODO: YOUR CODE HERE
		// Initializes an arraylist of arraylists with all points included in a region
		regions = new ArrayList<ArrayList<Point>>();

		// An image the size of the actual image to check visited pixels through color representation
		visited = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

		//Traversing over all the pixels in the image
		for(int y=0;y<image.getHeight();y++){
			for(int x=0;x<image.getWidth();x++){

				// Checking if pixel not visited and matches color and moving ahead if both true
				if(visited.getRGB(x,y)==0 && colorMatch(targetColor,new Color(image.getRGB(x,y)))){

					visited.setRGB(x,y,1); 						// marks pixel as visited

					ArrayList<Point> region = new ArrayList<Point>(); 		// starts new region
					ArrayList<Point> toVisit = new ArrayList<Point>(); 		// starts new list to add points that need to be visited

					Point currPoint = new Point(x,y);					// sets point to variable

					toVisit.add(currPoint); 							// Adds starting point of the region to the toVisit list

					while(toVisit.size()>0){							// Starts loop to find all points in the region

						Point curr = toVisit.remove(0);			// Removes point in consideration from toVisit list and
																		// sets it as current point
						// Starts another loop to iterate via 8 neighbours of current point.
						for(int smallY=curr.y-1;smallY<curr.y+2;smallY++){
							for(int smallX=curr.x-1;smallX<curr.x+2;smallX++){

								// Checking if neighbour pixel is valid (in the frame of the image) and unvisited
								if(neighbourValid(new Point(smallX,smallY)) && visited.getRGB(smallX,smallY)==0){

									visited.setRGB(smallX,smallY,1); 			// marks pixel as visited

									// Check if neighbour pixel in consideration has same color as targetColor
									if (colorMatch(targetColor, new Color(image.getRGB(smallX,smallY)))){
										region.add(new Point(smallX,smallY));		// adds point to region with same color
										toVisit.add(new Point(smallX, smallY));		// makes it the next point whose neighbours
																					// are to be considered
									}
								}
							}
						}
					}
					// Adds a region of connected points of the same color to the list of regions to be colored
					if (region.size()>=minRegion){
						regions.add(region);
					}
				}
			}
		}
	}

	// Checking if neighbour pixel is valid (in the frame of the image)
	public boolean neighbourValid(Point currPoint){
		return (currPoint.x<image.getWidth() && currPoint.x>0 && currPoint.y<image.getHeight() && currPoint.y>0);
	}

	/**
	 * Tests whether the two colors are "similar enough" (your definition, subject to the maxColorDiff threshold, which you can vary).
	 */
	private static boolean colorMatch(Color c1, Color c2) {
		// TODO: YOUR CODE HERE

		// Checks if abs difference of each color component is less than or equal to the maxDifference threshold given
		// Returns true if it is.
		int redThres = Math.abs(c1.getRed() - c2.getRed());
		int greenThres = Math.abs(c1.getGreen() - c2.getGreen());
		int blueThres = Math.abs(c1.getBlue() - c2.getBlue());

		return (redThres<=maxColorDiff && blueThres<=maxColorDiff && greenThres<=maxColorDiff);
	}

	/**
	 * Returns the largest region detected (if any region has been detected)
	 */
	public ArrayList<Point> largestRegion() {
		// TODO: YOUR CODE HERE

		// Simple logic to find largest region out of all regions
		ArrayList<Point> largestReg = new ArrayList<Point>();
		if (regions.size()!=0) {
			for (ArrayList<Point> region : regions) {
				if (region.size() > largestReg.size()) {
					largestReg = region;
				}
			}
		}
		return largestReg;
	}

	/**
	 * Sets recoloredImage to be a copy of image, 
	 * but with each region a uniform random color, 
	 * so we can see where they are
	 */
	public void recolorImage() {
		// First copy the original
		recoloredImage = new BufferedImage(image.getColorModel(), image.copyData(null), image.getColorModel().isAlphaPremultiplied(), null);
		// Now recolor the regions in it
		// TODO: YOUR CODE HERE

		// Starts a for each region in regions loop
		for(ArrayList<Point> region: regions){

			// picks a random color for each region
			Color randColor = new Color((int)(Math.random()*16777217));

			// Iterates through all points in the current region and colors them with chosen color
			for(int i=0;i<region.size();i++){
				Point currPoint = region.get(i);
				recoloredImage.setRGB(currPoint.x, currPoint.y, randColor.getRGB());
			}
		}
	}
}
