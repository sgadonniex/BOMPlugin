
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
}
