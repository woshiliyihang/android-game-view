package gameview.com.sijienet.com.androidgameview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import org.jbox2d.collision.AABB;
import org.jbox2d.collision.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.ContactListener;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.ContactPoint;
import org.jbox2d.dynamics.contacts.ContactResult;
import org.jbox2d.dynamics.joints.DistanceJoint;
import org.jbox2d.dynamics.joints.DistanceJointDef;
import org.jbox2d.dynamics.joints.GearJoint;
import org.jbox2d.dynamics.joints.GearJointDef;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.MouseJoint;
import org.jbox2d.dynamics.joints.MouseJointDef;
import org.jbox2d.dynamics.joints.PrismaticJointDef;
import org.jbox2d.dynamics.joints.PulleyJointDef;
import org.jbox2d.dynamics.joints.RevoluteJoint;
import org.jbox2d.dynamics.joints.RevoluteJointDef;

import gameview.com.sijienet.com.androidgameviewbase.AndroidGameViewBase;

/**
 * Created by user on 2017/5/19.
 */
public class GameView extends AndroidGameViewBase implements View.OnClickListener, ContactListener {

    public static final String tag="GameView";

    private BitmapGameObj mBallTypeGameObj;
    private RectGameObj mImgGameObj;
    private World world;
    private float timeStep=1f / 60f;//模式真实世界频率
    private int iterations=10;//计算频率 越大越精确
    private final float RATE=30f;//现实世界与屏幕比例 30px : 1m
    private Body body;
    private BitmapGameObj mBallTypeGameObj2;
    private Body polygon;
    private Body polygon2;
    private LineBody lineBody;
    private Body polygonForLine;
    private Body circleDef;
    private CircleGameObj circleGameObj;
    private Body topDef;
    private Body bottomDef;
    private DistanceJoint joint;
    private RectGameObj mRotateGameObj;
    private RectGameObj mRotateGameObj2;
    private Body mRotateDef;
    private Body mRotateDef2;
    private RevoluteJoint rotateJoint;
    private RectGameObj mRotateGameObj3;
    private Body mRotateDef3;
    private RectGameObj mRotateGameObj4;
    private Body mRotateDef4;
    private RectGameObj mPulleyGameObj1;
    private Body mPulleyDef1;
    private RectGameObj mPulleyGameObj2;
    private Body mPulleyDef2;
    private PulleyLine mPulleyLine3;
    private RectGameObj mMoveGameObj;
    private Body mMoveDef;
    private RectGameObj mRectGameObj2;
    private Body mMoveDef2;
    private MouseJoint mouseJoint;
    private float touchX;
    private float touchY;
    private Paint paint;
    private AABB aabb1;
    private RectGameObj mRectGameObj1;
    private RectGameObj rectGameObj2;
    private RectGameObj mRectGameObj;
    private MoveLine mMoveLine;
    private LineGameObj lineGameObj;

    public GameView(Context context) {
        super(context);
    }

    @Override
    public void init() {
        initConfig();

        world=initBox2dWorld(new Vec2(0f, 10f));

        initGameObject();

        useSort();

        initBodyDef();

        initLogic();
    }

    private void initLogic() {
        //赋值
        mMoveLine.moveObjDef2= mMoveDef2;
        mMoveLine.moveObjDef= mMoveDef;
        mMoveLine.rate=RATE;
        mPulleyLine3.body= mPulleyDef1;
        mPulleyLine3.body2= mPulleyDef2;
        mPulleyLine3.screentWidth=screenWitdh;
        mPulleyLine3.RATE=RATE;

        //彭转检测
        mImgGameObj.body.getShapeList().getFilterData().groupIndex=3;
        mImgGameObj.body.getShapeList().getFilterData().maskBits=4;//指定我要碰你

        mBallTypeGameObj2.body.getShapeList().getFilterData().categoryBits=4;
        mBallTypeGameObj2.body.getShapeList().getFilterData().groupIndex=4;

        //添加关节
        addBodyJoin();
        lineGameObj.distanceJoint=joint;
        lineGameObj.rate=RATE;

        //添加关节
        addBodyMaDa();

        RevoluteJoint revoluteJoint = addBodyMaDa3();
        RevoluteJoint revoluteJoint1 = addBodyMaDa4();
        //齿轮关节
        chaLun(revoluteJoint,revoluteJoint1);

        //滑轮关节
        huaLunDouble();

        //移动关节
//        moveJoint();
        moveJoint2();

        //添加点击关节
        MouseJoint mouseJoint = getMouseJoint();

        world.setContactListener(this);
    }

