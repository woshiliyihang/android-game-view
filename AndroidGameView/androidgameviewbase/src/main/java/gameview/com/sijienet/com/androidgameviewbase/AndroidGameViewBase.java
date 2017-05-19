package gameview.com.sijienet.com.androidgameviewbase;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by user on 2017/5/19.
 */
public abstract class AndroidGameViewBase extends View {

    private ArrayList<GameObj> gameObjs=new ArrayList<>();
    public Handler handler=new Handler();

    public int screenWitdh;
    public int screenHeight;

    public AndroidGameViewBase(Context context) {
        super(context);
        getConfig();
        init();
        post(new Runnable() {
            @Override
            public void run() {
                start();
            }
        });
    }

    public AndroidGameViewBase(Context context, AttributeSet attrs) {
        super(context, attrs);
        getConfig();
        init();
        post(new Runnable() {
            @Override
            public void run() {
                start();
            }
        });
    }

    public AndroidGameViewBase(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getConfig();
        init();
        post(new Runnable() {
            @Override
            public void run() {
                start();
            }
        });
    }

    public void addGameObj(GameObj gameObj){
        if (! gameObjs.contains(gameObj))
        {
            gameObjs.add(gameObj);
        }
    }

    public void useSort(){
        gameObjs.sort(new Comparator<GameObj>() {
            @Override
            public int compare(GameObj gameObj, GameObj t1) {
                int index = gameObj.index;
                int index1 = t1.index;
                if (index<index1)
                {
                    return -1;
                }
                if (index>index1)
                {
                    return 1;
                }
                return 0;
            }
        });
    }

    private void getConfig() {
        WindowManager systemService = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics mx=new DisplayMetrics();
        systemService.getDefaultDisplay().getMetrics(mx);
        screenWitdh=mx.widthPixels;
        screenHeight=mx.heightPixels;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (GameObj gameObj : gameObjs) {
            if (gameObj.isVisible)
                gameObj.drawSelf(canvas);
        }
    }

    public abstract void init();
    public abstract void start();

    public ValueAnimator startAnim(final OnUpdateAnimer updateAnimer, int time, int... val){
        return startAnim(this, updateAnimer, time, val);
    }

    public static ValueAnimator startAnim(final View view, final OnUpdateAnimer updateAnimer, int time, int... val){
        ValueAnimator valueAnimator = ValueAnimator.ofInt(val);
        valueAnimator.setDuration(time);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int index = (int) valueAnimator.getAnimatedValue();
                updateAnimer.run(index);
                view.invalidate();
            }
        });
        return valueAnimator;
    }

}
