package com.aistream.greenqube.customs;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aistream.greenqube.R;


/**
 * custom keyboard
 */

public class KeyBoardLayout extends LinearLayout implements View.OnClickListener {
    TextView keyboard_tv_one, keyboard_tv_two, keyboard_tv_three, keyboard_tv_four, keyboard_tv_five,
            keyboard_tv_six, keyboard_tv_seven, keyboard_tv_eight, keyboard_tv_nine, keyboard_tv_clear,
            keyboard_tv_zero, keyboard_tv_enter;
    private OnNumberClickListener onNumberClickListener;
    private LinearLayout layout;
    public OnNumberClickListener getOnNumberClickListener() {
        return onNumberClickListener;
    }

    public void setOnNumberClickListener(OnNumberClickListener onNumberClickListener) {
        this.onNumberClickListener = onNumberClickListener;
    }

    public KeyBoardLayout(Context context) {
        super(context);
        init();
    }

    public KeyBoardLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public KeyBoardLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public KeyBoardLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        inflate(getContext(), R.layout.view_keyboard, this);
        keyboard_tv_one = (TextView) findViewById(R.id.tv_keyboard_1);
        keyboard_tv_two = (TextView) findViewById(R.id.tv_keyboard_2);
        keyboard_tv_three = (TextView) findViewById(R.id.tv_keyboard_3);
        keyboard_tv_four = (TextView) findViewById(R.id.tv_keyboard_4);
        keyboard_tv_five = (TextView) findViewById(R.id.tv_keyboard_5);
        keyboard_tv_six = (TextView) findViewById(R.id.tv_keyboard_6);
        keyboard_tv_seven = (TextView) findViewById(R.id.tv_keyboard_7);
        keyboard_tv_eight = (TextView) findViewById(R.id.tv_keyboard_8);
        keyboard_tv_nine = (TextView) findViewById(R.id.tv_keyboard_9);
        keyboard_tv_clear = (TextView) findViewById(R.id.tv_keyboard_clear);
        keyboard_tv_zero = (TextView) findViewById(R.id.tv_keyboard_0);
        keyboard_tv_enter = (TextView) findViewById(R.id.tv_keyboard_enter);
        layout = (LinearLayout) findViewById(R.id.background);

        keyboard_tv_one.setOnClickListener(this);
        keyboard_tv_two.setOnClickListener(this);
        keyboard_tv_three.setOnClickListener(this);
        keyboard_tv_four.setOnClickListener(this);
        keyboard_tv_five.setOnClickListener(this);
        keyboard_tv_six.setOnClickListener(this);
        keyboard_tv_seven.setOnClickListener(this);
        keyboard_tv_eight.setOnClickListener(this);
        keyboard_tv_nine.setOnClickListener(this);
        keyboard_tv_clear.setOnClickListener(this);
        keyboard_tv_zero.setOnClickListener(this);
        keyboard_tv_enter.setOnClickListener(this);
    }

    public  void  setBackground(int color){
       if (layout != null){
           layout.setBackgroundResource(color);
       }
    }

    public void setBtnBackground(int color){
        keyboard_tv_one.setBackgroundResource(color);
        keyboard_tv_two.setBackgroundResource(color);
        keyboard_tv_three.setBackgroundResource(color);
        keyboard_tv_four.setBackgroundResource(color);
        keyboard_tv_five.setBackgroundResource(color);
        keyboard_tv_six.setBackgroundResource(color);
        keyboard_tv_seven.setBackgroundResource(color);
        keyboard_tv_eight.setBackgroundResource(color);
        keyboard_tv_nine.setBackgroundResource(color);
        keyboard_tv_clear.setBackgroundResource(color);
        keyboard_tv_zero.setBackgroundResource(color);
        keyboard_tv_enter.setBackgroundResource(color);
    }

    public void setTxtColor(int color){
        keyboard_tv_one.setTextColor(color);
        keyboard_tv_two.setTextColor(color);
        keyboard_tv_three.setTextColor(color);
        keyboard_tv_four.setTextColor(color);
        keyboard_tv_five.setTextColor(color);
        keyboard_tv_six.setTextColor(color);
        keyboard_tv_seven.setTextColor(color);
        keyboard_tv_eight.setTextColor(color);
        keyboard_tv_nine.setTextColor(color);
        keyboard_tv_clear.setTextColor(color);
        keyboard_tv_zero.setTextColor(color);
        keyboard_tv_enter.setTextColor(color);
    }

    @Override
     public void onClick(View v) {
        int number = -1;
        switch (v.getId()){
            case  R.id.tv_keyboard_1:
                number = 1;
                break;
            case R.id.tv_keyboard_2:
                number = 2;
                break;
            case R.id.tv_keyboard_3:
                number = 3;
                break;
            case R.id.tv_keyboard_4:
                number = 4;
                break;
            case R.id.tv_keyboard_5:
                number = 5;
                break;
            case R.id.tv_keyboard_6:
                number = 6;
                break;
            case R.id.tv_keyboard_7:
                number = 7;
                break;
            case R.id.tv_keyboard_8:
                number = 8;
                break;
            case R.id.tv_keyboard_9:
                number = 9;
                break;
            case R.id.tv_keyboard_clear:
                number = 67;
                break;
            case R.id.tv_keyboard_0:
                number = 0;
                break;
            case R.id.tv_keyboard_enter:
                number = 66;
                break;
        }

        if (onNumberClickListener != null && number != -1) {
            String numStr = String.valueOf(number);
            if (number == 66) {
                onNumberClickListener.onNumberEnter();
            } else if (number == 67) {
                onNumberClickListener.onNumberDelete();
            } else {
                onNumberClickListener.onNumberReturn(String.valueOf(number));
            }
        }
    }

    public interface OnNumberClickListener {
        //number key call
        public void onNumberReturn(String number);

        //clear key call
        public void onNumberDelete();

        //enter key call
        public void onNumberEnter();
    }
}
