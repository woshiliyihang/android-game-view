package gameview.com.sijienet.com.androidgameview;

import android.content.Context;
import android.graphics.Canvas;

import org.jbox2d.dynamics.Body;

import gameview.com.sijienet.com.androidgameviewbase.GameObj;

/**
 * 作者：李一航
 * 博客：http://sijienet.com/
 */
public class MoveLine extends GameObj {

    public Body moveObjDef;
    public Body moveObjDef2;

    public float rate;

    public MoveLine(Context context) {
        super(context);
        setStrokeWidth(20);
    }

    @Override
    public void drawSelf(Canvas canvas) {
        canvas.drawLine(moveObjDef.getWorldCenter().x*rate, moveObjDef.getWorldCenter().y*rate,moveObjDef2.getWorldCenter().x*rate, moveObjDef2.getWorldCenter().y*rate,this);
    }
}
