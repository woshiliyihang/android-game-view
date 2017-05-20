package gameview.com.sijienet.com.androidgameview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;

import gameview.com.sijienet.com.androidgameviewbase.GameObj;

/**
 * Created by user on 2017/5/20.
 */
public class LineBody extends GameObj {

    public LineBody(Context context) {
        super(context);
        setStyle(Style.FILL);
        setAntiAlias(true);
        setStrokeWidth(2);
        setColor(Color.RED);
        width=200;
        height=200;
    }

    @Override
    public void drawSelf(Canvas canvas) {
        canvas.save();
        canvas.rotate((float) (body.getAngle()*180f/Math.PI),x+width/2,y+height/2);
        canvas.drawLine(x, y, x + width, y, this);
        canvas.drawLine(x + width, y, x + width/2, y + height, this);
        canvas.drawLine(x + width/2, y + height, x, y, this);
        canvas.restore();
    }
}
