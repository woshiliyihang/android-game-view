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
public class QiuGameObj extends GameObj {

    Bitmap img;

    public QiuGameObj(Context context) {
        super(context);
        setAntiAlias(true);
        setStrokeWidth(1);
        setColor(Color.RED);
        img = BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_launcher);
    }

    @Override
    public void drawSelf(Canvas canvas) {
        canvas.drawBitmap(img, x,y,this);
    }
}
