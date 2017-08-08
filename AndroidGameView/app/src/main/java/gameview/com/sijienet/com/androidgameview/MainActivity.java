package gameview.com.sijienet.com.androidgameview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * 作者：李一航
 * 博客：http://sijienet.com/
 */
public class MainActivity extends AppCompatActivity {

    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameView=new GameView(this);
        setContentView(gameView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gameView.handler.removeCallbacksAndMessages(null);
    }
}
