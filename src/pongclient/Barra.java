package pongclient;

import java.awt.Graphics;
import javaPlay.GameObject;
import javaPlay.Sprite;

class Barra extends GameObject {

    Sprite sprite;

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    @Override
    public void step(long time) {

    }

    @Override
    public void draw(Graphics graphic) {
        sprite.draw(graphic, x, y);
    }
}
