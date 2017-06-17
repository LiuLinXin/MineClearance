package com.hx.mineclearance;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

/**
 * Created by sober_philer on 2017/5/28.
 */

public class MainActivity extends Activity implements View.OnClickListener, ContentAdapter.SuccessInterface {

    private RecyclerView rv;
    private ContentAdapter contentAdapter;
    private int[][] trueArrays = new int[ROW][COLUM];//-1表示雷， 0 - 8表示周围的雷数量
    private int[][] nowArrays = new int[ROW][COLUM];//-3表示标记为雷, -2表示没任何操作，-1表示雷, 0-8表示周围的雷
    public static final int ROW = 16, COLUM = 16;//row有多少行, colum有多少列
    public static final int MINE_NUM = 40;//雷的数量
    private TextView tvLastMine, tvUseTime;
    private int useTime;//本局用时
    private ImageView mark;
    private AIPlayer aiPlayer;

    private Handler timeHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            tvUseTime.setText(""+useTime++);
            timeHander.sendEmptyMessageDelayed(222, 1000);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        tvLastMine = (TextView) findViewById(R.id.tvLastMine);
        tvUseTime = (TextView) findViewById(R.id.tvUseTime);
        mark = (ImageView) findViewById(R.id.mark);
        tvLastMine.setText(""+MINE_NUM);
        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new GridLayoutManager(this, COLUM));
        rv.setAdapter(contentAdapter = new ContentAdapter(trueArrays, nowArrays, this, tvLastMine, mark));
        contentAdapter.addSuccessListener(this);
        aiPlayer = new AIPlayer(contentAdapter, nowArrays, this);

        findViewById(R.id.buRestart).setOnClickListener(this);
        findViewById(R.id.mark).setOnClickListener(this);
        findViewById(R.id.buAuto).setOnClickListener(this);
    }

    @Override
    public void success(){
        timeHander.removeMessages(222);
    }

    @Override
    public void fail() {

    }

    /**
     * 初始化
     */
    private void init(){
        useTime = 0;
        if(contentAdapter != null){
            contentAdapter.init();
        }
        timeHander.removeMessages(222);
        timeHander.sendEmptyMessage(222);
        for (int[] tempArray : nowArrays){
            for (int i = 0; i < COLUM; i++){
                tempArray[i] = -2;
            }
        }
        for (int[] tempArray : trueArrays){
            for (int i = 0; i < COLUM; i++){
                tempArray[i] = 0;
            }
        }
        initArrays();
    }

    /**
     * 初始化雷区与数组
     */
    private void initArrays(){
        initMine();
        initArray();
    }

    /**
     * 随机生成40个雷
     */
    private void initMine(){
        Random random = new Random();
        int maxIndex = trueArrays.length * trueArrays[0].length;
        int mineNum = 0;
        do{
            int temp = random.nextInt(maxIndex);
            int tempRow = temp / COLUM;
            int tempColum = temp % COLUM;
            if(trueArrays[tempRow][tempColum] != -1){
                trueArrays[tempRow][tempColum] = -1;
                mineNum++;
            }
        }while (mineNum < MINE_NUM);
    }

    /**
     * 根据雷初始化数组，这个操作需要在初始化雷之后进行
     */
    private void initArray(){
        for (int i = 0; i < ROW; i++){
            for (int j = 0; j < COLUM; j++){
                if(trueArrays[i][j] == -1){
                    continue;
                }

                trueArrays[i][j] = groundMineNum(i, j);
            }
        }
    }

    /**
     * 计算一个地区周围雷的数量
     */
    private int groundMineNum(int row, int colum){
        int result = 0;
        if(row - 1 >= 0 && colum - 1 >= 0){//左上角
            if (trueArrays[row - 1][colum - 1] == -1) {
                result++;
            }
        }
        if(row - 1 >= 0){//上方
            if (trueArrays[row - 1][colum] == -1) {
                result++;
            }
        }
        if(row - 1 >= 0 && colum + 1 < COLUM){//右上角
            if (trueArrays[row - 1][colum + 1] == -1) {
                result++;
            }
        }
        if(colum - 1 >= 0){//左方
            if (trueArrays[row][colum - 1] == -1) {
                result++;
            }
        }
        if(colum + 1 < COLUM){//右方
            if (trueArrays[row][colum + 1] == -1) {
                result++;
            }
        }
        if(row + 1 < ROW && colum - 1 >= 0){//左下角
            if (trueArrays[row + 1][colum - 1] == -1) {
                result++;
            }
        }
        if(row + 1 < ROW){//下方
            if (trueArrays[row + 1][colum] == -1) {
                result++;
            }
        }
        if(row + 1 < ROW && colum + 1 < COLUM){//右下角
            if (trueArrays[row + 1][colum + 1] == -1) {
                result++;
            }
        }
        return result;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.buRestart){
            init();
            contentAdapter.myNotifyDataSetChanged();
        }else if(id == R.id.mark){
            contentAdapter.setMarked();
        }else if(id == R.id.buAuto){
            new Thread(aiPlayer).start();
        }
    }
}
