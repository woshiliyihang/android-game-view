package gameview.com.sijienet.com.androidgameview;

import android.content.Context;
import android.util.AttributeSet;

import org.jbox2d.collision.AABB;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

import gameview.com.sijienet.com.androidgameviewbase.AndroidGameViewBase;

/**
 * Created by user on 2017/5/19.
 */
public class GameView extends AndroidGameViewBase {

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

        qiuGameObj = new BitmapGameObj(getContext());
        addGameObj(qiuGameObj);
        qiuGameObj.x=(screenWitdh-qiuGameObj.width)/2;
        qiuGameObj.y=50;
        qiuGameObj.index=2;
        qiuGameObj.isVisible=true;
        imgGameObj = new RectGameObj(getContext());
        addGameObj(imgGameObj);
        imgGameObj.index=1;
        imgGameObj.x=(screenWitdh-imgGameObj.width)/2;
        imgGameObj.y=20;
        qiuGameObj2 = new BitmapGameObj(getContext());
        addGameObj(qiuGameObj2);
        qiuGameObj2.index=2;
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





}