    private void initBodyDef() {
        //创建形状
        body = createPolygon(this.mBallTypeGameObj.x, this.mBallTypeGameObj.y, this.mBallTypeGameObj.img.getWidth(), this.mBallTypeGameObj.img.getHeight(), false);
        polygon = createPolygon(mBallTypeGameObj2.x, mBallTypeGameObj2.y, mBallTypeGameObj2.img.getWidth(), mBallTypeGameObj2.img.getHeight(), true);
        polygon2 = createPolygon(mImgGameObj.x, mImgGameObj.y, mImgGameObj.width, mImgGameObj.height, false);
        polygonForLine = createPolygonForLine( lineBody.x, lineBody.y, lineBody.width, lineBody.height, false);
        circleDef = createCircleDef(circleGameObj.x, circleGameObj.y, circleGameObj.radius, false);
        topDef = createPolygon(mRectGameObj1.x, mRectGameObj1.y, mRectGameObj1.width, mRectGameObj1.height, false);
        bottomDef = createPolygon(rectGameObj2.x, rectGameObj2.y, rectGameObj2.width, rectGameObj2.height, false);
        mRotateDef = createPolygon(mRotateGameObj.x, mRotateGameObj.y, mRotateGameObj.width, mRotateGameObj.height, false);
        mRotateDef2 = createPolygon(mRotateGameObj2.x, mRotateGameObj2.y, mRotateGameObj2.width, mRotateGameObj2.height, true);
        mRotateDef3 = createPolygon(mRotateGameObj3.x, mRotateGameObj3.y, mRotateGameObj3.width, mRotateGameObj3.height, false);
        mRotateDef4 = createPolygon(mRotateGameObj4.x, mRotateGameObj4.y, mRotateGameObj4.width, mRotateGameObj4.height, false);
        mPulleyDef1 = createPolygon(mPulleyGameObj1.x, mPulleyGameObj1.y, mPulleyGameObj1.width, mPulleyGameObj1.height, false);
        mPulleyDef2 = createPolygon(mPulleyGameObj2.x, mPulleyGameObj2.y, mPulleyGameObj2.width, mPulleyGameObj2.height, false);
        mMoveDef = createPolygon(mMoveGameObj.x, mMoveGameObj.y, mMoveGameObj.width, mMoveGameObj.height, false);
        mMoveDef2 = createPolygon(mRectGameObj2.x, mRectGameObj2.y, mRectGameObj2.width, mRectGameObj2.height, false);

        //绑定gameobj和bodydef
        addGameBodyBind(mRectGameObj2, mMoveDef2);
        addGameBodyBind(mMoveGameObj, mMoveDef);
        addGameBodyBind(mPulleyGameObj2, mPulleyDef2);
        addGameBodyBind(mPulleyGameObj1, mPulleyDef1);
        addGameBodyBind(mRotateGameObj4, mRotateDef4);
        addGameBodyBind(mRotateGameObj3, mRotateDef3);
        addGameBodyBind(mRotateGameObj, mRotateDef);
        addGameBodyBind(mRotateGameObj2, mRotateDef2);
        addGameBodyBind(mRectGameObj,createPolygon(mRectGameObj.x, mRectGameObj.y, mRectGameObj.width, mRectGameObj.height,true));
        addGameBodyBind(circleGameObj,circleDef);
        addGameBodyBind(mImgGameObj,polygon2);
        addGameBodyBind(mBallTypeGameObj,body);
        addGameBodyBind(mBallTypeGameObj2, this.polygon);
        addGameBodyBind(lineBody,polygonForLine);
        addGameBodyBind(mRectGameObj1, topDef);
        addGameBodyBind(rectGameObj2,bottomDef);

    }

