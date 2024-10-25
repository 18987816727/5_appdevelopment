package com.example.android_snack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.renderscript.ScriptC;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

//难点在于线程管理和图像刷新
/**
 * The type Main activity：小蛇移动及方向控制逻辑处理
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FrameLayout Living_Space;
    private boolean isstar=false;
    //    handler 用于处理从 Timer 发送的消息，主要是处理图像刷新
    private Timer timer;
    //    定义了一些按钮
    private Button BT_isstar,BT_RIGHT,BT_UP,BT_DOWN,BT_LEFT;
    private TextView prompt,ScoreView,MostScore;
    //    direction 表示当前移动的方向
    private int direction;
    //    weight 和 height 是游戏区域的宽度和高度。
    private int weight,height;
    private boolean islive;
    //    ScoreView, MostScore 是用于显示提示信息和分数的文本视图。
    private int score,time,mostscore;
    //    images 代表小蛇及食物等图像。
    Images images;
    MoveSnack movesnack;
    SharedPre shared;
    private Handler handler;


    //    movesnack 是一个线程，负责处理小蛇的移动。
//    使用单独的线程来控制小蛇移动的主要原因是保持用户界面（UI）的响应性，主线程阻塞
    class MoveSnack extends Thread{
        @SuppressLint("HandlerLeak")
       // @Override
        public void run() {
            super.run();
            Looper.prepare();
//            处理器
            handler=new Handler(){
//                传入了时间消息
                public void handleMessage(@NonNull Message msg) {
                    //    isstar 用来控制游戏是否开始/暂停。
                    //    islive 标记小蛇是否存活。
                    if(isstar==true&&islive==true){
                        //方向控制
                        if(msg.what==Macro.Right) {
                            images.setBitmapX(images.getBitmapX() + Macro.UNIT);//The constant UNIT：定义小蛇移动的基本单位,x轴+一个移动单位
                            images.setBitmapY(images.getBitmapY());             //y轴坐标不动
                        }
                        if(msg.what==Macro.Up) {
                            images.setBitmapY(images.getBitmapY()-Macro.UNIT);
                            images.setBitmapX(images.getBitmapX());
                        }
                        if(msg.what==Macro.Left) {
                            images.setBitmapX(images.getBitmapX()-Macro.UNIT);
                            images.setBitmapY(images.getBitmapY());
                        }
                        if(msg.what==Macro.Dwon) {
                            images.setBitmapY(images.getBitmapY()+Macro.UNIT);
                            images.setBitmapX(images.getBitmapX());
                        }
                        //吃掉食物处理
                        if((images.getBitmapX()==images.getFoodX())&&(images.getBitmapY()==images.getFoodY())){
                            images.setLenght(images.getLenght()+1);
                            int FoodX=(int)(Math.random()*(weight/Macro.UNIT))*Macro.UNIT;
                            int FoodY=(int)(Math.random()*(height/Macro.UNIT))*Macro.UNIT;
                            if(FoodX==images.getBitmapX()&&FoodY==images.getBitmapY()){
                                FoodX++;
                                FoodY++;
                            }
                            images.setFoodX(FoodX);
                            images.setFoodY(FoodY);
                            score+=Macro.SCORE_UNIT;
                            if(score%Macro.FOOD==0){
                                timer.cancel();
                                if(time<Macro.MAX_DSPEED)
                                    time+=Macro.SPEED;
                                StarViev(time);
                            }
                        }
                        images.invalidate();

                        //线程中更新控件，用来计分
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ScoreView.setText(Macro.SCORE+String.valueOf(score));
                            }
                        });

                        //死亡判断
                        if(images.getBitmapX()>=weight||images.getBitmapX()<0
                                ||images.getBitmapY()<0||images.getBitmapY()>=height
                                ||images.isIslive()==false){
                            islive=false;
                            //小蛇死亡提醒
                            runOnUiThread(new Runnable() {
                                @SuppressLint("SuspiciousIndentation")
                                @Override
                                public void run() {
                                    BT_isstar.setText(R.string.restar);
                                    prompt.setText(R.string.promptrestar);
                                    prompt.setVisibility(View.VISIBLE);
                                    if(score>mostscore)
                                        mostscore=score;
                                        shared.save(mostscore);
                                }
                            });
                        }
                    }
                    super.handleMessage(msg);
                }


            };
            Looper.loop();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //        初始化蛇移动线程，并启动游戏
        movesnack=new MoveSnack();
        movesnack.start();
//        初始化各种控件
        Init();
        StarViev(time);
        //将图片加载到布局中
        Living_Space.addView(images);
        //获取活动空间的宽和高
        Living_Space.post(new Runnable() {
            @Override
            public void run() {
                weight=Living_Space.getWidth();
                height=Living_Space.getHeight();
            }
        });

    }

//    生命周期创建与摧毁
    @Override
    protected void onDestroy() {
        super.onDestroy();
//        消息处理器的移除
        handler.removeCallbacksAndMessages(null);
        timer.cancel();
    }

    /**
     * Init：初始化该界面控件及参数
     * 和布局页面相互关联
     */
