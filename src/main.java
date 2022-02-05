import processing.core.PApplet;
import static constants.Constants.*;

public class main extends PApplet {

    public static void main(String... args){
        PApplet.main("main");
    }

    public void settings(){
        size(WIDE, HIGH);
    }

    public void setup(){
        frameRate(FRAME_RATE);
        background(BACKGROUND);
        println("HELLO_WORLD");
    }

    public void draw(){
        background(BACKGROUND);
    }
}
