package objects;

import processing.core.PApplet;
import processing.core.PVector;

import static java.lang.Math.*;

public class Worm {
    PApplet pa;

    float posX;  //beginning pos of skeleton
    float posY;  //beginning pos of skeleton
    float len;   //length of the worm
    float girth; //width of the worm
    float rad;    // half of the length

    //TODO FIX COLOR
//    color col = #FFFFFF;
    int col = 255;

    PVector[] skeletonVectors = new PVector[6];
    PVector[] mirrorSkeletonVectors = new PVector[6];
    PVector[] bodyVectors = new PVector[8];
    PVector[] mirrorBodyVectors = new PVector[8];

    float pointWeight = 5;
    float theta = 0;
    float thetaRate = 0;
    float rotVal = 0;
    float alpha = 255;
    float  thetaLimit = (float) (PI/2);
    boolean thetaIncreasing;


    public Worm(PApplet pa, float posX_, float posY_, float len_, float girth_) {
        this.pa = pa;
        this.posX = posX_;
        this.posY = posY_;
        this.len  = len_;
        this.girth= girth_;
        this.rad = len/2;

        //initialize mirror vectors
        initializeVectors(skeletonVectors, bodyVectors);
        initializeVectors(mirrorSkeletonVectors, mirrorBodyVectors);

        // flip mirror vectors
        setMirrors(skeletonVectors, bodyVectors, mirrorSkeletonVectors, mirrorBodyVectors);

        //randomize direction
        thetaIncreasing = true;
        if(pa.random(0,1) > 0.5){
            thetaIncreasing = false;
        }
    }

    private void setMirrors(PVector[] sv, PVector[] bv, PVector[] msv, PVector[] mbv) {

        for (int i = 0; i < sv.length; i++) {
            msv[i] = sv[i].copy();
            msv[i].add(-sv[i].x * 2, 0);
        }
        for (int i = 0; i < bv.length; i++) {
            mbv[i] = bv[i].copy();
            mbv[i].add(-bv[i].x * 2, 0);
        }
    }

    public void wiggle(PVector[] sv, PVector[] bv) {


        sv[2].x = (float) (sv[1].x + rad * abs(cos(theta)));
        sv[2].y = (float) (sv[1].y - rad * abs(sin(theta)));
        sv[3] = sv[2].copy();
        //TODO: FIX 0.4 ???
        sv[4].x = pa.bezierPoint(sv[0].x, sv[1].x, sv[2].x, sv[3].x, 0.4F); //mid x
        sv[4].y = pa.bezierPoint(sv[0].y, sv[1].y, sv[2].y, sv[3].y, 0.4F); //mid y
        sv[5] = sv[3].copy();

        sv[3] = sv[2].copy();

        bv[2] = perpVectorCClockwise(sv[5], girth);
        bv[5] = perpVectorClockwise(sv[5], girth);

        bv[3] = sv[5].copy();
        bv[4] = sv[5].copy();

        if (theta > thetaLimit) {
            thetaIncreasing = false;
        }
        if (theta < -thetaLimit) {
            thetaIncreasing = true;
        }

        if (thetaIncreasing) {
            theta += thetaRate;
        } else {
            theta -= thetaRate;
        }
    }


    public void update() {
        pa.push();
        pa.translate(posX, posY);
        pa.rotate(rotVal);
        wiggle(skeletonVectors, bodyVectors);
        setMirrors(skeletonVectors, bodyVectors, mirrorSkeletonVectors, mirrorBodyVectors);

        //FOR DEUBGGING
//        showSkeleton(skeletonVectors, bodyVectors);
//        showSkeleton(mirrorSkeletonVectors, mirrorBodyVectors);

        showBody    (skeletonVectors, bodyVectors, mirrorSkeletonVectors, mirrorBodyVectors);
        pa.pop();
    }

    void rotateBody() {
    }

    void showBody( PVector[] sv, PVector[] bv, PVector[] msv, PVector[] mbv) {

        styleLine1();
        pa.fill(col, alpha);
        pa.beginShape();
        pa.vertex(bv[0].x, bv[0].y);
        drawBezierVertex(bv[1], bv[2], sv[5]);
        drawBezierVertex(bv[5], bv[6], bv[7]);
        drawBezierVertex(mbv[6], mbv[5], msv[5]);
        drawBezierVertex(mbv[2], mbv[1], mbv[0]);
        //drawBezierVertex(mbv[], mbv[4], msv[5]);
        //drawBezierVertex(mbv[7], mbv[6], msv[5]);
        //drawBezierVertex(msv[5], mbv[2], mbv[1]);


        pa.endShape();
    }

