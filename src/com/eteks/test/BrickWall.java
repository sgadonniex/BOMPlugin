package com.eteks.test;
import com.eteks.sweethome3d.model.Wall;
import java.util.ArrayList;
import java.util.List;
import com.eteks.sweethome3d.model.HomePieceOfFurniture;
import java.io.Serializable;
@SuppressWarnings("unchecked")

/**
 * This class contains all the information we want for each wall..
 * 
 * @version 1.0
 * @since 2023-10-05
 */
// File: src/main/java/com/eteks/test/BrickWall.java
// Definition for the class BrickWall
// BrickWall is used to define the dimensions of the wall and calculate the
// materials needed for the wall
//Define the WallType enum



public class BrickWall extends Wall implements Serializable{ 
	private static final long serialVersionUID = 1L; // added to remove the warning
   
	// Properties that must be set for each wall
    private BOMPlugin.WallType wallType;  
    private BOMPlugin.WallOrientation wallOrientation;
    private float tolerance;
    private float subwallHeight;
    
    // Properties calculated for each wall
    private List<HomePieceOfFurniture> doorwindowlist;
    private int numBricks; // total number of bricks
    private int numHalfBricks; // total number of half bricks
    private int numChannelBricks; 	// total number of channel bricks
    private int numHalfChannelBricks; // total number of half channel bricks
    
    private int totalLayers; // total number of layers in the wall
    private int numBrickLayers; // number of brick layers in the wall
    private int numChannelLayers; // number of channel layers in the wall
    private int numBricksInLayer; // bricks in a layer
    private int numHalfBricksInLayer; // half bricks in a layer
    
    private ArrayList<Float>[] layerCoordinates;  // coordinates of layer sections  
    private boolean[] isChannelLayer; // is the layer a channel layer
    private float rebarAmount; // how much rebar is needed for the wall
    private StringBuilder warnings; 
   
    
    // Constructor
    public BrickWall(float xStart, float yStart, float xEnd, float yEnd, float thickness, float height, float tolerance, float subwallHeight) { 
    	super(xStart, yStart, xEnd, yEnd, thickness, height);    	
    	
        this.tolerance = tolerance;
        this.subwallHeight = subwallHeight;
        this.wallType = BOMPlugin.WallType.notSet;
        
        // set wallOrientation based on x, y start and end, if the difference in x is greater than the difference in y, it is a horizontal wall
		if (Math.abs(xEnd - xStart) > Math.abs(yEnd - yStart)) {
			this.wallOrientation = BOMPlugin.WallOrientation.horizontal;
		} else {
			this.wallOrientation = BOMPlugin.WallOrientation.vertical;
		}
    
        this.numBricks = 0;
        this.numHalfBricks = 0;
        this.numChannelBricks = 0;
        this.numHalfChannelBricks =0; 		
        this.rebarAmount = 0;
        this.totalLayers = 1;
        this.numBrickLayers = 0;
        this.numChannelLayers = 0;
        this.numBricksInLayer = 0;
        this.numHalfBricksInLayer = 0;
        this.warnings = new StringBuilder();
        this.doorwindowlist = new ArrayList<>();
 
             
    }


    // Getters

    public int getNumBricks() { return numBricks;}
    public float getRebarAmount() {return rebarAmount;}
    public int getNumHalfBricks() { return numHalfBricks;}
    public int getNumChannelBricks() { return numChannelBricks;}
    public int getNumHalfChannelBricks() { return numHalfChannelBricks;}
    public int getTotalLayers() { return totalLayers;}
    public int getNumBrickLayers() { return numBrickLayers;}
    public int getNumChannelLayers() { return numChannelLayers;}
    public int getNumBricksInLayer() { return numBricksInLayer;}
    public int getNumHalfBricksInLayer() { return numHalfBricks;}
    public String getWarnings() { return warnings.toString();}
    public BOMPlugin.WallType getWallType() {
        return wallType;
    }


    // Setters for all the properties not calculated.
    public void setTolerance(float tolerance) {this.tolerance = tolerance;}
    public void setSubwallHeight(float subwallHeight) {this.subwallHeight = subwallHeight;}
    public void setWallType(BOMPlugin.WallType wallType) {
        this.wallType = wallType;
    }

    
    // Methods
// Utility function to check if a door or window intersects with the wall bounds  
    public boolean doesDoorOrWindowIntersectLayer(float doorWindowStartHeight, float doorWindowEndHeight, int layerIndex, float brickHeight) {
        // Calculate the start and end height of the layer
        float layerStartHeight = layerIndex * brickHeight;
        float layerEndHeight = layerStartHeight + brickHeight;

        // Check if the door or window intersects with the layer
        return (doorWindowStartHeight < layerEndHeight && doorWindowEndHeight > layerStartHeight);
    }
    

