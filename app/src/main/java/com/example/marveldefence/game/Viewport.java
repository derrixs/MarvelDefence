package com.example.marveldefence.game;

/** Transformação uniforme entre mapa e tela, compartilhada pelo desenho e pelos toques. */
public final class Viewport {
    public double scale = 1, left, top;
    public void fit(double screenWidth, double screenHeight, double worldWidth, double worldHeight) {
        scale = Math.max(.0001, Math.min(screenWidth / worldWidth, screenHeight / worldHeight));
        left = (screenWidth - worldWidth * scale) / 2;
        top = (screenHeight - worldHeight * scale) / 2;
    }
    /** Preenche o fundo do combate sem deformar; as bordas decorativas podem ficar fora da tela. */
    public void cover(double screenWidth, double screenHeight, double worldWidth, double worldHeight) {
        scale = Math.max(.0001, Math.max(screenWidth / worldWidth, screenHeight / worldHeight));
        left = (screenWidth - worldWidth * scale) / 2;
        top = (screenHeight - worldHeight * scale) / 2;
    }
    public double worldX(double screenX) {
        return (screenX - left) / scale;
    }
    public double worldY(double screenY) {
        return (screenY - top) / scale;
    }
    public double screenX(double worldX) {
        return left + worldX * scale;
    }
    public double screenY(double worldY) {
        return top + worldY * scale;
    }
}
