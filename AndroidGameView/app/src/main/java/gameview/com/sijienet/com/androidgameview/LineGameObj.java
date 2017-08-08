package gameview.com.sijienet.com.androidgameview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;

import org.jbox2d.dynamics.joints.DistanceJoint;

import gameview.com.sijienet.com.androidgameviewbase.GameObj;

/**
 * 作者：李一航
 * 博客：http://sijienet.com/
 */
public class LineGameObj extends GameObj {

    public DistanceJoint distanceJoint;
    public float rate;

    public LineGameObj(Context context) {
        super(context);
        setColor(Color.BLACK);
        setAntiAlias(true);
        setStrokeWidth(10);
    }

    @Override
    public void drawSelf(Canvas canvas) {
        canvas.drawLine(distanceJoint.getAnchor1().x*rate,distanceJoint.getAnchor1().y*rate,distanceJoint.getAnchor2().x*rate,distanceJoint.getAnchor2().y*rate,this);
    }
}
