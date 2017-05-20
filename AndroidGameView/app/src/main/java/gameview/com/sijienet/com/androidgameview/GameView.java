package gameview.com.sijienet.com.androidgameview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import org.jbox2d.collision.AABB;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.ContactListener;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.ContactPoint;
import org.jbox2d.dynamics.contacts.ContactResult;

import gameview.com.sijienet.com.androidgameviewbase.AndroidGameViewBase;

/**
 * Created by user on 2017/5/19.
 */
public class GameView extends AndroidGameViewBase implements View.OnClickListener, ContactListener {

    public static final String tag="GameView";

    private BitmapGameObj qiuGameObj;
    private RectGameObj imgGameObj;
    private World world;
    private float timeStep=1f / 60f;//模式真实世界频率
    private int iterations=10;//计算频率 越大越精确
    private final float RATE=30f;//现实世界与屏幕比例 30px : 1m
    private Body body;
    private BitmapGameObj qiuGameObj2;
    private Body polygon;
    private Body polygon2;
    private LineBody lineBody;
    private Body polygonForLine;
    private Body circleDef;
    private CircleGameObj circleGameObj;

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
        setKeepScreenOn(true);
        setFocusable(true);
        setOnClickListener(this);

        qiuGameObj = new BitmapGameObj(getContext());
        addGameObj(qiuGameObj);
        qiuGameObj.x=(screenWitdh-qiuGameObj.width)/2;
        qiuGameObj.y=50;
        qiuGameObj.index=2;
        qiuGameObj.isVisible=true;
        imgGameObj = new RectGameObj(getContext());
        addGameObj(imgGameObj);
        imgGameObj.index=5;
        imgGameObj.x=(screenWitdh-imgGameObj.width)/2;
        imgGameObj.y=-400;
        qiuGameObj2 = new BitmapGameObj(getContext());
        addGameObj(qiuGameObj2);
        qiuGameObj2.index=0;
        qiuGameObj2.x=(screenWitdh-qiuGameObj2.width)/2;
        qiuGameObj2.y=900;
        lineBody = new LineBody(getContext());
        addGameObj(lineBody);
        lineBody.x=(screenWitdh-lineBody.width)/2;
        lineBody.y=0;
        circleGameObj = new CircleGameObj(getContext());
        addGameObj(circleGameObj);
        circleGameObj.x=(screenWitdh-circleGameObj.width)/2;
        circleGameObj.y=0;
        useSort();

        //设置物理世界
        AABB aabb = new AABB();
        Vec2 gravity = new Vec2(0f, 10f);
        aabb.lowerBound.set(-100f, -100f);
        aabb.upperBound.set(100f, 100f);
        world = new World(aabb, gravity, true);

        //创建物体
        body = createPolygon(this.qiuGameObj.x, this.qiuGameObj.y, this.qiuGameObj.img.getWidth(), this.qiuGameObj.img.getHeight(), false);
        polygon = createPolygon(qiuGameObj2.x, qiuGameObj2.y, qiuGameObj2.img.getWidth(), qiuGameObj2.img.getHeight(), true);
        polygon2 = createPolygon(imgGameObj.x, imgGameObj.y, imgGameObj.width, imgGameObj.height, false);
        polygonForLine = createPolygonForLine( lineBody.x, lineBody.y, lineBody.width, lineBody.height, false);
        circleDef = createCircleDef(circleGameObj.x, circleGameObj.y, circleGameObj.radius, false);
        addGameBodyBind(circleGameObj,circleDef);
        addGameBodyBind(imgGameObj,polygon2);
        addGameBodyBind(qiuGameObj,body);
        addGameBodyBind(qiuGameObj2, polygon);
        addGameBodyBind(lineBody,polygonForLine);

        //彭转检测
        imgGameObj.body.getShapeList().getFilterData().groupIndex=3;
        imgGameObj.body.getShapeList().getFilterData().maskBits=4;//指定我要碰你

        qiuGameObj2.body.getShapeList().getFilterData().categoryBits=4;
        qiuGameObj2.body.getShapeList().getFilterData().groupIndex=4;



        world.setContactListener(this);
    }

    @Override
    public void start() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                invalidate();
                updateView();
                handler.postDelayed(this, (long) (timeStep*1000));
            }
        });
    }


    @Override
    public float getRate() {
        return RATE;
    }

    @Override
    public World getWorld() {
        return world;
    }

    private void updateView() {
        bindGameBody(timeStep,iterations);
    }


    @Override
    public void onClick(View v) {
        imgGameObj.body.applyForce(new Vec2(0,-5000),imgGameObj.body.getWorldCenter());
        Log.i(tag,"touch");
    }

    @Override
    public void add(ContactPoint contactPoint) {
        //每当有碰撞会调用此函数添加新的接触点
        Log.i(tag,"add=="+contactPoint.toString());
    }

    @Override
    public void persist(ContactPoint contactPoint) {
        // 持续碰撞调用此函数
        Log.i(tag,"persist=="+contactPoint.toString());
    }

    @Override
    public void remove(ContactPoint contactPoint) {
        //脱离碰撞调用此函
        Log.i(tag,"remove=="+contactPoint.toString());
    }

    @Override
    public void result(ContactResult contactResult) {
        // 发生碰撞（有新的接触点被监听到）会调用此函数
        // 持续碰撞时也会调用此函数
        Log.i(tag,"result=="+contactResult.toString());
    }
}
