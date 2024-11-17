/**
 * Definition for the class brick, which is part of the BOMPlugin settings
 * It defines the dimensions of the brick used in this construction
 * 
 * @version 1.0
 * @since 2023-10-05
 */

//File: src/main/java/com/eteks/test/Brick.java
//definition for the class Brick

package com.eteks.test;

public class Brick {
	private float width;
	private float length;
	private float height;

	public Brick(float width, float length, float height) {
		this.width = width;
		this.length = length;
		this.height = height;
	}

	public float getWidth() {
		return width;
	}

	public float getLength() {
		return length;
	}

	public float getHeight() {
		return height;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public void setLength(float length) {
		this.length = length;
	}

	public void setHeight(float height) {
		this.height = height;
	}
	
	
}	//end of class Brick
