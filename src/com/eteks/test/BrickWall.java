package com.eteks.test;

/**
 * This class contains all the information we want for each wall..
 * 
 * @version 1.0
 * @since 2023-10-05
 */


public class BrickWall { // File: src/main/java/com/eteks/test/BrickWall.java
	// Definition for the class BrickWall
	// BrickWall is used to define the dimensions of the wall and calculate the
	// materials needed for the wall

	// Properties that must be set for each wall
	private float length;
    private float height;
    float tolerance;
    float subwallHeight;
    float rebarLength;
    
    // Properties calculated for each wall
    private int numBricks; // total number of bricks
    private int numHalfBricks; // total number of half bricks
    private int numChannelBricks; 	// total number of channel bricks
    private int numHalfChannelBricks; // total number of half channel bricks
    
    private int totalLayers; // total number of layers in the wall
    private int numBrickLayers; // number of brick layers in the wall
    private int numChannelLayers; // number of channel layers in the wall
    private int numBricksInLayer; // bricks in a layer
    private int numHalfBricksInLayer; // half bricks in a layer
    
    private float rebarAmount; // how much rebar is needed for the wall
    private StringBuilder warnings; 

   
    // Constructor
    public BrickWall() { // initialize all the properties to 0
        this.length = 0;
        this.height = 0;
        this.tolerance = 0.2f;
        this.subwallHeight = 0;
        this.rebarLength = 0;
        
        this.numBricks = 0;
        this.numHalfBricks = 0;
        this.numChannelBricks = 0;
        this.numHalfChannelBricks =0; 		
        this.rebarAmount = 0;
        this.totalLayers = 0;
        this.numBrickLayers =0;
        this.numChannelLayers=0;
        this.numBricksInLayer = 0;
        this.numHalfBricksInLayer = 0;
        this.warnings = new StringBuilder();

    }
    
    // Constructor
    public BrickWall(float length, float height, float tolerance, float subwallHeight,float rebarLength) { // initialize all the properties to 0
        this.length = length;
        this.height = height;
        this.tolerance = tolerance;
        this.subwallHeight = subwallHeight;
        this.rebarLength = rebarLength;
        
        this.numBricks = 0;
        this.numHalfBricks = 0;
        this.numChannelBricks = 0;
        this.numHalfChannelBricks =0; 		
        this.rebarAmount = 0;
        this.totalLayers = 0;
        this.numBrickLayers = 0;
        this.numChannelLayers = 0;
        this.numBricksInLayer = 0;
        this.numHalfBricksInLayer = 0;
        this.warnings = new StringBuilder();
             
    }

    // Getters
    public float getLength() { return length; }
    public float getHeight() { return height; }
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

    // Setters for all the properties not calculated.
    public void setLength(float length) {this.length = length;}
    public void setHeight(float height) {this.height = height;}
    public void setTolerance(float tolerance) {this.tolerance = tolerance;}
    public void setSubwallHeight(float subwallHeight) {this.subwallHeight = subwallHeight;}
    public void setRebarLength(float rebarLength) {this.rebarLength = rebarLength;}

    
    // Methods
	public void buildWall(Brick brick, Brick halfBrick) {
		
	// round height and width to the tolerance values
		
	if (tolerance > 0.001f) {
		height = Math.round(height / tolerance) * tolerance;
		length = Math.round(length / tolerance) * tolerance;
	} else {
		warnings.append("tolerance is too small ").append("\n");
	}


    // 1 calculate number of layers, bricklayers and channel layers		
		// round the wall height up to the nearest multiple of the brick height
		totalLayers = (int)Math.ceil(height / brick.getHeight());
		// round the wall height up to the nearest multiple of the subwall height 
		numChannelLayers = (int)Math.ceil(height / subwallHeight);
		// total number of non channel layers
		numBrickLayers = totalLayers - numChannelLayers;
		
	// 2 calculate the number of bricks and half bricks in a layer	
		// get the remainder of the wall length divided by the halfbrick length
		// first, round the wall length to the tolerance
		int tmpNumHalfBricks = (int)Math.round(length / halfBrick.getLength());
		numBricksInLayer = (int)tmpNumHalfBricks / 2;
		// check to see if we need a half brick in the layer.
		if (tmpNumHalfBricks % 2 !=0) { // odd number of half bricks
			numHalfBricksInLayer = 1;
		}
		else {
			numHalfBricksInLayer = 0;
		}

		
		float lengthmodulus = length % halfBrick.getLength();
		// add a warning if its not a multiple of the halfbrick length within the tolerance
		if (Math.abs(lengthmodulus)> tolerance) {
			warnings.append("wall length is not a multiple of the brick length:  ").append(lengthmodulus).append(" tolerance is ").append(tolerance).append("\n");
		}
		

		

// 3 calculate the total bricks, halfbricks, channelbricks, and halfchannelbricks.
		numBricks = numBricksInLayer * numBrickLayers;
		numHalfBricks = numHalfBricksInLayer * numBrickLayers;
		numChannelBricks = numBricksInLayer * numChannelLayers;
		numHalfChannelBricks = numHalfBricksInLayer * numChannelLayers;
		
		

	}


    // Wall description.

    public String getWallDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Length: ").append(length).append(" cm ");
        sb.append("Height: ").append(height).append(" cm\n");
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
		if (warnings.length() > 0) {
			sb.append("Warnings ").append(warnings.toString()).append("\n");
		}
        return sb.toString();
    }


} // end of class BrickWall


