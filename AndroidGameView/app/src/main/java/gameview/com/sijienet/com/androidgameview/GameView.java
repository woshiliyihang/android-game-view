package gameview.com.sijienet.com.androidgameview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
import org.jbox2d.dynamics.joints.DistanceJoint;
import org.jbox2d.dynamics.joints.DistanceJointDef;
import org.jbox2d.dynamics.joints.GearJoint;
import org.jbox2d.dynamics.joints.GearJointDef;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.MouseJoint;
import org.jbox2d.dynamics.joints.MouseJointDef;
import org.jbox2d.dynamics.joints.PrismaticJoint;
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
    private Body topDef;
    private Body bottomDef;
    private DistanceJoint joint;
    private RectGameObj xuanZhuan;
    private RectGameObj xuanZhuan2;
    private Body xuanDef;
    private Body xuanDef2;
    private RevoluteJoint xuanJoin;
    private RectGameObj xuanZhuan3;
    private Body xuanDef3;
    private RectGameObj xuanZhuan4;
    private Body xuanDef4;
    private RectGameObj huanLun1;
    private Body huaLunDef1;
    private RectGameObj huanLun2;
    private Body huaLunDef2;
    private HuaLunDrawLine huanLun3;
    private RectGameObj moveObj;
    private Body moveObjBody;
    private RectGameObj moveObj2;
    private Body moveObjBody2;
    private MouseJoint mouseJoint;
    private float touchX;
    private float touchY;
    private Paint paint;

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

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(10);
        paint.setColor(Color.BLACK);

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
        RectGameObj rectGameObj = new RectGameObj(getContext());
        addGameObj(rectGameObj);
        rectGameObj.x=0;
        rectGameObj.y=300;
        rectGameObj.width=200;
        rectGameObj.height=10;
        RectGameObj rectGameObj1 = new RectGameObj(getContext());
        rectGameObj1.x=0;
        rectGameObj1.y=250;
        rectGameObj1.width=100;
        rectGameObj1.height=10;
        addGameObj(rectGameObj1);

        RectGameObj rectGameObj2 = new RectGameObj(getContext());
        rectGameObj2.x=100;
        rectGameObj2.y=600;
        rectGameObj2.width=100;
        rectGameObj2.height=20;
        addGameObj(rectGameObj2);

        LineGameObj lineGameObj = new LineGameObj(getContext());
        addGameObj(lineGameObj);
        lineGameObj.index=8;

        xuanZhuan = new RectGameObj(getContext());
        xuanZhuan.x=0;
        xuanZhuan.y=805;
        xuanZhuan.width=200;
        xuanZhuan.height=10;
        addGameObj(xuanZhuan);

        xuanZhuan2 = new RectGameObj(getContext());
        xuanZhuan2.x=0;
        xuanZhuan2.y=800;
        xuanZhuan2.width=200;
        xuanZhuan2.height=20;
        addGameObj(xuanZhuan2);

        xuanZhuan3 = new RectGameObj(getContext());
        xuanZhuan3.x=0;
        xuanZhuan3.y=1205;
        xuanZhuan3.width=200;
        xuanZhuan3.height=10;
        addGameObj(xuanZhuan3);

        xuanZhuan4 = new RectGameObj(getContext());
        xuanZhuan4.x=0;
        xuanZhuan4.y=1705;
        xuanZhuan4.width=200;
        xuanZhuan4.height=10;
        addGameObj(xuanZhuan4);

        huanLun1 = new RectGameObj(getContext());
        huanLun1.width=100;
        huanLun1.height=200;
        huanLun1.x=(screenWitdh-huanLun1.width)/2;
        huanLun1.y=1700;
        addGameObj(huanLun1);

        huanLun2 = new RectGameObj(getContext());
        huanLun2.width=100;
        huanLun2.height=100;
        huanLun2.x=(screenWitdh-huanLun2.width)/2+300;
        huanLun2.y=1700;
        addGameObj(huanLun2);

        huanLun3 = new HuaLunDrawLine(getContext());
        huanLun3.width=100;
        huanLun3.height=100;
        huanLun3.x=(screenWitdh-huanLun2.width)/2+300;
        huanLun3.y=1700;
        addGameObj(huanLun3);

        moveObj = new RectGameObj(getContext());
        moveObj.width=100;
        moveObj.height=100;
        moveObj.x=570;
        moveObj.y=0;
        addGameObj(moveObj);

        moveObj2 = new RectGameObj(getContext());
        moveObj2.width=120;
        moveObj2.height=100;
        moveObj2.x=560;
        moveObj2.y=140;
        addGameObj(moveObj2);

        MoveLine moveLine = new MoveLine(getContext());
        addGameObj(moveLine);

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
        topDef = createPolygon(rectGameObj1.x, rectGameObj1.y, rectGameObj1.width, rectGameObj1.height, false);
        bottomDef = createPolygon(rectGameObj2.x, rectGameObj2.y, rectGameObj2.width, rectGameObj2.height, false);
        xuanDef = createPolygon(xuanZhuan.x, xuanZhuan.y, xuanZhuan.width, xuanZhuan.height, false);
        xuanDef2 = createPolygon(xuanZhuan2.x, xuanZhuan2.y, xuanZhuan2.width, xuanZhuan2.height, true);
        xuanDef3 = createPolygon(xuanZhuan3.x, xuanZhuan3.y, xuanZhuan3.width, xuanZhuan3.height, false);
        xuanDef4 = createPolygon(xuanZhuan4.x, xuanZhuan4.y, xuanZhuan4.width, xuanZhuan4.height, false);
        huaLunDef1 = createPolygon(huanLun1.x, huanLun1.y, huanLun1.width, huanLun1.height, false);
        huaLunDef2 = createPolygon(huanLun2.x, huanLun2.y, huanLun2.width, huanLun2.height, false);
        moveObjBody = createPolygon(moveObj.x, moveObj.y, moveObj.width, moveObj.height, false);
        moveObjBody2 = createPolygon(moveObj2.x, moveObj2.y, moveObj2.width, moveObj2.height, false);

        addGameBodyBind(moveObj2,moveObjBody2);
        addGameBodyBind(moveObj,moveObjBody);
        addGameBodyBind(huanLun2,huaLunDef2);
        addGameBodyBind(huanLun1, huaLunDef1);
        addGameBodyBind(xuanZhuan4,xuanDef4);
        addGameBodyBind(xuanZhuan3,xuanDef3);
        addGameBodyBind(xuanZhuan,xuanDef);
        addGameBodyBind(xuanZhuan2,xuanDef2);
        addGameBodyBind(rectGameObj,createPolygon(rectGameObj.x,rectGameObj.y,rectGameObj.width,rectGameObj.height,true));
        addGameBodyBind(circleGameObj,circleDef);
        addGameBodyBind(imgGameObj,polygon2);
        addGameBodyBind(qiuGameObj,body);
        addGameBodyBind(qiuGameObj2, this.polygon);
        addGameBodyBind(lineBody,polygonForLine);
        addGameBodyBind(rectGameObj1, topDef);
        addGameBodyBind(rectGameObj2,bottomDef);

        //赋值
        moveLine.moveObjDef2=moveObjBody2;
        moveLine.moveObjDef=moveObjBody;
        moveLine.rate=RATE;
        huanLun3.body=huaLunDef1;
        huanLun3.body2=huaLunDef2;
        huanLun3.screentWidth=screenWitdh;
        huanLun3.RATE=RATE;

        //彭转检测
        imgGameObj.body.getShapeList().getFilterData().groupIndex=3;
        imgGameObj.body.getShapeList().getFilterData().maskBits=4;//指定我要碰你

        qiuGameObj2.body.getShapeList().getFilterData().categoryBits=4;
        qiuGameObj2.body.getShapeList().getFilterData().groupIndex=4;

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


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        touchX = event.getX();
        touchY = event.getY();
        polygon2.wakeUp();
        mouseJoint.m_target.set(touchX/RATE,touchY/RATE);
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(mouseJoint.m_target.x*RATE,mouseJoint.m_target.y*RATE, polygon2.getWorldCenter().x*RATE, polygon2.getWorldCenter().y*RATE, paint);
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
        prismaticJointDef.initialize(world.getGroundBody(),moveObjBody,moveObjBody.getWorldCenter(),new Vec2(1,0));
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
        prismaticJointDef.initialize(moveObjBody,moveObjBody2,moveObjBody.getWorldCenter(),vec2);
        Joint joint = world.createJoint(prismaticJointDef);
    }


    //双还轮方式
    private void huaLunDouble(){
        PulleyJointDef pulleyJointDef=new PulleyJointDef();
        Vec2 vec2=new Vec2((huanLun1.x+huanLun1.width/2)/RATE, (huanLun1.y-300)/RATE);
        Vec2 vec21=new Vec2( (huanLun2.x+huanLun1.width/2)/RATE, (huanLun2.y-300)/RATE );
        pulleyJointDef.initialize(huaLunDef1,huaLunDef2,vec2,vec21,huaLunDef1.getWorldCenter(),huaLunDef2.getWorldCenter(),1f);
        Joint joint = world.createJoint(pulleyJointDef);
    }

    private void chaLun(RevoluteJoint revoluteJoint, RevoluteJoint revoluteJoint1) {
        GearJointDef gearJointDef=new GearJointDef();
        gearJointDef.joint1=revoluteJoint;
        gearJointDef.joint2=revoluteJoint1;
        gearJointDef.body1=xuanDef3;
        gearJointDef.body2=xuanDef4;
        gearJointDef.ratio=2;//角度比
        GearJoint gearJoint= (GearJoint) world.createJoint(gearJointDef);
    }

    private void addBodyMaDa() {
        RevoluteJointDef revoluteJointDef=new RevoluteJointDef();
        revoluteJointDef.initialize(xuanDef,xuanDef2,xuanDef.getWorldCenter());
        revoluteJointDef.maxMotorTorque=100;//扭矩
        revoluteJointDef.motorSpeed=10;//速度
        revoluteJointDef.enableMotor=true;//启动
        xuanJoin = (RevoluteJoint) world.createJoint(revoluteJointDef);
    }

    private RevoluteJoint addBodyMaDa3() {
        RevoluteJointDef revoluteJointDef=new RevoluteJointDef();
        revoluteJointDef.initialize(world.getGroundBody(),xuanDef3,xuanDef3.getWorldCenter());
        revoluteJointDef.maxMotorTorque=20;//扭矩
        revoluteJointDef.motorSpeed=20;//速度
        revoluteJointDef.enableMotor=true;//启动
        RevoluteJoint joint = (RevoluteJoint) world.createJoint(revoluteJointDef);
        return joint;
    }

    private RevoluteJoint addBodyMaDa4() {
        RevoluteJointDef revoluteJointDef=new RevoluteJointDef();
        revoluteJointDef.initialize(world.getGroundBody(),xuanDef4,xuanDef4.getWorldCenter());
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
