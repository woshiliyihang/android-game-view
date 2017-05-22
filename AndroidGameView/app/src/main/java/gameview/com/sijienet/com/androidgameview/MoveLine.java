package gameview.com.sijienet.com.androidgameview;

import android.content.Context;
import android.graphics.Canvas;

import org.jbox2d.dynamics.Body;

import gameview.com.sijienet.com.androidgameviewbase.GameObj;

/**
 * Created by user on 2017/5/22.
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
