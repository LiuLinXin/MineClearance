package com.hx.mineclearance;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sober_philer on 2017/5/28.
 */

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.InnerHolder> implements View.OnClickListener {

    private TextView tvLastMine;
    private ImageView mark;
    private List<Integer> markedMine = new ArrayList<>();
    private int[][] trueArrays = new int[MainActivity.ROW][MainActivity.COLUM];//-1表示雷， 0 - 8表示周围的雷数量
    private boolean marked;//是否是标记模式
    private boolean success;//是否已经成功
    private List<SuccessInterface> successes = new ArrayList<>();

    //-1表示为雷, -2表示没任何操作，-3表示标记为地雷,0-8表示周围的雷
    private int[][] nowArrays = new int[MainActivity.ROW][MainActivity.COLUM];
    private Activity activity;

    public ContentAdapter(int[][] trueArrays, int[][] nowArrays, Activity activity, TextView tvLastMine, ImageView mark) {
        this.trueArrays = trueArrays;
        this.nowArrays = nowArrays;
        this.activity = activity;
        this.tvLastMine = tvLastMine;
        this.mark = mark;
    }

    public void addSuccessListener(SuccessInterface successInterface){
        successes.add(successInterface);
    }

    public void setMarked() {
        marked = !marked;
        if(marked){
            mark.setImageResource(R.mipmap.ic_memi);
        }else {
            mark.setImageResource(R.mipmap.ic_normal);
        }
    }

    @Override
    public InnerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(activity).inflate(R.layout.item, parent, false);
        return new InnerHolder(inflate);
    }

    @Override
    public void onBindViewHolder(InnerHolder holder, int position) {
        holder.ivItem.setTag(position);
        holder.ivItem.setOnClickListener(this);

        int tempRow = position / MainActivity.COLUM;
        int tempColum = position % MainActivity.COLUM;
        int num = nowArrays[tempRow][tempColum];
        if (num == -1) {
            holder.ivItem.setBackgroundColor(Color.BLACK);
        } else if (0 <= num) {
            if (num != 0) {
                holder.ivItem.setText(num + "");
            } else {
                holder.ivItem.setText("");
            }
            holder.ivItem.setBackgroundColor(Color.argb(255, 0, 255, 0));
        } else if (num == -3) {
            holder.ivItem.setBackgroundColor(Color.argb(255, 0, 0, 255));
            holder.ivItem.setText("×");
            return;
        } else {
//            holder.ivItem.setBackgroundColor(Color.argb(255, 255, 0, 255));
            holder.ivItem.setText("");
            holder.ivItem.setBackgroundResource(R.drawable.xixicolor);
        }
    }

    @Override
    public int getItemCount() {
        return MainActivity.ROW * MainActivity.COLUM;
    }

    public void myNotifyDataSetChanged() {
        tvLastMine.setText(MainActivity.MINE_NUM - markedMine.size() + "");
        notifyDataSetChanged();
    }

    public void init() {
        markedMine.clear();
        marked = false;
        success = false;
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        select(position);
    }

    public void select(int position){
        int tempRow = position / MainActivity.COLUM;
        int tempColum = position % MainActivity.COLUM;
        if (nowArrays[tempRow][tempColum] >= -1 || success) {
            return;
        }
        if (marked) {
            setMarked();
            if (markedMine.contains(position)) {
                markedMine.remove((Integer) position);
                nowArrays[tempRow][tempColum] = -2;
            } else {
                markedMine.add(position);
                nowArrays[tempRow][tempColum] = -3;
            }
            myNotifyDataSetChanged();
            return;
        }
        if (nowArrays[tempRow][tempColum] == -3) {
            return;
        }

        nowArrays[tempRow][tempColum] = trueArrays[tempRow][tempColum];
        if (nowArrays[tempRow][tempColum] == -1) {
            for(SuccessInterface successInterface : successes){
                successInterface.fail();
            }
            for (int i = 0; i < MainActivity.ROW; i++) {
                for (int j = 0; j < MainActivity.COLUM; j++) {
                    nowArrays[i][j] = trueArrays[i][j];
                }
            }
        } else if (nowArrays[tempRow][tempColum] == 0) {
            aroudNowMine(tempRow, tempColum);
        }
        if (checkSuccess()) {
            success = true;
            Toast.makeText(activity, "success", Toast.LENGTH_LONG).show();
            for(SuccessInterface successInterface : successes){
                successInterface.success();
            }
        }
        myNotifyDataSetChanged();
    }

    public boolean checkSuccess() {
        int lastNum = 0;
        for (int i = 0; i < MainActivity.ROW; i++) {
            for (int j = 0; j < MainActivity.COLUM; j++) {
                int tempNum = nowArrays[i][j];
                if (tempNum == -2 || tempNum == -3) {
                    lastNum++;
                }
            }
        }
        Log.i("hx", "last Num : " + lastNum);
        return lastNum == MainActivity.MINE_NUM;
    }

    private void aroudNowMine(int row, int colum) {
        int tempRow, tempColum;

        tempRow = row - 1;
        tempColum = colum;
        if (tempRow >= 0) {//上方
            if (nowArrays[tempRow][tempColum] == -2 || nowArrays[tempRow][tempColum] == -3) {
                nowArrays[tempRow][tempColum] = trueArrays[tempRow][tempColum];
                if (trueArrays[tempRow][tempColum] == 0) {
                    aroudNowMine(tempRow, tempColum);
                }
            }
        }

        tempRow = row;
        tempColum = colum - 1;
        if (tempColum >= 0) {//左方
            if (nowArrays[tempRow][tempColum] == -2 || nowArrays[tempRow][tempColum] == -3) {
                nowArrays[tempRow][tempColum] = trueArrays[tempRow][tempColum];
                if (trueArrays[tempRow][tempColum] == 0) {
                    aroudNowMine(tempRow, tempColum);
                }
            }
        }

        tempRow = row;
        tempColum = colum + 1;
        if (tempColum < MainActivity.COLUM) {//右方
            if (nowArrays[tempRow][tempColum] == -2 || nowArrays[tempRow][tempColum] == -3) {
                nowArrays[tempRow][tempColum] = trueArrays[tempRow][tempColum];
                if (trueArrays[tempRow][tempColum] == 0) {
                    aroudNowMine(tempRow, tempColum);
                }
            }
        }

        tempRow = row + 1;
        tempColum = colum;
        if (tempRow < MainActivity.ROW) {//下方
            if (nowArrays[tempRow][tempColum] == -2 || nowArrays[tempRow][tempColum] == -3) {
                nowArrays[tempRow][tempColum] = trueArrays[tempRow][tempColum];
                if (trueArrays[tempRow][tempColum] == 0) {
                    aroudNowMine(tempRow, tempColum);
                }
            }
        }

        tempRow = row - 1;
        tempColum = colum - 1;
        if (tempRow >= 0 && tempColum >= 0) {//左上角
            if (nowArrays[tempRow][tempColum] == -2 || nowArrays[tempRow][tempColum] == -3) {
                nowArrays[tempRow][tempColum] = trueArrays[tempRow][tempColum];
                if (trueArrays[tempRow][tempColum] == 0) {
                    aroudNowMine(tempRow, tempColum);
                }
            }
        }

        tempRow = row - 1;
        tempColum = colum + 1;
        if (tempRow >= 0 && tempColum < MainActivity.COLUM) {//右上角
            if (nowArrays[tempRow][tempColum] == -2 || nowArrays[tempRow][tempColum] == -3) {
                nowArrays[tempRow][tempColum] = trueArrays[tempRow][tempColum];
                if (trueArrays[tempRow][tempColum] == 0) {
                    aroudNowMine(tempRow, tempColum);
                }
            }
        }

        tempRow = row + 1;
        tempColum = colum - 1;
        if (tempRow < MainActivity.ROW && tempColum >= 0) {//左下角
            if (nowArrays[tempRow][tempColum] == -2 || nowArrays[tempRow][tempColum] == -3) {
                nowArrays[tempRow][tempColum] = trueArrays[tempRow][tempColum];
                if (trueArrays[tempRow][tempColum] == 0) {
                    aroudNowMine(tempRow, tempColum);
                }
            }
        }

        tempRow = row + 1;
        tempColum = colum + 1;
        if (tempRow < MainActivity.ROW && tempColum < MainActivity.COLUM) {//右下角
            if (nowArrays[tempRow][tempColum] == -2 || nowArrays[tempRow][tempColum] == -3) {
                nowArrays[tempRow][tempColum] = trueArrays[tempRow][tempColum];
                if (trueArrays[tempRow][tempColum] == 0) {
                    aroudNowMine(tempRow, tempColum);
                }
            }
        }

    }

    class InnerHolder extends RecyclerView.ViewHolder {
        private TextView ivItem;

        public InnerHolder(View itemView) {
            super(itemView);
            ivItem = (TextView) itemView.findViewById(R.id.ivItem);
        }
    }

    public static interface SuccessInterface{
        void success();
        void fail();
    }
}
