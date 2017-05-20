package gameview.com.sijienet.com.androidgameview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;

import gameview.com.sijienet.com.androidgameviewbase.GameObj;

/**
 * Created by user on 2017/5/20.
 */
public class CircleGameObj extends GameObj {

    public CircleGameObj(Context context) {
        super(context);
        setAntiAlias(true);
        setColor(Color.RED);
        setStrokeWidth(8);
        width=100;
        height=100;
        radius=50;
    }

    @Override
    public void drawSelf(Canvas canvas) {
        canvas.save();
        canvas.rotate((float) (body.getAngle()*180f/Math.PI),x+width/2,y+height/2);
        canvas.drawCircle(x+radius,y+radius,radius,this);
        canvas.restore();
    }
}
