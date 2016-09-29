package com.example.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import java.util.ArrayList;

public class BackgroundActivity extends Activity {
    private int id;
    private ViewPager viewPager;
    private ArrayList<View> pageViews;
    //包裹滑动图片
    private ViewGroup main;
    //包裹小圆点的LinearLayout
    private ViewGroup group;
    private ImageView imageView;
    private ImageView[] imageViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //设置无标题窗口
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //隐藏状态栏
        //定义全屏参数
        int flag= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //获得当前窗体对象
        Window window=BackgroundActivity.this.getWindow();
        //设置当前窗体为全屏显示
        window.setFlags(flag, flag);

        LayoutInflater inflater=getLayoutInflater();
        pageViews=new ArrayList<View>();
        pageViews.add(inflater.inflate(R.layout.item1, null));
        pageViews.add(inflater.inflate(R.layout.item2, null));
        pageViews.add(inflater.inflate(R.layout.item3, null));
//        pageViews.add(inflater.inflate(R.layout.item4, null));
        pageViews.add(inflater.inflate(R.layout.item5, null));
//        pageViews.add(inflater.inflate(R.layout.item6, null));
//        pageViews.add(inflater.inflate(R.layout.item7, null));
//        pageViews.add(inflater.inflate(R.layout.item8, null));
//        pageViews.add(inflater.inflate(R.layout.item09, null));
//        pageViews.add(inflater.inflate(R.layout.item10, null));
//        pageViews.add(inflater.inflate(R.layout.item11, null));
        main=(ViewGroup) inflater.inflate(R.layout.activity_background,null);
        setContentView(main);

        imageViews=new ImageView[pageViews.size()];
        group=(ViewGroup)main.findViewById(R.id.viewGroup);

        viewPager=(ViewPager)main.findViewById(R.id.guidePages);
        for(int i=0;i<pageViews.size();i++){
            imageView=new ImageView(BackgroundActivity.this);
            imageView.setLayoutParams(new LayoutParams(20, 20));
            imageView.setPadding(20, 0, 20, 0);
            imageViews[i]=imageView;
            if(i==0)
                //默认选中第一张图片
                imageViews[i].setBackgroundResource(R.drawable.back_circle_big);//page_indicator_focused
            else
                imageViews[i].setBackgroundResource(R.drawable.back_circle_small);//page_indicator
            group.addView(imageViews[i]);
        }

        viewPager.setAdapter(new GuidePageAdapter());
        viewPager.setOnPageChangeListener(new GuidePageChangeListener());
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.guide_view_demo, menu);
//        return true;
//    }

    //指引页面数据适配器
    class GuidePageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return pageViews.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0==arg1;
        }

        public int getItemPosition(Object object){
            return super.getItemPosition(object);
        }

        public void destroyItem(View arg0,int arg1,Object arg2){
            ((ViewPager)arg0).removeView(pageViews.get(arg1));
        }

        public Object instantiateItem(View arg0,int arg1){
            ((ViewPager)arg0).addView(pageViews.get(arg1));
            return pageViews.get(arg1);
        }

        public void restoreState(Parcelable arg0,ClassLoader arg1){

        }

        public Parcelable saveState(){
            return null;
        }

        public void startUpdate(View ag0){

        }

        public void finishUpdate(View arg0){

        }
    }

    //指引页面更改事件监听器
    class GuidePageChangeListener implements OnPageChangeListener{

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int arg0) {
            for(int i=0;i<imageViews.length;i++){
                imageViews[arg0].
                        setBackgroundResource(
                                R.drawable.back_circle_big);
                id=arg0;
                if(arg0!=i)
                    imageViews[i].
                            setBackgroundResource(
                                    R.drawable.back_circle_small);
            }

        }

    }

    @Override
    public void onBackPressed() {
        Log.d("onBack", id + "");
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
        super.onBackPressed();
    }
}
