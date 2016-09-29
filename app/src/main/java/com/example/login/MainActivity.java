package com.example.login;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.bignerdranch.android.multiselector.SwappingHolder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private int mId;
    private int[] imageID;
    private RecyclerView mList;
    private SimpleAdapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Toolbar mToolbar;
    private TextView mNoReminderView;
    private com.getbase.floatingactionbutton.FloatingActionButton mAddReminderButton;
    private int mTempPost;
    private LinkedHashMap<Integer, Integer> IDmap = new LinkedHashMap<>();
    private ReminderDatabase rb;
    private MultiSelector mMultiSelector = new MultiSelector();
    private AlarmReceiver mAlarmReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化数据库
        rb = new ReminderDatabase(getApplicationContext());

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mAddReminderButton = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.add_reminder);
        mList = (RecyclerView) findViewById(R.id.reminder_list);
        mNoReminderView = (TextView) findViewById(R.id.no_reminder_text);


        //检查是否有已经存在的事项，没有的话显示mNoReminderView
        List<Reminder> mTest = rb.getAllReminders();

        if (mTest.isEmpty()) {
            mNoReminderView.setVisibility(View.VISIBLE);
        }

        //创建RecyclerView

        registerForContextMenu(mList);
        mAdapter = new SimpleAdapter();
        mAdapter.setItemCount(getDefaultItemCount());
        mList.setAdapter(mAdapter);

        mList.setLayoutManager(getLayoutManager());
        mList.setItemAnimator(new DefaultItemAnimator());

        // toolbar
        setSupportActionBar(mToolbar);
        mToolbar.setTitle(R.string.app_name);


        // 创建floating action button的监听器
        mAddReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ReminderAddActivity.class);
                startActivity(intent);
            }
        });

        //初始化alarm
        mAlarmReceiver = new AlarmReceiver();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //0则不执行拖动或者滑动
        ItemTouchHelper.Callback mCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP|ItemTouchHelper.DOWN,ItemTouchHelper.RIGHT) {
            /**
             * @param recyclerView
             * @param viewHolder 拖动的ViewHolder
             * @param target 目标位置的ViewHolder
             * @return
             */
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
//                int fromPosition = viewHolder.getAdapterPosition();//得到拖动ViewHolder的position
//                int toPosition = target.getAdapterPosition();//得到目标ViewHolder的position
//                if (fromPosition < toPosition) {
//                    //分别把中间所有的item的位置重新交换
//                    for (int i = fromPosition; i < toPosition; i++) {
//                        Collections.swap(new SimpleAdapter().getmItems(), i, i + 1);
//                    }
//                } else {
//                    for (int i = fromPosition; i > toPosition; i--) {
//                        Collections.swap(new SimpleAdapter().getmItems(), i, i - 1);
//                    }
//                }
//                mAdapter.notifyItemMoved(fromPosition, toPosition);
                //返回true表示执行拖动
                return false;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                //得到与recycler view item相关的事项ID
                int id = IDmap.get(position);
                //使用ID从数据库里拿到事项
                Reminder temp = rb.getReminder(id);
                System.out.println(temp);
                // 删除事项
                rb.deleteReminder(temp);
                // 从RecyclerView里删除事项
                mAdapter.removeItemSelected(position);
                // 删除事项alarm
                mAlarmReceiver.cancelAlarm(getApplicationContext(), id);

                //重新创建recyclerView里的项目
                mAdapter.onDeleteItem(getDefaultItemCount());

                //显示Toast
                Toast.makeText(getApplicationContext(),
                        "Deleted",
                        Toast.LENGTH_SHORT).show();

                //检查还有没有事项了，没有的话显示mNoReminderView
                List<Reminder> mTest = rb.getAllReminders();

                if (mTest.isEmpty()) {
                    mNoReminderView.setVisibility(View.VISIBLE);
                } else {
                    mNoReminderView.setVisibility(View.GONE);
                }
                Log.d("mc", "onSwiped");
                mAdapter.notifyItemRemoved(position);
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    //左右滑动时改变Item的透明度
                    final float alpha = 1 - Math.abs(dX) / (float)viewHolder.itemView.getWidth();
                    viewHolder.itemView.setAlpha(alpha);
                    viewHolder.itemView.setTranslationX(dX);
                }
            }
            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                super.onSelectedChanged(viewHolder, actionState);
                //当选中Item时候会调用该方法，重写此方法可以实现选中时候的一些动画逻辑
                Log.v("xx","onSelectedChanged");
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                //当动画已经结束的时候调用该方法，重写此方法可以实现恢复Item的初始状态
                Log.v("xx", "clearView");
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(mCallback);
        itemTouchHelper.attachToRecyclerView(mList);


    }

    //点击事项
    private void selectReminder(int mClickID) {
        String mStringClickID = Integer.toString(mClickID);

        //创建intent，并把id放进去
        Intent i = new Intent(this, ReminderEditActivity.class);
        i.putExtra(ReminderEditActivity.EXTRA_REMINDER_ID, mStringClickID);
        startActivityForResult(i, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mAdapter.setItemCount(getDefaultItemCount());
    }

    //重新创建RecyclerView，用于新建的事项即时显示出来
    @Override
    public void onResume() {
        super.onResume();

        //根据传来的id找到对应的imageID来设置背景图
        imageID = new int[]{
                R.drawable.wlbackground06_mobile,R.drawable.wlbackground05_mobile,R.drawable.wlbackground13_mobile, R.drawable.wlbackground23_mobile};
        RelativeLayout layout=(RelativeLayout) findViewById(R.id.content);
        Resources resources = this.getResources();
        Drawable btnDrawable = resources.getDrawable(imageID[mId]);
        layout.setBackgroundDrawable(btnDrawable);
        Log.d("onResume", imageID[mId] + "");


        //判断有没有提醒事项，没有的话把mNoReminderView设为VISABLE
        List<Reminder> mTest = rb.getAllReminders();

        if (mTest.isEmpty()) {
            mNoReminderView.setVisibility(View.VISIBLE);
        } else {
            mNoReminderView.setVisibility(View.GONE);
        }

        mAdapter.setItemCount(getDefaultItemCount());
    }

    //onResume里不能接收intent，所以使用这个
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mId=intent.getIntExtra("id",0);
        Log.d("onNewintent", mId + "");
        setIntent(intent);
    }
    //Back点击
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //RecyclerVIew的layoutmanager
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
    }

    protected int getDefaultItemCount() {
        return 100;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate菜单
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);//在菜单中找到对应控件的item
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        Log.d("Tag", "menu create");
        MenuItemCompat.setOnActionExpandListener(menuItem, new MenuItemCompat.OnActionExpandListener() {//设置打开关闭动作监听
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);

        //return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_about) {
            Toast.makeText(getApplicationContext(),"TODO Alpha Version\nCoded by MadCatz at Dec 18,2015",Toast.LENGTH_SHORT).show();
            return true;
        }
        if(id==R.id.action_logout) {
            Intent logoutIntent = new Intent(MainActivity.this,LoginActivity.class);
            logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(logoutIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        //处理导航栏的点击事件
        int id = item.getItemId();

        if (id == R.id.nav_inbox) {
            switchToInbox();
            Toast.makeText(getApplicationContext(),"Inbox",Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_today) {
            switchToToday();
            Toast.makeText(getApplicationContext(),"今天",Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_upcoming7) {
            Toast.makeText(getApplicationContext(),"未来7天",Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_item) {
            Toast.makeText(getApplicationContext(),"事项",Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_priority) {
            Toast.makeText(getApplicationContext(),"优先级",Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_Chengjiu) {
            Toast.makeText(getApplicationContext(),"成就",Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_BackG) {
            Intent intent = new Intent(this, BackgroundActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void switchToInbox() {
//        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, new Inbox()).commit();
        mToolbar.setTitle("Inbox");
    }
    private void switchToToday() {
//        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, new Today()).commit();
        mToolbar.setTitle("Today");
    }



    // RecyclerView的适配器
    public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.VerticalItemHolder> {
        private ArrayList<ReminderItem> mItems;
        private Context mContext;


        public ArrayList<ReminderItem> getmItems()
        {
            return mItems;
        }

        public SimpleAdapter( Context context){
            this.mContext = context;
        }

        public SimpleAdapter() {
            mItems = new ArrayList<>();
        }

        public void setItemCount(int count) {
            mItems.clear();
            mItems.addAll(generateData(count));
            notifyDataSetChanged();
        }

        public void onDeleteItem(int count) {
            mItems.clear();
            mItems.addAll(generateData(count));
        }

        public void removeItemSelected(int selected) {
            if (mItems.isEmpty()) return;
            mItems.remove(selected);
            notifyItemRemoved(selected);
        }

        // Adapter必须使用的ViewHolder
        @Override
        public VerticalItemHolder onCreateViewHolder(ViewGroup container, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(container.getContext());
            View root = inflater.inflate(R.layout.recycle_item, container, false);

            return new VerticalItemHolder(root, this);
        }

        @Override
        public void onBindViewHolder(VerticalItemHolder itemHolder, int position) {
            ReminderItem item = mItems.get(position);
            itemHolder.setReminderTitle(item.mTitle);
            itemHolder.setReminderDateTime(item.mDateTime);
            itemHolder.setReminderRepeatInfo(item.mRepeat, item.mRepeatNo, item.mRepeatType);
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        public  class ReminderItem {
            public String mTitle;
            public String mDateTime;
            public String mRepeat;
            public String mRepeatNo;
            public String mRepeatType;
            public String mActive;

            public ReminderItem(String Title, String DateTime, String Repeat, String RepeatNo, String RepeatType, String Active) {
                this.mTitle = Title;
                this.mDateTime = DateTime;
                this.mRepeat = Repeat;
                this.mRepeatNo = RepeatNo;
                this.mRepeatType = RepeatType;
                this.mActive = Active;
            }
        }
        //比较时间的类，事件按升序排列
        public class DateTimeComparator implements Comparator {
            DateFormat f = new SimpleDateFormat("dd/mm/yyyy hh:mm");

            public int compare(Object a, Object b) {
                String o1 = ((DateTimeSorter)a).getDateTime();
                String o2 = ((DateTimeSorter)b).getDateTime();

                try {
                    return f.parse(o1).compareTo(f.parse(o2));
                } catch (ParseException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        }

        //为RecyclerView的每个item处理UI和数据
        public  class VerticalItemHolder extends SwappingHolder
                implements View.OnClickListener{
            private TextView mTitleText, mDateAndTimeText, mRepeatInfoText;
            private ImageView mThumbnailImage;
            private ColorGenerator mColorGenerator = ColorGenerator.DEFAULT;
            private TextDrawable mDrawableBuilder;
            private SimpleAdapter mAdapter;

            public VerticalItemHolder(View itemView, SimpleAdapter adapter) {
                super(itemView, mMultiSelector);
                itemView.setOnClickListener(this);

                // 为item初始化适配器
                mAdapter = adapter;

                // 初始化view
                mTitleText = (TextView) itemView.findViewById(R.id.recycle_title);
                mDateAndTimeText = (TextView) itemView.findViewById(R.id.recycle_date_time);
                mRepeatInfoText = (TextView) itemView.findViewById(R.id.recycle_repeat_info);
                mThumbnailImage = (ImageView) itemView.findViewById(R.id.thumbnail_image);
            }

            // 单一事项的点击
            @Override
            public void onClick(View v) {
                if (!mMultiSelector.tapSelection(this)) {
                    mTempPost = mList.getChildAdapterPosition(v);

                    int mReminderClickID = IDmap.get(mTempPost);
                    selectReminder(mReminderClickID);

                } else if(mMultiSelector.getSelectedPositions().isEmpty()){
                    mAdapter.setItemCount(getDefaultItemCount());
                }
            }

            //设置item的标题
            public void setReminderTitle(String title) {
                mTitleText.setText(title);
                String letter = "A";

                if(title != null && !title.isEmpty()) {
                    letter = title.substring(0, 1);
                }

                int color = mColorGenerator.getRandomColor();
                //用随机的颜色及title的首字母创建图片
                mDrawableBuilder = TextDrawable.builder()
                        .buildRound(letter, color);
                mThumbnailImage.setImageDrawable(mDrawableBuilder);
            }

            // 设置item时间
            public void setReminderDateTime(String datetime) {
                mDateAndTimeText.setText(datetime);
            }

            // 设置item重复
            public void setReminderRepeatInfo(String repeat, String repeatNo, String repeatType) {
                if(repeat.equals("true")){
                    mRepeatInfoText.setText("Every " + repeatNo + " " + repeatType + "(s)");
                }else if (repeat.equals("false")) {
                    mRepeatInfoText.setText("Repeat Off");
                }
            }

        }

        //为每个item生成数据
        public List<ReminderItem> generateData(int count) {
            ArrayList<SimpleAdapter.ReminderItem> items = new ArrayList<>();

            //从数据库里拿到数据
            List<Reminder> reminders = rb.getAllReminders();

            // 初始化List
            List<String> Titles = new ArrayList<>();
            List<String> Repeats = new ArrayList<>();
            List<String> RepeatNos = new ArrayList<>();
            List<String> RepeatTypes = new ArrayList<>();
            List<String> Actives = new ArrayList<>();
            List<String> DateAndTime = new ArrayList<>();
            List<Integer> IDList= new ArrayList<>();
            List<DateTimeSorter> DateTimeSortList = new ArrayList<>();

            //在事项的list中分别添加detail
            for (Reminder r : reminders) {
                Titles.add(r.getTitle());
                DateAndTime.add(r.getDate() + " " + r.getTime());
                Repeats.add(r.getRepeat());
                RepeatNos.add(r.getRepeatNo());
                RepeatTypes.add(r.getRepeatType());
                Actives.add(r.getActive());
                IDList.add(r.getID());
            }

            int key = 0;
            //Datetimesorter
            for(int k = 0; k<Titles.size(); k++){
                DateTimeSortList.add(new DateTimeSorter(key, DateAndTime.get(k)));
                key++;
            }

            // 根据时间升序排列
            Collections.sort(DateTimeSortList, new DateTimeComparator());

            int k = 0;

            //为RecyclerView里每个item添加数据
            for (DateTimeSorter item:DateTimeSortList) {
                int i = item.getIndex();

                items.add(new SimpleAdapter.ReminderItem(Titles.get(i), DateAndTime.get(i), Repeats.get(i),
                        RepeatNos.get(i), RepeatTypes.get(i), Actives.get(i)));
                IDmap.put(k, IDList.get(i));
                k++;
            }
            return items;
        }
    }
}
