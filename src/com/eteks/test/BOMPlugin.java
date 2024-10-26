package com.eteks.test;

import com.eteks.sweethome3d.plugin.Plugin;
import com.eteks.sweethome3d.plugin.PluginAction;
import javax.swing.JOptionPane;
import com.eteks.sweethome3d.model.PieceOfFurniture;
import com.eteks.sweethome3d.model.Wall;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class BOMPlugin extends Plugin {

		private Brick brick;
		
		public BOMPlugin() {
			brick = new Brick((float)12.5, 25, 6);
		}
		
		@Override
	    public PluginAction[] getActions() {
	        return new PluginAction [] {new BOMAction()};
	    }

	    private void writeToFile(String message) {
	        try (BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt", true))) {
	            writer.write(message);
	            writer.newLine();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }	

		public class BOMAction extends PluginAction {
	        public BOMAction() {
	           putPropertyValue(Property.NAME, "Compute Bricks");
	           putPropertyValue(Property.MENU, "Materials List2");
	           // Enables the action by default
	           setEnabled(true);
	        }
	        
	        @Override
	        public void execute() {
	            float linearFeetOfWalls = 0;
	            float numWalls = getHome().getWalls().size();
	            float numWindowsAndDoors = 0;
	            float wallHeight = 0;
	            float wallLength = 0;
	            float numBricks = 0;
	            float windowDoorAreaInBricks = 0;
	            float totalBricksInLayer = 0;
	            float numBrickLayers = 0;
	            float totalBricks =0 ;
	            float doororwindowheight =0;
	            float dowheightinbricks =0;
	            float doororwindowwidth =0;
	            float dowwidthinbricks=0;
	            		
	            float wallIndex = 0;

	           
	            // Compute the volume in bricks of all the windows and doors
	            for (PieceOfFurniture piece : getHome().getFurniture()) {
	                if (piece.isDoorOrWindow()) {
	                    numWindowsAndDoors ++;
	                    doororwindowheight = piece.getHeight();
	                    dowheightinbricks = doororwindowheight / brick.getHeight();
	                    doororwindowwidth = piece.getWidth();
	                    dowwidthinbricks = doororwindowwidth / brick.getLength();
	                    windowDoorAreaInBricks += dowheightinbricks * dowwidthinbricks;   
	                }
	            }           
	   
	            // Compute the combined length of all walls and the length in bricks of each wall
	            for (Wall wall : getHome().getWalls()) {
	            	wallIndex++;
	            	wallLength = wall.getLength();
	            	numBricks = wallLength/brick.getLength();
	            	totalBricksInLayer += numBricks;
	               linearFeetOfWalls += wallLength;
	               wallHeight = wall.getHeight();
	               // Display the result in a message box (\u00b3 is for 3 in superscript)
	               String message1 = String.format(
	                       "We need %.2f bricks for wall %f, which is %.0f cm long.",
	                       numBricks, wallIndex, wallLength);
	               JOptionPane.showMessageDialog(null, message1);
	                    
	   
	            }
	            // total bricks in windows and doors needs to be subtracted from total bricks in layer
	            
	            numBrickLayers = wallHeight/ brick.getHeight();
	            totalBricks = totalBricksInLayer * numBrickLayers;
	            
	            // Display the result in a message box (\u00b3 is for 3 in superscript)
	            String message1 = String.format(
	                    "We need %.0f brick per layer. There are %.0f walls with a combined length of %.2f cm. There are %.0f walls and doors.",
	                    totalBricksInLayer, numWalls, linearFeetOfWalls, numWindowsAndDoors);
	            JOptionPane.showMessageDialog(null, message1);
	            String message2 = String.format(
	                    "The wall height is  %.0f and the brick height is %.0f so we need %.0f layers. Total bricks is  %.0f . We still need to subtract the volume in bricks of windows and doors which is %f",
	                    wallHeight, brick.getHeight(), numBrickLayers, totalBricks, windowDoorAreaInBricks);
	            JOptionPane.showMessageDialog(null, message2);
	            
	            String message3 = String.format(
	                    "We need %.0f brick per layer. There are %.0f walls with a combined length of %.2f cm. There are %.0f walls and doors.",
	                    totalBricksInLayer, numWalls, linearFeetOfWalls, numWindowsAndDoors);
	            writeToFile(message3);
	            String message4 = String.format(
	                    "The wall height is  %.0f and the brick height is %.0f so we need %.0f layers. Total bricks is  %.0f . We still need to subtract the volume in bricks of windows and doors which is %f",
	                    wallHeight, brick.getHeight(), numBrickLayers, totalBricks, windowDoorAreaInBricks);
	            writeToFile(message4);
	        }
	    }
	 
	}	
	


