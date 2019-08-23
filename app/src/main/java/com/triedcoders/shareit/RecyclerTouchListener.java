package com.triedcoders.shareit;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

    private GestureDetector mGestureDetetctor;
    private ClickListener mClickListener;

    public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener){
        this.mClickListener = clickListener;
        mGestureDetetctor = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapUp(MotionEvent e){
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e){
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if(child != null && clickListener != null){
                    clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rView, MotionEvent e){

        View child = rView.findChildViewUnder(e.getX(), e.getY());
        if(child != null && mClickListener != null && mGestureDetetctor.onTouchEvent(e)){
            mClickListener.onClick(child, rView.getChildAdapterPosition(child));
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rView, MotionEvent e){

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept){

    }

    public interface ClickListener{
        void onClick(View view, int position);
        void onLongClick(View view, int position);
    }
}
