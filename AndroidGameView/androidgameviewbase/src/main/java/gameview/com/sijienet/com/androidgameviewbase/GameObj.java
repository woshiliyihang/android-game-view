package gameview.com.sijienet.com.androidgameviewbase;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by user on 2017/5/19.
 */
public abstract class GameObj extends Paint {

    public int width;
    public int height;
    public int x=0;
    public int y=0;
    public float scale=1.0f;
    public int index=0;
    public Context context;
    public boolean isVisible=true;

    public GameObj(Context context) {
        this.context = context;
    }

    public abstract void drawSelf(Canvas canvas);


}
