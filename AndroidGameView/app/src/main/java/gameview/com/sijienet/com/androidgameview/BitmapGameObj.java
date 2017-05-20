package gameview.com.sijienet.com.androidgameview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;

import gameview.com.sijienet.com.androidgameviewbase.GameObj;

/**
 * Created by user on 2017/5/19.
 */
public class BitmapGameObj extends GameObj {

    public Bitmap img;

    public BitmapGameObj(Context context) {
        super(context);
        setAntiAlias(true);
        setStrokeWidth(1);
        setColor(Color.RED);
        img = BitmapFactory.decodeResource(context.getResources(),R.drawable.touxiang);
        width=img.getWidth();
        height=img.getHeight();
    }

    @Override
    public void drawSelf(Canvas canvas) {
        canvas.save();
        canvas.rotate((float) (body.getAngle()*180f/Math.PI),x+width/2,y+height/2);
        canvas.drawBitmap(img,x,y,this);
        canvas.restore();
    }

}
