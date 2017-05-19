package gameview.com.sijienet.com.androidgameview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;

import gameview.com.sijienet.com.androidgameviewbase.GameObj;

/**
 * Created by user on 2017/5/19.
 */
public class ImgGameObj extends GameObj {

    public ImgGameObj(Context context) {
        super(context);
        setAntiAlias(true);
        setColor(Color.BLACK);
        setTextSize(50);
    }

    @Override
    public void drawSelf(Canvas canvas) {
        canvas.drawText("我绘制的文字",x,y,this);
    }
}
