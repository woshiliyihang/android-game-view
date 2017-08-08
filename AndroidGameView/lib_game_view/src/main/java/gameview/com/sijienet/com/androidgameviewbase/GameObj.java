package gameview.com.sijienet.com.androidgameviewbase;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;

import org.jbox2d.dynamics.Body;

/**
 * 作者：李一航
 * 博客：http://sijienet.com/
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
        setStrokeWidth(4);
    }

    public void onTouchEvent(MotionEvent event) {

    }

    public boolean isClick(MotionEvent event){
        if (event.getAction()==MotionEvent.ACTION_DOWN && isTouch(event))
        {
            return true;
        }
        return false;
    }

    public boolean isTouch(MotionEvent event) {
        if (!isVisible)
        {
            return false;
        }
        if (event.getX()>=x && event.getX()<= (x+width) && event.getY()>=y && event.getY()<=(y+height) )
        {
            return true;
        }
        return false;
    }

    public abstract void drawSelf(Canvas canvas);

    // 工具函数 图片尺寸压缩函数
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth) {
        // 源图片的宽度
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (width > reqWidth) {
            // 计算出实际宽度和目标宽度的比率
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = widthRatio;
        }
        return inSampleSize;
    }

    // 压缩图片质量
    public static Bitmap decodeSampledBitmapFromResource(String pathName, int reqWidth) {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        // 调用上面定义的方法计算inSampleSize值
        options.inSampleSize = calculateInSampleSize(options, reqWidth);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(pathName, options);
    }

    // 压缩图片质量
    public static Bitmap decodeSampledBitmapFromResource(Context context,int pathName, int reqWidth) {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(),pathName, options);
        // 调用上面定义的方法计算inSampleSize值
        options.inSampleSize = calculateInSampleSize(options, reqWidth);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(context.getResources(),pathName, options);
    }


}
