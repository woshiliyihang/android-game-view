package gameview.com.sijienet.com.androidgameview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;

import gameview.com.sijienet.com.androidgameviewbase.AndroidGameViewBase;
import gameview.com.sijienet.com.androidgameviewbase.OnUpdateAnimer;

/**
 * Created by user on 2017/5/19.
 */
public class GameView extends AndroidGameViewBase {

    private QiuGameObj qiuGameObj;
    private ImgGameObj imgGameObj;

    public GameView(Context context) {
        super(context);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void init() {
        qiuGameObj = new QiuGameObj(getContext());
        addGameObj(qiuGameObj);
        qiuGameObj.x=40;
        qiuGameObj.y=100;
        qiuGameObj.index=2;
        qiuGameObj.isVisible=true;
        imgGameObj = new ImgGameObj(getContext());
        addGameObj(imgGameObj);
        imgGameObj.index=1;
        imgGameObj.x=40;
        imgGameObj.y=110;
        useSort();
    }

    @Override
    public void start() {
        ValueAnimator valueAnimator = startAnim(new OnUpdateAnimer() {
            @Override
            public void run(int index) {
                int move = 100 + index * 10;
                qiuGameObj.y = move;
            }
        }, 3000, 0, 100);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.start();
    }
}
