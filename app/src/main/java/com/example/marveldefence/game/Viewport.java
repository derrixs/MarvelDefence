package com.example.marveldefence.game;

/** Uniform world/screen transform shared by drawing and input. */
public final class Viewport {
    public double scale = 1, left, top;
    public void fit(double screenWidth, double screenHeight, double worldWidth, double worldHeight) {
        scale = Math.max(.0001, Math.min(screenWidth/worldWidth, screenHeight/worldHeight));
        left = (screenWidth-worldWidth*scale)/2; top = (screenHeight-worldHeight*scale)/2;
    }
    /** Uniform fill for the battle background; decorative edges may leave the screen. */
    public void cover(double screenWidth,double screenHeight,double worldWidth,double worldHeight) {
        scale = Math.max(.0001,Math.max(screenWidth/worldWidth,screenHeight/worldHeight));
        left = (screenWidth-worldWidth*scale)/2; top = (screenHeight-worldHeight*scale)/2;
    }
    public double worldX(double screenX) { return (screenX-left)/scale; }
    public double worldY(double screenY) { return (screenY-top)/scale; }
    public double screenX(double worldX) { return left + worldX*scale; }
    public double screenY(double worldY) { return top + worldY*scale; }
}