    private void initGameObject() {
        mBallTypeGameObj = new BitmapGameObj(getContext());
        addGameObj(mBallTypeGameObj);
        mBallTypeGameObj.x=(screenWitdh- mBallTypeGameObj.width)/2;
        mBallTypeGameObj.y=50;
        mBallTypeGameObj.index=2;
        mBallTypeGameObj.isVisible=true;
        mImgGameObj = new RectGameObj(getContext());
        addGameObj(mImgGameObj);
        mImgGameObj.index=5;
        mImgGameObj.x=(screenWitdh- mImgGameObj.width)/2;
        mImgGameObj.y=-400;
        mBallTypeGameObj2 = new BitmapGameObj(getContext());
        addGameObj(mBallTypeGameObj2);
        mBallTypeGameObj2.index=0;
        mBallTypeGameObj2.x=(screenWitdh- mBallTypeGameObj2.width)/2;
        mBallTypeGameObj2.y=900;
        lineBody = new LineBody(getContext());
        addGameObj(lineBody);
        lineBody.x=(screenWitdh-lineBody.width)/2;
        lineBody.y=0;
        circleGameObj = new CircleGameObj(getContext());
        addGameObj(circleGameObj);
        circleGameObj.x=(screenWitdh-circleGameObj.width)/2;
        circleGameObj.y=0;
        mRectGameObj = new RectGameObj(getContext());
        addGameObj(mRectGameObj);
        mRectGameObj.x=0;
        mRectGameObj.y=300;
        mRectGameObj.width=200;
        mRectGameObj.height=10;
        mRectGameObj1 = new RectGameObj(getContext());
        mRectGameObj1.x=0;
        mRectGameObj1.y=250;
        mRectGameObj1.width=100;
        mRectGameObj1.height=10;
        addGameObj(mRectGameObj1);

        rectGameObj2 = new RectGameObj(getContext());
        rectGameObj2.x=100;
        rectGameObj2.y=600;
        rectGameObj2.width=100;
        rectGameObj2.height=20;
        addGameObj(rectGameObj2);

        lineGameObj = new LineGameObj(getContext());
        addGameObj(lineGameObj);
        lineGameObj.index=8;

        mRotateGameObj = new RectGameObj(getContext());
        mRotateGameObj.x=0;
        mRotateGameObj.y=805;
        mRotateGameObj.width=200;
        mRotateGameObj.height=10;
        addGameObj(mRotateGameObj);

        mRotateGameObj2 = new RectGameObj(getContext());
        mRotateGameObj2.x=0;
        mRotateGameObj2.y=800;
        mRotateGameObj2.width=200;
        mRotateGameObj2.height=20;
        addGameObj(mRotateGameObj2);

        mRotateGameObj3 = new RectGameObj(getContext());
        mRotateGameObj3.x=0;
        mRotateGameObj3.y=1205;
        mRotateGameObj3.width=200;
        mRotateGameObj3.height=10;
        addGameObj(mRotateGameObj3);

        mRotateGameObj4 = new RectGameObj(getContext());
        mRotateGameObj4.x=0;
        mRotateGameObj4.y=1705;
        mRotateGameObj4.width=200;
        mRotateGameObj4.height=10;
        addGameObj(mRotateGameObj4);

        mPulleyGameObj1 = new RectGameObj(getContext());
        mPulleyGameObj1.width=100;
        mPulleyGameObj1.height=200;
        mPulleyGameObj1.x=(screenWitdh- mPulleyGameObj1.width)/2;
        mPulleyGameObj1.y=1700;
        addGameObj(mPulleyGameObj1);

        mPulleyGameObj2 = new RectGameObj(getContext());
        mPulleyGameObj2.width=100;
        mPulleyGameObj2.height=100;
        mPulleyGameObj2.x=(screenWitdh- mPulleyGameObj2.width)/2+300;
        mPulleyGameObj2.y=1700;
        addGameObj(mPulleyGameObj2);

        mPulleyLine3 = new PulleyLine(getContext());
        mPulleyLine3.width=100;
        mPulleyLine3.height=100;
        mPulleyLine3.x=(screenWitdh- mPulleyGameObj2.width)/2+300;
        mPulleyLine3.y=1700;
        addGameObj(mPulleyLine3);

        mMoveGameObj = new RectGameObj(getContext());
        mMoveGameObj.width=100;
        mMoveGameObj.height=100;
        mMoveGameObj.x=570;
        mMoveGameObj.y=0;
        addGameObj(mMoveGameObj);

        mRectGameObj2 = new RectGameObj(getContext());
        mRectGameObj2.width=120;
        mRectGameObj2.height=100;
        mRectGameObj2.x=560;
        mRectGameObj2.y=140;
        addGameObj(mRectGameObj2);

        mMoveLine = new MoveLine(getContext());
        addGameObj(mMoveLine);
    }

