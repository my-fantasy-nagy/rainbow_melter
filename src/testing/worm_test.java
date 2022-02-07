package testing;

import objects.Worm;
import processing.core.PApplet;

import static constants.Constants.*;
import static constants.Constants.BACKGROUND;

public class worm_test extends PApplet {

    public static void main(String... args){
        PApplet.main("testing.worm_test");
    }

    Worm worm;

    public void settings(){
        size(WIDE, HIGH);
    }

    public void setup(){
        frameRate(FRAME_RATE);
        background(BACKGROUND);

        worm = new Worm(this, width/2, height/2, 100.0F, 50.0F);
        worm.setAlpha(100);
        worm.setThetaRate(0.1F);
    }

    public void draw(){
        background(BACKGROUND);
        worm.update();
    }
}
