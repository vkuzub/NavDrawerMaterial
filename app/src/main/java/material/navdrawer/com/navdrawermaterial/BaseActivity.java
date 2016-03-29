package material.navdrawer.com.navdrawermaterial;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

public class BaseActivity extends AppCompatActivity {

    private static final int NAVDRAWER_LAUNCH_DELAY = 250;
    private static final int MAIN_CONTENT_FADEOUT_DURATION = 150;

    public static final int UNKNOWN_ACTIVITY = -1;
    public static final int MAIN_ACTIVITY = 0;
    public static final int SETTINGS_ACTIVITY = 1;
    public static final int ABOUT_ACTIVITY = 2;

    private Drawer mDrawer;
    private Toolbar mToolbar;
    private Handler mHandler;

    private PrimaryDrawerItem mainDrawerItem;
    private PrimaryDrawerItem settingsDrawerItem;
    private PrimaryDrawerItem aboutDrawerItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
    }

    private void initToolBar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        initToolBar();
        initializeDrawer();
    }

    protected int getSelfNavDrawerItem() {
        return UNKNOWN_ACTIVITY;
    }

    private void initializeDrawer() {

        initializeDrawerItems();

        mDrawer = new DrawerBuilder(this).
                withRootView(R.id.drawer_layout).
                withToolbar(mToolbar).
                withActionBarDrawerToggleAnimated(true).
                withDisplayBelowStatusBar(false).
                addDrawerItems(
                        mainDrawerItem,
                        settingsDrawerItem,
                        aboutDrawerItem
//                                .withSelectable(true)
                ).
                withOnDrawerItemClickListener(OnDrawerItemClickListenerImpl).
                build();

//        mDrawer.getActionBarDrawerToggle().setDrawerIndicatorEnabled(false);

        mDrawer.setSelection(getSelfNavDrawerItem(), false);
        // Should be done separately from mDrawer building to avoid NullPointerException
    }

    Drawer.OnDrawerItemClickListener OnDrawerItemClickListenerImpl = new Drawer.OnDrawerItemClickListener() {
        @Override
        public boolean onItemClick(View view, final int position, IDrawerItem drawerItem) {
            if (position == getSelfNavDrawerItem()) {
                mDrawer.closeDrawer();
                return true;
            }
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    goToNavDrawerItem(position);
                }
            }, NAVDRAWER_LAUNCH_DELAY);

            View mainContent = findViewById(R.id.main_content);
            if (mainContent != null) {
                mainContent.animate().alpha(0).setDuration(MAIN_CONTENT_FADEOUT_DURATION);
            }

//            goToNavDrawerItem(position);

            //todo gotonavdrawer id
//            goToNavDrawerItem(getSelfNavDrawerItem());
            //todo setSelection?
//            drawerItem.withSetSelected(true);
            mDrawer.closeDrawer();
            return true;
        }
    };

    private void initializeDrawerItems() {
        mainDrawerItem =
                new PrimaryDrawerItem().
                        withName("Main").
                        withIdentifier(MAIN_ACTIVITY);
        settingsDrawerItem =
                new PrimaryDrawerItem().
                        withName("Settings").
                        withIdentifier(SETTINGS_ACTIVITY);
        aboutDrawerItem =
                new PrimaryDrawerItem().
                        withName("About").
                        withIdentifier(ABOUT_ACTIVITY);

    }

    private void goToNavDrawerItem(int item) {
        System.out.println(item);

        switch (item) {
            case MAIN_ACTIVITY:
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
            case SETTINGS_ACTIVITY:
                createBackStack(new Intent(this, SettingsActivity.class));
                break;
            case ABOUT_ACTIVITY:
                createBackStack(new Intent(this, AboutActivity.class));
                break;
        }

    }

    private void createBackStack(Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            TaskStackBuilder builder = TaskStackBuilder.create(this);
            builder.addNextIntentWithParentStack(intent);
            builder.startActivities();
        } else {
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawer != null && mDrawer.isDrawerOpen()) {
            mDrawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }
}
