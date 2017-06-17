package com.hx.mineclearance;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.util.Random;

/**
 * Created by sober_philer on 2017/5/28.
 */

public class AIPlayer implements ContentAdapter.SuccessInterface, Runnable {
    private ContentAdapter contentAdapter;
    private Activity activity;

    //-1表示为雷, -2表示没任何操作，-3表示标记为地雷,0-8表示周围的雷
    private int[][] nowArrays = new int[MainActivity.ROW][MainActivity.COLUM];
    private boolean finished;

    public AIPlayer(ContentAdapter contentAdapter, int[][] nowArrays, Activity activity) {
        this.activity = activity;
        this.contentAdapter = contentAdapter;
        this.nowArrays = nowArrays;
        contentAdapter.addSuccessListener(this);
    }

    private static final int SELECT = 1;
    private static final int MARK_SELECT = 2;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int position = msg.arg1;
            if (msg.what == SELECT){
                contentAdapter.select(position);
            }else if(msg.what == MARK_SELECT){
                contentAdapter.setMarked();
                contentAdapter.select(position);
            }
        }
    };

    @Override
    public void success() {
        finished = true;
    }

    @Override
    public void fail() {
        finished = true;
    }

    @Override
    public void run() {
        finished = false;
        start();
    }

    private void start() {
        while (!finished) {
            Log.i("hx", "start");
            if (!sureOne()) {
//                return;
                randomOne();
            }
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean sureOne() {
        for (int i = 0; i < MainActivity.ROW; i++){
            for (int j = 0; j < MainActivity.COLUM; j++){
                int tempNum = nowArrays[i][j];
                if(tempNum > 0){
                    if(checkNumMine(i, j, tempNum) || checkNumOk(i, j ,tempNum)){
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean checkLegal(int row, int colum){
        if(row >=0 && row < MainActivity.ROW && colum >=0 && colum < MainActivity.COLUM)
            return true;
        return false;
    }

    /**
     * 算法核心代码 - 识别一个数字周围的地雷
     */
    private boolean checkNumMine(int row, int colum, int mineNum){
        boolean result = false;
        int tempRow, tempColum;
        int nowMinu = 0;

        tempRow = row - 1;
        tempColum = colum;
        if (checkLegal(tempRow, tempColum)) {//上方
            if (nowArrays[tempRow][tempColum] <= -1) {
                nowMinu++;
            }
        }

        tempRow = row;
        tempColum = colum - 1;
        if (checkLegal(tempRow, tempColum)) {//左方
            if (nowArrays[tempRow][tempColum] <= -1) {
                nowMinu++;
            }
        }

        tempRow = row;
        tempColum = colum + 1;
        if (checkLegal(tempRow, tempColum)) {//右方
            if (nowArrays[tempRow][tempColum] <= -1) {
                nowMinu++;
            }
        }

        tempRow = row + 1;
        tempColum = colum;
        if (checkLegal(tempRow, tempColum)) {//下方
            if (nowArrays[tempRow][tempColum] <= -1) {
                nowMinu++;
            }
        }

        tempRow = row - 1;
        tempColum = colum - 1;
        if (checkLegal(tempRow, tempColum)) {//左上角
            if (nowArrays[tempRow][tempColum] <= -1) {
                nowMinu++;
            }
        }

        tempRow = row - 1;
        tempColum = colum + 1;
        if (checkLegal(tempRow, tempColum)) {//右上角
            if (nowArrays[tempRow][tempColum] <= -1) {
                nowMinu++;
            }
        }

        tempRow = row + 1;
        tempColum = colum - 1;
        if (checkLegal(tempRow, tempColum)) {//左下角
            if (nowArrays[tempRow][tempColum] <= -1) {
                nowMinu++;
            }
        }

        tempRow = row + 1;
        tempColum = colum + 1;
        if (checkLegal(tempRow, tempColum)) {//右下角
            if (nowArrays[tempRow][tempColum] <= -1) {
                nowMinu++;
            }
        }

        if(nowMinu == mineNum){
            Log.i("hx", row +" : "+ colum +" 可以确定周围的地雷");
            tempRow = row - 1;
            tempColum = colum;
            if (tempRow >= 0) {//上方
                if (nowArrays[tempRow][tempColum] == -2) {
                    result = true;
                    Message message = handler.obtainMessage();
                    message.what = MARK_SELECT;
                    message.arg1 = tempRow * MainActivity.COLUM + tempColum;
                    handler.sendMessage(message);
                }
            }

            tempRow = row;
            tempColum = colum - 1;
            if (tempColum >= 0) {//左方
                if (nowArrays[tempRow][tempColum] == -2) {
                    result = true;
                    Message message = handler.obtainMessage();
                    message.what = MARK_SELECT;
                    message.arg1 = tempRow * MainActivity.COLUM + tempColum;
                    handler.sendMessage(message);
                }
            }

            tempRow = row;
            tempColum = colum + 1;
            if (tempColum < MainActivity.COLUM) {//右方
                if (nowArrays[tempRow][tempColum] == -2) {
                    result = true;
                    Message message = handler.obtainMessage();
                    message.what = MARK_SELECT;
                    message.arg1 = tempRow * MainActivity.COLUM + tempColum;
                    handler.sendMessage(message);
                }
            }

            tempRow = row + 1;
            tempColum = colum;
            if (tempRow < MainActivity.ROW) {//下方
                if (nowArrays[tempRow][tempColum] == -2) {
                    result = true;
                    Message message = handler.obtainMessage();
                    message.what = MARK_SELECT;
                    message.arg1 = tempRow * MainActivity.COLUM + tempColum;
                    handler.sendMessage(message);
                }
            }

            tempRow = row - 1;
            tempColum = colum - 1;
            if (tempRow >= 0 && tempColum >= 0) {//左上角
                if (nowArrays[tempRow][tempColum] == -2) {
                    result = true;
                    Message message = handler.obtainMessage();
                    message.what = MARK_SELECT;
                    message.arg1 = tempRow * MainActivity.COLUM + tempColum;
                    handler.sendMessage(message);
                }
            }

            tempRow = row - 1;
            tempColum = colum + 1;
            if (tempRow >= 0 && tempColum < MainActivity.COLUM) {//右上角
                if (nowArrays[tempRow][tempColum] == -2) {
                    result = true;
                    Message message = handler.obtainMessage();
                    message.what = MARK_SELECT;
                    message.arg1 = tempRow * MainActivity.COLUM + tempColum;
                    handler.sendMessage(message);
                }
            }

            tempRow = row + 1;
            tempColum = colum - 1;
            if (tempRow < MainActivity.ROW && tempColum >= 0) {//左下角
                if (nowArrays[tempRow][tempColum] == -2) {
                    result = true;
                    Message message = handler.obtainMessage();
                    message.what = MARK_SELECT;
                    message.arg1 = tempRow * MainActivity.COLUM + tempColum;
                    handler.sendMessage(message);
                }
            }

            tempRow = row + 1;
            tempColum = colum + 1;
            if (tempRow < MainActivity.ROW && tempColum < MainActivity.COLUM) {//右下角
                if (nowArrays[tempRow][tempColum] == -2) {
                    result = true;
                    Message message = handler.obtainMessage();
                    message.what = MARK_SELECT;
                    message.arg1 = tempRow * MainActivity.COLUM + tempColum;
                    handler.sendMessage(message);
                }
            }
        }

        return result;
    }

    /**
     * 算法核心代码 - 识别一个数字周围的安全地方
     */
    private boolean checkNumOk(int row, int colum, int mineNum){
        boolean result = false;
        int tempRow, tempColum;
        int nowMinu = 0;

        tempRow = row - 1;
        tempColum = colum;
        if (tempRow >= 0) {//上方
            if (nowArrays[tempRow][tempColum] == -3) {
                nowMinu++;
            }
        }

        tempRow = row;
        tempColum = colum - 1;
        if (tempColum >= 0) {//左方
            if (nowArrays[tempRow][tempColum] == -3) {
                nowMinu++;
            }
        }

        tempRow = row;
        tempColum = colum + 1;
        if (tempColum < MainActivity.COLUM) {//右方
            if (nowArrays[tempRow][tempColum] == -3) {
                nowMinu++;
            }
        }

        tempRow = row + 1;
        tempColum = colum;
        if (tempRow < MainActivity.ROW) {//下方
            if (nowArrays[tempRow][tempColum] == -3) {
                nowMinu++;
            }
        }

        tempRow = row - 1;
        tempColum = colum - 1;
        if (tempRow >= 0 && tempColum >= 0) {//左上角
            if (nowArrays[tempRow][tempColum] == -3) {
                nowMinu++;
            }
        }

        tempRow = row - 1;
        tempColum = colum + 1;
        if (tempRow >= 0 && tempColum < MainActivity.COLUM) {//右上角
            if (nowArrays[tempRow][tempColum] == -3) {
                nowMinu++;
            }
        }

        tempRow = row + 1;
        tempColum = colum - 1;
        if (tempRow < MainActivity.ROW && tempColum >= 0) {//左下角
            if (nowArrays[tempRow][tempColum] == -3) {
                nowMinu++;
            }
        }

        tempRow = row + 1;
        tempColum = colum + 1;
        if (tempRow < MainActivity.ROW && tempColum < MainActivity.COLUM) {//右下角
            if (nowArrays[tempRow][tempColum] == -3) {
                nowMinu++;
            }
        }

        if(nowMinu == mineNum){
            Log.i("hx", row +" : "+ colum +" 可以确定周围的安全数字");
            tempRow = row - 1;
            tempColum = colum;
            if (tempRow >= 0) {//上方
                if (nowArrays[tempRow][tempColum] == -2) {
                    result = true;
                    Message message = handler.obtainMessage();
                    message.what = SELECT;
                    message.arg1 = tempRow * MainActivity.COLUM + tempColum;
                    handler.sendMessage(message);
                }
            }

            tempRow = row;
            tempColum = colum - 1;
            if (tempColum >= 0) {//左方
                if (nowArrays[tempRow][tempColum] == -2) {
                    result = true;
                    Message message = handler.obtainMessage();
                    message.what = SELECT;
                    message.arg1 = tempRow * MainActivity.COLUM + tempColum;
                    handler.sendMessage(message);
                }
            }

            tempRow = row;
            tempColum = colum + 1;
            if (tempColum < MainActivity.COLUM) {//右方
                if (nowArrays[tempRow][tempColum] == -2) {
                    result = true;
                    Message message = handler.obtainMessage();
                    message.what = SELECT;
                    message.arg1 = tempRow * MainActivity.COLUM + tempColum;
                    handler.sendMessage(message);
                }
            }

            tempRow = row + 1;
            tempColum = colum;
            if (tempRow < MainActivity.ROW) {//下方
                if (nowArrays[tempRow][tempColum] == -2) {
                    result = true;
                    Message message = handler.obtainMessage();
                    message.what = SELECT;
                    message.arg1 = tempRow * MainActivity.COLUM + tempColum;
                    handler.sendMessage(message);
                }
            }

            tempRow = row - 1;
            tempColum = colum - 1;
            if (tempRow >= 0 && tempColum >= 0) {//左上角
                if (nowArrays[tempRow][tempColum] == -2) {
                    result = true;
                    Message message = handler.obtainMessage();
                    message.what = SELECT;
                    message.arg1 = tempRow * MainActivity.COLUM + tempColum;
                    handler.sendMessage(message);
                }
            }

            tempRow = row - 1;
            tempColum = colum + 1;
            if (tempRow >= 0 && tempColum < MainActivity.COLUM) {//右上角
                if (nowArrays[tempRow][tempColum] == -2) {
                    result = true;
                    Message message = handler.obtainMessage();
                    message.what = SELECT;
                    message.arg1 = tempRow * MainActivity.COLUM + tempColum;
                    handler.sendMessage(message);
                }
            }

            tempRow = row + 1;
            tempColum = colum - 1;
            if (tempRow < MainActivity.ROW && tempColum >= 0) {//左下角
                if (nowArrays[tempRow][tempColum] == -2) {
                    result = true;
                    Message message = handler.obtainMessage();
                    message.what = SELECT;
                    message.arg1 = tempRow * MainActivity.COLUM + tempColum;
                    handler.sendMessage(message);
                }
            }

            tempRow = row + 1;
            tempColum = colum + 1;
            if (tempRow < MainActivity.ROW && tempColum < MainActivity.COLUM) {//右下角
                if (nowArrays[tempRow][tempColum] == -2) {
                    result = true;
                    Message message = handler.obtainMessage();
                    message.what = SELECT;
                    message.arg1 = tempRow * MainActivity.COLUM + tempColum;
                    handler.sendMessage(message);
                }
            }
        }

        return result;
    }

    private Random random = new Random();

    private void randomOne() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, "没有可以确定的，随机选择一个", Toast.LENGTH_SHORT).show();
            }
        });
        while (true) {
            int tempPosition = random.nextInt(MainActivity.COLUM * MainActivity.ROW);
            int tempRow = tempPosition / MainActivity.COLUM;
            int tempColum = tempPosition % MainActivity.COLUM;
            if (nowArrays[tempRow][tempColum] == -2) {
                Log.i("hx", "random : "+tempRow +" _ "+ tempColum);
                Message message = handler.obtainMessage();
                message.what = SELECT;
                message.arg1 = tempRow * MainActivity.COLUM + tempColum;
                handler.sendMessage(message);
                return;
            }
        }
    }
}