    // Method to add wall segments to each layer
    // if the layer does not intersect has no windows or doors, there is only one segment 
    // if there is one door, then there are two segments, and so on. 
    public void addWallSegmentsToLayers(float brickHeight) {
    	
    	// Initialize the layerCoordinates array
    	this.layerCoordinates = new ArrayList[totalLayers];
    	for (int i = 0; i < totalLayers; i++) {
    	    this.layerCoordinates[i] = new ArrayList<>();
    	}
        
    	this.isChannelLayer = new boolean[totalLayers];
      
    	// for each layer in the wall
        for (int layerIndex = 0; layerIndex < totalLayers; layerIndex++) {
        	// check to see if this is a channel layer
        	if((layerIndex+1) % (totalLayers/this.numChannelLayers) == 0){
        		isChannelLayer[layerIndex] = true;
        	}
        	else {
        		isChannelLayer[layerIndex] = false;
        	}
        	// if wall is horizontal, add the y coordinates
 
        	if (this.wallOrientation == BOMPlugin.WallOrientation.vertical) {
        		layerCoordinates[layerIndex].add(this.getYStart());
        	}
        	else {
        		layerCoordinates[layerIndex].add(getXStart());
        	}
            // Iterate through all doors and windows and if they intersect with the layer
            // add their start and end points to the layerCoordinates array
            for (HomePieceOfFurniture furniture : doorwindowlist) {
                float[][] points = furniture.getPoints();
                float startX = points[0][0];
                float startY = points[0][1];
                float endX = points[2][0];
                float endY = points[2][1];
                float doorWindowStartHeight = furniture.getElevation(); // returns the bottom of the door or window
                float doorWindowEndHeight = furniture.getHeight() + doorWindowStartHeight;  

                // Add the door or window coordinates if it intersects with the layer
                if (doesDoorOrWindowIntersectLayer(doorWindowStartHeight, doorWindowEndHeight, layerIndex, brickHeight)) {
                	if (this.wallOrientation == BOMPlugin.WallOrientation.vertical) {
                		layerCoordinates[layerIndex].add(startY);
                		layerCoordinates[layerIndex].add(endY);
                	}
					else {
						layerCoordinates[layerIndex].add(startX );
						layerCoordinates[layerIndex].add(endX );
					}
                }
            }

            // add the end point of the wall
			if (this.wallOrientation == BOMPlugin.WallOrientation.vertical) {
				layerCoordinates[layerIndex].add( this.getYEnd());
			} else {
				layerCoordinates[layerIndex].add(this.getXEnd());
			}

	// now, sort the points from low to high
			layerCoordinates[layerIndex].sort((a, b) -> 	Float.compare(a, b));
        }
    }
    
   
     // Method to calculate the number of bricks in a layer 
    // exterior walls are not allowed to have half bricks at corners
    public void calculateBricksInSegment(float distance, Brick brick, Brick halfBrick, int layerIndex) {

    	// There are eight patterns for the segments
    	// exterior
    	// even layer number:
    	// exterior wall, first or last segment, integer multiple of brick length, no half bricks
    	// exterior wall, first or last segment, not an integer multiple of brick length, not allowed
    	// exterior wall, no windows or doors in the layer - dimension/brick length
    	// odd layer number
    	// exterior wall, first or last segment, integer multiple of brick length, no half bricks
    	// exterior wall, first or last segment, not an integer multiple of brick length, not allowed
    	// exterior wall, no windows or doors in the layer, no half bricks at all.
    	
    	// interior
    	// interior wall or segment, integer multiple of brick length, then two extra half bricks and one fewer brick
    	// interior wall or segment, not an integer multiple of brick length, then no change in bricks or half bricks
    	// interior wall or segment, integer multiple of brick length, no half bricks
    	// interior wall or segment, not an integer multiple of brick length, then no change in bricks or half bricks
    	
    	// note well: the wall length goes from center of wall to center of wall, so the length from outer to outer will
    	// be 1/2 brick width longer. We will account for this in the calculations as follows:
    	// on even layers, horizontal walls will increase by 1/2 brick
    	// on odd layers, vertical walls will increase by 1/2 brick

    	
    	
    
    	
        // first determine which case we are in
		if (this.wallType == BOMPlugin.WallType.exterior) {
			if (layerIndex % 2 == 0) {
				if (distance % brick.getLength() == 0) {
					numBricks += distance / brick.getLength();
				} else {
					numBricks += Math.floor(distance / brick.getLength());
					numHalfBricks += 1;
				}
			} else {
				if (distance % brick.getLength() == 0) {
					numBricks += distance / brick.getLength();
				} else {
					numBricks += Math.floor(distance / brick.getLength());
				}
			}
		} else {
			if (layerIndex % 2 == 0) {
				if (distance % brick.getLength() == 0) {
					numBricks += distance / brick.getLength();
					numBricks -= 1;
					numHalfBricks += 2;
				} else {
					numBricks += Math.floor(distance / brick.getLength());
				}
			} else {
				if (distance % brick.getLength() == 0) {
					numBricks += distance / brick.getLength();
				} else {
					numBricks += Math.floor(distance / brick.getLength());
				}
			}
		}
    	
    	
    	

    }
    
 
    public void calculateBricksInLayers(Brick brick, Brick halfBrick) {
        numBricks = 0;
        numHalfBricks = 0;

        for (int layerIndex = 0; layerIndex < totalLayers; layerIndex++) {
        	List<Float> coordinates = layerCoordinates[layerIndex];
            for (int i = 0; i < coordinates.size() - 1; i++) {
                Float start = coordinates.get(i);
                Float end = coordinates.get(i + 1);
                // most of the time this will be a straight line, but let's do the math anyway
                float distance = (float) Math.abs(start - end);
                
                // increment the number of bricks and half bricks in the segment

            }
        }
    }
    