//初始化
    void Init(){
        Living_Space=findViewById(R.id.framelayout);
        BT_isstar=findViewById(R.id.isstar);
        BT_RIGHT=findViewById(R.id.Right);
        BT_UP=findViewById(R.id.Up);
        BT_DOWN=findViewById(R.id.Dwon);
        BT_LEFT=findViewById(R.id.Left);
        prompt=findViewById(R.id.prompt);
        ScoreView=findViewById(R.id.Score);
        MostScore=findViewById(R.id.MostScore);

        images=new Images(MainActivity.this);
        shared=new SharedPre(MainActivity.this);
        direction=Macro.Right;
        images.Init();
        images.Size();
        islive=true;
        score=0;
        mostscore=shared.read();
        time=0;

        BT_isstar.setOnClickListener(this);
        BT_RIGHT.setOnClickListener(this);
        BT_UP.setOnClickListener(this);
        BT_LEFT.setOnClickListener(this);
        BT_DOWN.setOnClickListener(this);
        MostScore.setText(Macro.MOST_SCORE+shared.read());
    }

    /**
     * onClick:该界面按钮监听事件
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();

// 游戏的开始、暂停按钮
        if (id == R.id.isstar) {
            isstar = !isstar;
            if (isstar && islive) {
                BT_isstar.setText(R.string.stop);
                prompt.setVisibility(View.GONE);
            } else if (!isstar && islive) {
                BT_isstar.setText(R.string.star);
                prompt.setVisibility(View.VISIBLE);
            } else if (!islive) {
                finish();
            }
        }
// 控制小蛇方向向右
        else if (id == R.id.Right) {
            if (isstar) {
                direction = Macro.Right;
                images.setDirection(direction);
            }
        }
// 控制小蛇方向向上
        else if (id == R.id.Up) {
            if (isstar) {
                direction = Macro.Up;
                images.setDirection(direction);
            }
        }
// 控制小蛇方向向左
        else if (id == R.id.Left) {
            if (isstar) {
                direction = Macro.Left;
                images.setDirection(direction);
            }
        }
// 控制小蛇方向向下
        else if (id == R.id.Dwon) { // 注意：通常应该是 R.id.Down，检查是否有拼写错误
            if (isstar) {
                direction = Macro.Dwon; // 同样，确认 Macro.Dwon 是否正确，通常应为 Macro.Down
                images.setDirection(direction);
            }
        }
    }


    /**
     * Star viev 方法是用来设置一个定时器，定期发送消息给 Handler，从而控制小蛇的移动速度
     *
     * @param time the time
     */

    void StarViev(int time) {
        timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
//                定时发送一下移动方向
                handler.sendEmptyMessage(direction);
            }
//            初始延迟时间和默认周期
        },Macro.DELAY,Macro.PERIOD-time);
    }
}