    void showSkeleton(PVector[] sv, PVector[] bv) {
        styleLine1();
        drawBezier(sv[0], sv[1], sv[2], sv[3]);
        drawAnchorPoints(sv[0], sv[3]);
        drawControlPoints( sv[1], sv[2]);

        drawAnchorPoints(bv[0], bv[3]);
        drawControlPoints(bv[1], bv[2]);
        drawAnchorPoints(bv[7], bv[4]);

        styleLine1();
        drawLine(sv[4], bv[1]);
        drawLine(sv[4], bv[6]);
        drawLine(sv[5], bv[5]);
        drawLine(sv[5], bv[2]);


        //point(tip.x, tip.y);
    }




    private void drawBezierVertex(PVector p, PVector q, PVector r) {
        pa.bezierVertex(p.x, p.y, q.x, q.y, r.x, r.y);
    }

    private void drawQuadraticBezierVertex(PVector p, PVector q) {
        pa.quadraticVertex(p.x, p.y, q.x, q.y);
    }


    private PVector perpVectorClockwise(PVector p, float girth) {
        PVector temp = p.copy().mult(0);
        temp.x = (float) (girth/2 * abs(sin(theta)));
        temp.y = (float) (girth/2 * abs(cos(theta)));
        return temp.add(p);
    }


    private PVector perpVectorCClockwise(PVector p, float girth) {
        PVector temp = p.copy().mult(0);
        temp.x = (float) (-girth/2 * abs(sin(theta)));
        temp.y = (float) (-girth/2 * abs(cos(theta)));
        return temp.add(p);
    }

    private void initializeVectors(PVector[] sv, PVector[] bv) {

        sv[0] = new PVector(0, 0);             //a
        sv[1] = new PVector(len/2, 0);         //b
        sv[2] = new PVector(len, 0);           //c
        sv[3] = sv[2].copy();                  //d
        sv[4] = sv[1].copy();                  //mid
        sv[5] = sv[3].copy();                  //tip
        bv[0] = sv[0].copy().add(0, -girth/2); //al
        bv[1] = sv[1].copy().add(0, -girth/2); //bl
        bv[2] = sv[2].copy().add(0, -girth/2); //cl
        bv[3]=  bv[2].copy();                  //dl
        bv[4]=  sv[2].copy().add(0, girth/2);  //dr
        bv[5]=  bv[4].copy();                  //cr
        bv[6]=  sv[1].copy().add(0, girth/2);  //br
        bv[7]=  sv[0].copy().add(0, girth/2);  //ar
    }

    void drawBezier(PVector a, PVector b, PVector c, PVector d) {

        styleLine1();
        pa.bezier(a.x, a.y, b.x, b.y, c.x, c.y, d.x, d.y);

        styleLine2();
        pa.line(a.x, a.y, b.x, b.y);
        pa.line(c.x, c.y, d.x, d.y);
    }

    void drawLine(PVector a, PVector b) {
        pa.line(a.x, a.y, b.x, b.y);
        styleLine1();
    }

    public void setCol(int c) {
        col = c;
    }

    public void setThetaRate(float t) {
        thetaRate = t;
    }

    public void setAlpha(float a) {
        alpha = a;
    }

    public void setRot(float r) {
        rotVal = r;
    }

    public void setThetaOffset(float to) {
        theta = to;
    }

    public void setLength(float len_) {
        len = len_;
        rad = len/2;
    }

    public void setGirth(float girth_) {
        girth = girth_;
    }

    public void setThetaLimit(float t) {
        thetaLimit = t;
    }

    void drawControlPoints( PVector b, PVector c) {
        styleAnchorB();
        pa.point(b.x, b.y);
        styleAnchorC();
        pa.point(c.x, c.y);
    }

    void drawAnchorPoints(PVector a, PVector d) {
        styleControlA();
        pa.point(a.x, a.y);
        styleControlD();
        pa.point(d.x, d.y);
    }

    void styleAnchorB() {
        pa.strokeWeight(pointWeight);
        pa.stroke(0, 255, 0);
    }

    void styleAnchorC() {
        pa.strokeWeight(pointWeight);
        pa.stroke(0, 0, 255);
    }
    void styleControlA() {
        pa.strokeWeight(pointWeight);
        pa.stroke(255, 0, 0);
    }

    void styleControlD() {
        pa.strokeWeight(pointWeight);
        pa.stroke(255, 255, 0);
    }


    void styleLine1() {
        pa.noFill();
        pa.strokeWeight(3);
        int newCol = col - 150;
        pa.stroke(newCol, alpha);
    }

    void styleLine2() {
        pa.noFill();
        pa.strokeWeight(2);
        pa.stroke(100, 50);
    }

    //print skeleton vectors
    void printSV() {
        for (PVector v : skeletonVectors) {
            System.out.println(v);
        }
    }

    void printMSV() {
        for (PVector v : mirrorSkeletonVectors) {
            System.out.println(v);
        }
    }

    //print body vectors
    void printBV() {
    }
}
