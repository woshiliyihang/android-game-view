package gameview.com.sijienet.com.androidgameview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;

import org.jbox2d.dynamics.Body;

import gameview.com.sijienet.com.androidgameviewbase.GameObj;

/**
 * Created by user on 2017/5/19.
 */
public class RectGameObj extends GameObj {

    public RectGameObj(Context context) {
        super(context);
        setAntiAlias(true);
        setColor(Color.RED);
        setTextSize(50);
        setStrokeWidth(20);
        setStyle(Style.FILL);
        width = 80;
        height = 50;
    }

    @Override
    public void drawSelf(Canvas canvas) {
        canvas.save();
        canvas.rotate((float) (body.getAngle()*180f/Math.PI),x+width/2,y+height/2);
        canvas.drawRect(x,y,x+width,y+height,this);
        canvas.restore();
    }

}
