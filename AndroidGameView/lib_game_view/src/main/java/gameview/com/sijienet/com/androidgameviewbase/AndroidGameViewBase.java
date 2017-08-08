package gameview.com.sijienet.com.androidgameviewbase;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import org.jbox2d.collision.AABB;
import org.jbox2d.collision.CircleDef;
import org.jbox2d.collision.PolygonDef;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * 作者：李一航
 * 博客：http://sijienet.com/
 */
public abstract class AndroidGameViewBase extends View {

    public static final String TAG="android-game-view";

    private ArrayList<GameObj> gameObjs;
    public Handler handler;
    private ArrayList<BodyBind> bodyBinds;
    private boolean isFirstLoading;

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
        baseConfig();
        init();
    }

    public AndroidGameViewBase(Context context, AttributeSet attrs) {
        super(context, attrs);
        baseConfig();
        init();
    }

    public AndroidGameViewBase(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        baseConfig();
        init();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (isFirstLoading && changed)
        {
            isFirstLoading=false;
            start();
        }
    }

    public void addGameObj(GameObj gameObj){
        if (! gameObjs.contains(gameObj))
        {
            synchronized (gameObjs)
            {
                gameObjs.add(gameObj);
            }
        }
    }

    public void removeGameObj(GameObj gameObj){
        try {
            synchronized (gameObjs)
            {
                gameObjs.remove(gameObj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addGameBodyBind(GameObj gameObj, Body body){
        gameObj.body=body;
        body.m_userData=gameObj;
        BodyBind bodyBind = new BodyBind(body,gameObj);
        if (! bodyBinds.contains(bodyBind))
        {
            synchronized (bodyBinds)
            {
                bodyBinds.add(bodyBind);
            }
        }
    }

    public void removeBody(Body body){
        synchronized (bodyBinds)
        {
            int index=-1;
            for (int i = 0; i < bodyBinds.size(); i++) {
                if (bodyBinds.get(i).body==body)
                {
                    index=i;
                }
            }
            if (index!=-1)
            {
                BodyBind bodyBind = bodyBinds.get(index);
                getWorld().destroyBody(bodyBind.body);
                bodyBinds.remove(index);
            }
        }
    }


    public void bindGameBody(float timeStep, int iterations){
        getWorld().step(timeStep,iterations);//模拟运算
        synchronized (bodyBinds)
        {
            for (BodyBind bodyBind : bodyBinds) {
                bindGameObjBody(bodyBind.gameObj, bodyBind.body);
            }
        }
    }

    public ArrayList<GameObj> getGameObjs() {
        return gameObjs;
    }

    public void useSort(){
        Collections.sort(gameObjs, new Comparator<GameObj>() {
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

    private void baseConfig() {

        isFirstLoading=true;

        handler=new Handler();
        bodyBinds=new ArrayList<>();
        gameObjs=new ArrayList<>();

        WindowManager systemService = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics mx=new DisplayMetrics();
        systemService.getDefaultDisplay().getMetrics(mx);
        screenWitdh=mx.widthPixels;
        screenHeight=mx.heightPixels;
    }

    public World initBox2dWorld(Vec2 gravity) {
        //设置物理世界
        AABB aabb = new AABB();
        aabb.lowerBound.set(-100f, -100f);
        aabb.upperBound.set(100f, 100f);
        return new World(aabb, gravity, true);
    }

    public void startBox2dWord(final float timeStep, final int iterations){
        handler.post(new Runnable() {
            @Override
            public void run() {
                invalidate();
                bindGameBody(timeStep,iterations);
                handler.postDelayed(this, (long) (timeStep*1000));
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        synchronized (gameObjs)
        {
            for (GameObj gameObj : gameObjs) {
                if (gameObj.isVisible)
                    gameObj.drawSelf(canvas);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        synchronized (gameObjs)
        {
            for (GameObj gameObj : gameObjs) {
                gameObj.onTouchEvent(event);
            }
        }
        return true;
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

    public static ValueAnimator startAnim2(final OnUpdateAnimer updateAnimer, int time, int... val){
        ValueAnimator valueAnimator = ValueAnimator.ofInt(val);
        valueAnimator.setDuration(time);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int index = (int) valueAnimator.getAnimatedValue();
                updateAnimer.run(index);
            }
        });
        return valueAnimator;
    }

    public void bindGameObjBody(GameObj gameObj, Body body){
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
        pd.restitution = 0.4f; // 设置多边形的恢复力
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

    public Body createPolygon(float x, float y, float width, float height, float density, float friction, float restitution) {
        // ---创建多边形皮肤
        PolygonDef pd = new PolygonDef(); // 实例一个多边形的皮肤
        pd.density=density;
        pd.friction = friction; // 设置多边形的摩擦力
        pd.restitution = restitution; // 设置多边形的恢复力
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
        pd.restitution = 0.4f; // 设置多边形的恢复力
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

    public Body createCircleDef(float x, float y, float r, float density, float friction, float restitution) {
        // ---创建多边形皮肤
        CircleDef pd = new CircleDef(); // 实例一个多边形的皮肤
        pd.density = density;
        pd.friction = friction; // 设置多边形的摩擦力
        pd.restitution = restitution; // 设置多边形的恢复力
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
        pd.restitution = 0.4f; // 设置多边形的恢复力
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


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public int dp2px( float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }


}