    private void initConfig() {
        setKeepScreenOn(true);
        setFocusable(true);
        setOnClickListener(this);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(10);
        paint.setColor(Color.BLACK);

        aabb1 = new AABB();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        touchX = event.getX();
        touchY = event.getY();
        polygon2.wakeUp();
        mouseJoint.m_target.set(touchX/RATE,touchY/RATE);

        //通过aabb 查找内容body 数量
        aabb1.lowerBound.set(touchX/RATE,touchY/RATE);
        aabb1.upperBound.set((touchX+100)/RATE,(touchY+100)/RATE);
        Shape[] query = world.query(aabb1, 100);
        Log.i(TAG,"body size=="+query.length);
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //自定义回执内容
        canvas.drawLine(mouseJoint.m_target.x*RATE,mouseJoint.m_target.y*RATE, polygon2.getWorldCenter().x*RATE, polygon2.getWorldCenter().y*RATE, paint);
        canvas.drawRect(touchX,touchY,touchX+100, touchY+100,paint);
    }

    //鼠标移动移动关节
    private MouseJoint getMouseJoint(){
        MouseJointDef mouseJointDef=new MouseJointDef();
        mouseJointDef.body1=world.getGroundBody();
        mouseJointDef.body2=polygon2;
        mouseJointDef.target.x= mouseJointDef.body2.getPosition().x;
        mouseJointDef.target.y= mouseJointDef.body2.getPosition().y;
        mouseJointDef.maxForce=100f;//拉力
        mouseJoint= (MouseJoint) world.createJoint(mouseJointDef);
        mouseJoint.m_gamma=20;//弹力
        mouseJoint.m_target.set(600/RATE, 100/RATE);
        return mouseJoint;
    }

    //相对于墙壁的移动关节
    private void moveJoint() {
        PrismaticJointDef prismaticJointDef=new PrismaticJointDef();
        prismaticJointDef.maxMotorForce=90;//最大马力
        prismaticJointDef.motorSpeed=10;//最大玛丽
        prismaticJointDef.enableMotor=true;
        float moveMax=720f;
        prismaticJointDef.lowerTranslation=-moveMax/RATE;// 设置位移最小偏移值
        prismaticJointDef.upperTranslation=moveMax/RATE;//设置位移最大偏移值
        prismaticJointDef.enableLimit=true;//开启限制
        prismaticJointDef.initialize(world.getGroundBody(), mMoveDef, mMoveDef.getWorldCenter(),new Vec2(1,0));
        Joint joint = world.createJoint(prismaticJointDef);
    }

