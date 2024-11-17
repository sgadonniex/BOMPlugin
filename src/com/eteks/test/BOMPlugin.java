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
import com.eteks.sweethome3d.model.PieceOfFurniture;
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
	private Brick brick;
	private Brick halfbrick;
	private Brick channelbrick;
	private Brick halfchannelbrick;
	private float rebarlength;
	private float subwallheight;
	private float tolerance;
	private String outputFileName = "billofmaterials.txt";

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
            List<BrickWall> brickWalls = new ArrayList<>();



			
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
			int wallIndex = 0;

			StringBuilder summary = new StringBuilder();
			StringBuilder walls = new StringBuilder();
			
			// Title
			summary.append("Bill of Materials Summary:\n");
			
			// Compute the window and door area in bricks
			float windowDoorAreaInBricks = getWindowDoorAreaInBricks();

			// Compute the number of bricks and half bricks in the first layer of each wall.
			// also get the height of the wall
			for (Wall wall : getHome().getWalls()) {
				// add a BrickWall object to the house
				// build the wall and get the number of bricks needed
                // Create a new BrickWall object and add it to the array
                BrickWall brickWall = new BrickWall(wall.getLength(), wall.getHeight(), tolerance, subwallheight, rebarlength);
                brickWalls.add(brickWall);	
                brickWall.buildWall(brick,halfbrick); // build the wall
                // accumulate the house level parameters
                // brick counts
                totalBricksInLayer += brickWall.getNumBricksInLayer();
                totalBricks += brickWall.getNumBricks();
                totalHalfBricks += brickWall.getNumHalfBricks();
                totalChannelBricks += brickWall.getNumChannelBricks();
                totalHalfChannelBricks += brickWall.getNumHalfChannelBricks();
                // height related parameters
                numLayers = brickWall.getTotalLayers();
                numBrickLayers = brickWall.getNumBrickLayers();
                subwallLayers = brickWall.getNumChannelLayers();
                wallHeight = wall.getHeight();
                wallIndex++;
                
                // build the output string for each wall
                walls.append("Wall number ").append(wallIndex).append("\n");
                walls.append( brickWall.getWallDescription()).append("\n");
             
                
			}

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

			// Display the contents of sb on the screen
	//		JOptionPane.showMessageDialog(null, summary.toString());
	//		JOptionPane.showMessageDialog(null, walls.toString());

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

		for (PieceOfFurniture piece : getHome().getFurniture()) {
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

} // end of BOMPlugin class
