/**
 * BOMPlugin - Plugin to compute the Bill of Materials (BOM) for walls.
 * 
 * @version 1.0
 * @since 2023-10-05
 */

package com.eteks.test;

import com.eteks.sweethome3d.plugin.Plugin;

import com.eteks.sweethome3d.plugin.PluginAction;
import javax.swing.JOptionPane;
import com.eteks.sweethome3d.model.HomePieceOfFurniture;
import com.eteks.sweethome3d.model.Wall;
import java.util.ArrayList;
import java.util.List;

import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class BOMPlugin extends Plugin {

	// the following are a list of the material definitions that will be used in the
	// project
    private List<BrickWall> brickWallsList;
	private Brick brick;
	private Brick halfbrick;
	private Brick channelbrick;
	private Brick halfchannelbrick;
	private float rebarlength;
	private float subwallheight;
	private float tolerance;
	private String outputFileName = "billofmaterials.txt";
	
	// exterior bounds
    float home_minX = Float.MAX_VALUE;
    float home_minY = Float.MAX_VALUE;
    float home_maxX = Float.MIN_VALUE;
    float home_maxY = Float.MIN_VALUE;
	

    
    // Enum for wall types
	
    public enum WallType {
        exterior,
        interior,
        notSet
    }

    public enum WallOrientation {
    	notSet,
    	horizontal,
    	vertical
    }   
        
	// Constructor
	public BOMPlugin() {
		// the following variables set the dimensions of the building rules and
		// materials used in the project
		// Initialize the brick objects to avoid null pointer exceptions
		// brick initialization is width, length, height

		this.brick = new Brick((float) 12.5, 25, (float) 6.25);
		this.halfbrick = new Brick((float) 12.5, (float) 12.5, (float) 6.25);
		this.channelbrick = new Brick((float) 12.5, 25, (float) 6.25);
		this.halfchannelbrick = new Brick((float) 12.5, (float) 12.5, (float) 6.25);
		this.rebarlength = (float) 100;
		this.subwallheight = (float) 100;
		this.tolerance = 0.1f;
		this.outputFileName = "./billofmaterials.txt";
        this.brickWallsList = new ArrayList<>();

	}

	// Add the getter and setter methods for accessing the private variables
	// getters
	public Brick getBrick() {
		return brick;
	}

	public Brick getHalfBrick() {
		return halfbrick;
	}

	public Brick getChannelBrick() {
		return channelbrick;
	}

	public Brick getHalfChannelBrick() {
		return halfchannelbrick;
	}

	public float getSubwallHeight() {
		return subwallheight;
	}

	public float getRebarLength() {
		return rebarlength;
	}

	public String getOutputFileName() {
		return outputFileName;
	}

	public void setSubwallHeight(float height) {
		this.subwallheight = height;
	}

	public void setRebarLength(float length) {
		this.rebarlength = length;
	}

	public void setOutputFileName(String outputFileName) {
		this.outputFileName = outputFileName;
	}

	// method to get the actions, needed because we have extra actions
	@Override
	public PluginAction[] getActions() {
		return new PluginAction[] { new BOMAction(), new BOMSettings(this) };
	}

	// Create a new class BOMAction that extends PluginAction
	public class BOMAction extends PluginAction {
		public BOMAction() {
			putPropertyValue(Property.NAME, "Compute Bill of Materials");
			putPropertyValue(Property.MENU, "Bill of Materials");
			// Enables the action by default
			setEnabled(true);
		}

		// Add the following execute method to BOMAction.java, it's the main action
		@Override
		public void execute() {
	         // Array to hold BrickWall objects
            // house level parameters
			float numWalls = getHome().getWalls().size();
			// Height related parameters
			float totalBricksInLayer = 0;
			float numLayers = 0;
			float numBrickLayers = 0;
			float subwallLayers = 0;
			float totalChannelBricks = 0;
			float totalBricks = 0;
			float totalHalfBricks =0;
			float totalHalfChannelBricks = 0;
			float wallHeight = 0;

			StringBuilder summary = new StringBuilder();
			StringBuilder walls = new StringBuilder();
			
			// Title
			summary.append("Bill of Materials Summary:\n");
			walls.append("Wall Description \n");
			
            // figure out exterior bounds
			getExteriorBounds();

			// Build list of walls and establish the exterior walls
			for (Wall wall : getHome().getWalls()) {
				// build the list of walls and establish the exterior vs interior
				// plus, put in the windows and door
                BrickWall brickWall = new BrickWall(wall.getXStart(), wall.getYStart(), wall.getXEnd(), wall.getYEnd() ,wall.getThickness(), wall.getHeight(), tolerance, subwallheight); 
                BOMPlugin.this.brickWallsList.add(brickWall);
                //is this an exterior wall?
                setWallType(brickWall);
                
                // Add doors or windows intersecting with this wall
				for (HomePieceOfFurniture piece : getHome().getFurniture()) {
					if (piece.isDoorOrWindow()) {
						// figure out if the door or window intersects with the wall and if so, add it to the wall
						brickWall.addDoorOrWindow(piece);
					}
				}
				
				// now build the wall, which calculates the materials needed for the wall	
				brickWall.buildWall(brick, halfbrick);
				
				// build the wall description
				walls.append(brickWall.getWallDescription());
			}
			
            
			
			// Compute the window and door area in bricks
			float windowDoorAreaInBricks = getWindowDoorAreaInBricks();		

			// subtract the windows and doors from the total bricks
			totalBricks -= windowDoorAreaInBricks;

			// Materials needed for the project
			summary.append("\nMaterials Needed for Project: \n");
			summary.append("Total Bricks Needed for Project: ").append(totalBricks).append("\n");
			summary.append("Total Half Bricks Needed for Project: ").append(totalHalfBricks).append("\n");
			summary.append("Total Channel Bricks Needed For Project: ").append(totalChannelBricks).append("\n");
			summary.append("Total Half Channel Bricks Needed For Project: ").append(totalHalfChannelBricks).append("\n");

			// information about the house
			summary.append("\nHouse Information: \n");
			summary.append("Number of Walls: ").append(numWalls).append("\n");
			summary.append("Wall Height: ").append(wallHeight).append("cm \n");
			summary.append("Total Bricks in One Layer: ").append(totalBricksInLayer).append("\n");
			summary.append("Total Number of Layers: ").append(numLayers).append("\n");
			summary.append("Number of Brick Layers: ").append(numBrickLayers).append("\n");
			summary.append("Number of Subwall Layers: ").append(subwallLayers).append("\n");

			summary.append("Window and Door Area in Bricks: ").append(windowDoorAreaInBricks).append("\n");

			// information about the materials
			summary.append("\nMaterials Information: \n");
			summary.append("Brick Dimensions: ").append(brick.getLength()).append("cm  x ").append(brick.getWidth())
					.append("cm  x ").append(brick.getHeight()).append("cm\n");
			summary.append("Half Brick Dimensions: ").append(halfbrick.getLength()).append("cm x ")
					.append(halfbrick.getWidth()).append("cm x ").append(halfbrick.getHeight()).append("cm\n");


			outputBillOfMaterials(summary.toString());
			outputBillOfMaterials(walls.toString());

		} // end of execute method

	} // end of BOMAction class

	public void outputBillOfMaterials(String billOfMaterials) {

		// Display the contents of billOfMaterials on the screen
		JOptionPane.showMessageDialog(null, billOfMaterials);

		// Check if the output file exists, if not create it
		File file = new File(outputFileName);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				System.err.println("Error creating file: " + outputFileName);
				e.printStackTrace();
			}
		}

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName, true))) {
			writer.write(billOfMaterials);
			writer.newLine();
		} catch (IOException e) {
			System.err.println("Error writing to file: " + outputFileName);
			e.printStackTrace();
		}
	}

	public float getWindowDoorAreaInBricks() {
		float windowDoorAreaInBricks = 0;
		float doororwindowheight = 0;
		float doororwindowwidth = 0;
		float dowheightinbricks = 0;
		float dowwidthinbricks = 0;

		for (HomePieceOfFurniture piece : getHome().getFurniture()) {
			if (piece.isDoorOrWindow()) {
				doororwindowheight = piece.getHeight();
				dowheightinbricks = Math.round(doororwindowheight / brick.getHeight());
				doororwindowwidth = piece.getWidth();
				dowwidthinbricks = Math.round(doororwindowwidth / brick.getLength());
				windowDoorAreaInBricks += dowheightinbricks * dowwidthinbricks;
			}
		}
		return windowDoorAreaInBricks;
	}
	
 
    public void getExteriorBounds() {

        for (Wall wall : getHome().getWalls()) {
            float xStart = wall.getXStart();
            float yStart = wall.getYStart();
            float xEnd = wall.getXEnd();
            float yEnd = wall.getYEnd();

            if (xStart < home_minX) home_minX = xStart;
            if (yStart < home_minY) home_minY = yStart;
            if (xEnd > home_maxX) home_maxX = xEnd;
            if (yEnd > home_maxY) home_maxY = yEnd;
        }

    }   
    
    public void setWallType(BrickWall wall) {
        float xStart = wall.getXStart();
        float yStart = wall.getYStart();
        float xEnd = wall.getXEnd();
        float yEnd = wall.getYEnd();

        if ((xStart == home_minX || xStart == home_maxX || xEnd == home_minX || xEnd == home_maxX) ||
            (yStart == home_minY || yStart == home_maxY || yEnd == home_minY || yEnd == home_maxY)) {
            wall.setWallType(BOMPlugin.WallType.exterior);
        } else {
            wall.setWallType(BOMPlugin.WallType.interior);
        }
    }

} // end of BOMPlugin class
