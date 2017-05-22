package gameview.com.sijienet.com.androidgameview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;

import org.jbox2d.dynamics.Body;

import gameview.com.sijienet.com.androidgameviewbase.GameObj;

/**
 * Created by user on 2017/5/22.
 */
public class HuaLunDrawLine extends GameObj {

    public Body body;
    public Body body2;
    public float RATE;
    public float screentWidth;

    public HuaLunDrawLine(Context context) {
        super(context);
        setColor(Color.BLUE);
        setStrokeWidth(2);
    }

    @Override
    public void drawSelf(Canvas canvas) {
        canvas.drawLine(body.getWorldCenter().x*RATE,body.getWorldCenter().y*RATE, screentWidth/2,1400,this);
        canvas.drawLine(screentWidth/2,1400,screentWidth/2+300,1400,this);
        canvas.drawLine(screentWidth/2+300,1400,body2.getWorldCenter().x*RATE,body2.getWorldCenter().y*RATE,this);
    }
}
