package gameview.com.sijienet.com.androidgameviewbase;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import org.jbox2d.collision.CircleDef;
import org.jbox2d.collision.PolygonDef;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by user on 2017/5/19.
 */
public abstract class AndroidGameViewBase extends View {

    private ArrayList<GameObj> gameObjs=new ArrayList<>();
    public Handler handler=new Handler();

    private ArrayList<BodyBind> bodyBinds=new ArrayList<>();

    public static class BodyBind{
        public Body body;
        public GameObj gameObj;

        public BodyBind(Body body, GameObj gameObj) {
            this.body = body;
            this.gameObj = gameObj;
        }
    }

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

    public void addGameBodyBind(GameObj gameObj,Body body){
        gameObj.body=body;
        body.m_userData=gameObj;
        BodyBind bodyBind = new BodyBind(body,gameObj);
        if (! bodyBinds.contains(bodyBind))
        {
            bodyBinds.add(bodyBind);
        }
    }

    public void bindGameBody(float timeStep, int iterations){
        getWorld().step(timeStep,iterations);//模拟运算
        for (BodyBind bodyBind : bodyBinds) {
            bindGameObjBody(bodyBind.gameObj, bodyBind.body);
        }
    }

    public ArrayList<GameObj> getGameObjs() {
        return gameObjs;
    }

    @TargetApi(Build.VERSION_CODES.N)
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
    public abstract float getRate();
    public abstract World getWorld();

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

    public void bindGameObjBody(GameObj gameObj,Body body){
        Vec2 position2 = body.getPosition();
        gameObj.x = position2.x*getRate() - gameObj.width/2;
        gameObj.y = position2.y*getRate() - gameObj.height/2;
    }

    public Body createPolygon(float x, float y, float width, float height, boolean isStatic) {
        // ---创建多边形皮肤
        PolygonDef pd = new PolygonDef(); // 实例一个多边形的皮肤
        if (isStatic) {
            pd.density = 0; // 设置多边形为静态
        } else {
            pd.density = 1; // 设置多边形的质量
        }
        pd.friction = 0.8f; // 设置多边形的摩擦力
        pd.restitution = 0.3f; // 设置多边形的恢复力
        // 设置多边形快捷成盒子(矩形)
        // 两个参数为多边形宽高的一半
        pd.setAsBox(width / 2 / getRate(), height / 2 / getRate());
        // ---创建刚体
        BodyDef bd = new BodyDef(); // 实例一个刚体对象
        bd.position.set((x + width / 2) / getRate(), (y + height / 2) / getRate());// 设置刚体的坐标
        // ---创建Body（物体）
        Body body = getWorld().createBody(bd); // 物理世界创建物体
        body.createShape(pd); // 为Body添加皮肤
        body.setMassFromShapes(); // 将整个物体计算打包
        return body;
    }

    public Body createCircleDef(float x, float y, float r, boolean isStatic) {
        // ---创建多边形皮肤
        CircleDef pd = new CircleDef(); // 实例一个多边形的皮肤
        if (isStatic) {
            pd.density = 0; // 设置多边形为静态
        } else {
            pd.density = 1; // 设置多边形的质量
        }
        pd.friction = 0.8f; // 设置多边形的摩擦力
        pd.restitution = 0.3f; // 设置多边形的恢复力
        // 设置多边形快捷成盒子(矩形)
        // 两个参数为多边形宽高的一半
        pd.radius=r/getRate();
        // ---创建刚体
        BodyDef bd = new BodyDef(); // 实例一个刚体对象
        bd.position.set((x + r) / getRate(), (y + r) / getRate());// 设置刚体的坐标
        // ---创建Body（物体）
        Body body = getWorld().createBody(bd); // 物理世界创建物体
        body.createShape(pd); // 为Body添加皮肤
        body.setMassFromShapes(); // 将整个物体计算打包
        return body;
    }

    public Body createPolygonForLine(float x, float y, float width, float height, boolean isStatic) {
        // ---创建多边形皮肤
        PolygonDef pd = new PolygonDef(); // 实例一个多边形的皮肤
        if (isStatic) {
            pd.density = 0; // 设置多边形为静态
        } else {
            pd.density = 1; // 设置多边形的质量
        }
        pd.friction = 0.8f; // 设置多边形的摩擦力
        pd.restitution = 0.3f; // 设置多边形的恢复力
        // 设置多边形快捷成盒子(矩形)
        // 两个参数为多边形宽高的一半
        float v = width / 2 / getRate();
        pd.addVertex(new Vec2(-1*v,-1*v));
        pd.addVertex(new Vec2(1*v,-1*v));
        pd.addVertex(new Vec2(0,1*v));
        // ---创建刚体
        BodyDef bd = new BodyDef(); // 实例一个刚体对象
        bd.position.set((x + width / 2) / getRate(), (y + height / 2) / getRate());// 设置刚体的坐标
        // ---创建Body（物体）
        Body body = getWorld().createBody(bd); // 物理世界创建物体
        body.createShape(pd); // 为Body添加皮肤
        body.setMassFromShapes(); // 将整个物体计算打包
        return body;
    }


}