	public void buildWall(Brick brick, Brick halfBrick) {
		
		float tmpheight = getHeight();
		float tmplength = getLength();
		
	// Calculate the number of layers
	// round height and width to the tolerance values
		tmpheight = Math.round(tmpheight / tolerance) * tolerance;
		tmplength = Math.round(tmplength / tolerance) * tolerance;
	// add a warning if length is not a multiple of the halfbrick length within the tolerance
		if (Math.abs(tmplength % halfBrick.getLength())> tolerance) {
			warnings.append("wall length is not a multiple of the brick length:  ").append(tmplength).append(" tolerance is ").append(tolerance).append("\n");
		}
	
		// round the wall height up to the nearest multiple of the brick height
		totalLayers = (int)Math.ceil(tmpheight / brick.getHeight());
		// round the wall height up to the nearest multiple of the subwall height 
		numChannelLayers = (int)Math.ceil(tmpheight / subwallHeight);
		// total number of non channel layers
		numBrickLayers = totalLayers - numChannelLayers;
		
		// Calculate the segments per layer
		// for each layer, calculate all the sub wall pieces by adding the door and window coordinates
		addWallSegmentsToLayers(brick.getHeight());
		
		// now, calculate the bricks in a layer by adding up the bricks in each segment
		calculateBricksInLayers(brick, halfBrick);



	}

	  // Method to add a door or window  if it intersects with the wall bounds
    public void addDoorOrWindow(HomePieceOfFurniture furniture) {
    	float tmppoints[][] = furniture.getPoints();
    	float furn_x1, furn_x2, furn_y1, furn_y2;
    	furn_x2 = tmppoints[0][0]; // x1, y1 is the top left corner of the door or window
    	furn_y2 = tmppoints[0][1];
    	
    	furn_x1 = tmppoints[2][0]; // x2, y2 is the bottom right corner of the door or window
    	furn_y1 = tmppoints[2][1];
    	
		if (furniture.isDoorOrWindow() == false) {
			return;
		}
		
		if (this.intersectsRectangle(furn_x1, furn_y1, furn_x2, furn_y2)) {
			doorwindowlist.add(furniture);
		}
		
    }



    // Wall description.
    public String getWallDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Length: ").append(this.getLength()).append(" cm ");
        sb.append("Height: ").append(this.getHeight()).append(" cm\n");
        sb.append("Number of Bricks: ").append(numBricks).append(", ");
        sb.append("Number of Half Bricks: ").append(numHalfBricks).append("\n");
        sb.append("Number of Channel Bricks: ").append(numChannelBricks).append(", ");
        sb.append("Number of Half Channel Bricks: ").append(numHalfChannelBricks).append("\n");
        sb.append("Total Layers: ").append(totalLayers).append(", ");
        sb.append("Number of Bricks in each layer: ").append(numBricksInLayer).append(", ");
		if (numHalfBricksInLayer > 0) {
			sb.append("Number of Half Bricks in each layer: ").append(numHalfBricksInLayer).append(", ");
		}
        sb.append("Number of Brick Layers: ").append(numBrickLayers).append(", ");
        sb.append("Number of Channel Layers: ").append(numChannelLayers).append("\n");
        sb.append("number of Doors and Windows ").append(doorwindowlist.size()).append("\n");
		if (warnings.length() > 0) {
			sb.append("Warnings ").append(warnings.toString()).append("\n");
		}
        return sb.toString();
    }


    
    
} // end of class BrickWall