    //移动关节
    private void moveJoint2() {
        PrismaticJointDef prismaticJointDef=new PrismaticJointDef();
        prismaticJointDef.maxMotorForce=10;//最大马力
        prismaticJointDef.motorSpeed=10;//最大玛丽
        prismaticJointDef.enableMotor=true;
        float moveMax=20;
        prismaticJointDef.lowerTranslation=-moveMax/RATE;// 设置位移最小偏移值
        prismaticJointDef.upperTranslation=moveMax/RATE;//设置位移最大偏移值
        prismaticJointDef.enableLimit=true;//开启限制
        Vec2 vec2 = new Vec2(1, 0);//设置伸缩方向还是左右方向 当前左右方向
        prismaticJointDef.initialize(mMoveDef, mMoveDef2, mMoveDef.getWorldCenter(),vec2);
        Joint joint = world.createJoint(prismaticJointDef);
    }


    //双还轮方式
    private void huaLunDouble(){
        PulleyJointDef pulleyJointDef=new PulleyJointDef();
        Vec2 vec2=new Vec2((mPulleyGameObj1.x+ mPulleyGameObj1.width/2)/RATE, (mPulleyGameObj1.y-300)/RATE);
        Vec2 vec21=new Vec2( (mPulleyGameObj2.x+ mPulleyGameObj1.width/2)/RATE, (mPulleyGameObj2.y-300)/RATE );
        pulleyJointDef.initialize(mPulleyDef1, mPulleyDef2,vec2,vec21, mPulleyDef1.getWorldCenter(), mPulleyDef2.getWorldCenter(),1f);
        Joint joint = world.createJoint(pulleyJointDef);
    }

    private void chaLun(RevoluteJoint revoluteJoint, RevoluteJoint revoluteJoint1) {
        GearJointDef gearJointDef=new GearJointDef();
        gearJointDef.joint1=revoluteJoint;
        gearJointDef.joint2=revoluteJoint1;
        gearJointDef.body1= mRotateDef3;
        gearJointDef.body2= mRotateDef4;
        gearJointDef.ratio=2;//角度比
        GearJoint gearJoint= (GearJoint) world.createJoint(gearJointDef);
    }

    private void addBodyMaDa() {
        RevoluteJointDef revoluteJointDef=new RevoluteJointDef();
        revoluteJointDef.initialize(mRotateDef, mRotateDef2, mRotateDef.getWorldCenter());
        revoluteJointDef.maxMotorTorque=100;//扭矩
        revoluteJointDef.motorSpeed=10;//速度
        revoluteJointDef.enableMotor=true;//启动
        rotateJoint = (RevoluteJoint) world.createJoint(revoluteJointDef);
    }

    private RevoluteJoint addBodyMaDa3() {
        RevoluteJointDef revoluteJointDef=new RevoluteJointDef();
        revoluteJointDef.initialize(world.getGroundBody(), mRotateDef3, mRotateDef3.getWorldCenter());
        revoluteJointDef.maxMotorTorque=20;//扭矩
        revoluteJointDef.motorSpeed=20;//速度
        revoluteJointDef.enableMotor=true;//启动
        RevoluteJoint joint = (RevoluteJoint) world.createJoint(revoluteJointDef);
        return joint;
    }

    private RevoluteJoint addBodyMaDa4() {
        RevoluteJointDef revoluteJointDef=new RevoluteJointDef();
        revoluteJointDef.initialize(world.getGroundBody(), mRotateDef4, mRotateDef4.getWorldCenter());
        RevoluteJoint joint = (RevoluteJoint) world.createJoint(revoluteJointDef);
        return joint;
    }

    private void addBodyJoin() {
        DistanceJointDef jointDef=new DistanceJointDef();
        jointDef.initialize(topDef,bottomDef,topDef.getWorldCenter(),bottomDef.getWorldCenter());
        joint = (DistanceJoint)world.createJoint(jointDef);
    }

    @Override
    public void start() {
        startBox2dWord(timeStep, iterations);
    }

    @Override
    public float getRate() {
        return RATE;
    }

    @Override
    public World getWorld() {
        return world;
    }


    @Override
    public void onClick(View v) {
        mImgGameObj.body.applyForce(new Vec2(0,-5000), mImgGameObj.body.getWorldCenter());
        bottomDef.applyForce(new Vec2(10,-1000),bottomDef.getWorldCenter());
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
