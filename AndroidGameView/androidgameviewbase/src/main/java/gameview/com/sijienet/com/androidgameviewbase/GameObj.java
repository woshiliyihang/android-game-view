package gameview.com.sijienet.com.androidgameviewbase;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.joints.Joint;

/**
 * Created by user on 2017/5/19.
 */
public abstract class GameObj extends Paint {

    public float width;
    public float height;
    public float x;
    public float y;
    public int index;
    public Context context;
    public boolean isVisible=true;
    public Body body;

    public GameObj(Context context) {
        this.context = context;
        setAntiAlias(true);
        setColor(Color.BLACK);
        setStrokeWidth(10);
    }

    public abstract void drawSelf(Canvas canvas);


}
