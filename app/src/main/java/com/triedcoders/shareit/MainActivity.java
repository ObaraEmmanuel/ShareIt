package com.triedcoders.shareit;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

//import android.widget.SearchView;

public class MainActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener{
    TabLayout tabLayout;
    ViewPager viewPager;
    Toolbar toolbar;
    SearchView searchView;
    private TabAdapter adapter;
    FloatingActionButton floatingAction;
    private int prevTabIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        tabLayout = findViewById(R.id.mainTabLayout);
        viewPager = findViewById(R.id.mainViewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        adapter = new TabAdapter(this,getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(this);

        floatingAction = findViewById(R.id.floatingAction);
        floatingAction.bringToFront();
        setupFloatingActionButton(viewPager.getCurrentItem());
    }

    private void setupFloatingActionButton(int position) {
        prevTabIndex = position;
        BaseFragment fragment = (BaseFragment)adapter.getItem(position);

        if (fragment.useFloatingActionButton()) {
            // Button is now needed so show it
            int icon = fragment.getFloatingActionButtonIcon();
            int color = fragment.getFloatingActionButtonColor();

            floatingAction.setBackgroundColor(ContextCompat.getColor(this, color));
            floatingAction.setImageDrawable(ContextCompat.getDrawable(this, icon));
            floatingAction.show();
        }
    }

    private void updateFloatingActionButton(int oldPosition, int newPosition) {
        BaseFragment oldFragment = (BaseFragment)adapter.getItem(oldPosition);
        BaseFragment newFragment = (BaseFragment)adapter.getItem(newPosition);

        boolean oldShow = oldFragment.useFloatingActionButton();
        boolean newShow = newFragment.useFloatingActionButton();

        if (!oldShow && !newShow) {
            // Neither tab uses the button, do nothing
            return;
        }
        if (oldShow && !newShow) {
            // Button is no longer needed so hide it
            floatingAction.hide();
            return;
        }
        if (!oldShow && newShow) {
            // Button is now needed so show it
            int icon = newFragment.getFloatingActionButtonIcon();
            int color = newFragment.getFloatingActionButtonColor();

            floatingAction.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), color));
            floatingAction.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), icon));
            floatingAction.show();
            return;
        }

        // Both fragments use the button so do the transition
        final int icon = newFragment.getFloatingActionButtonIcon();
        final int color = newFragment.getFloatingActionButtonColor();

        floatingAction.clearAnimation();
        ScaleAnimation shrink = new ScaleAnimation(
                1.0f, 0.2f,
                1.0f, 0.2f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        shrink.setDuration(150);
        shrink.setInterpolator(new DecelerateInterpolator());
        shrink.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                floatingAction.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), color));
                floatingAction.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), icon));

                ScaleAnimation expand = new ScaleAnimation(
                        0.2f, 1.0f,
                        0.2f, 1.0f,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
                expand.setDuration(100);
                expand.setInterpolator(new AccelerateInterpolator());
                floatingAction.startAnimation(expand);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        floatingAction.startAnimation(shrink);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.app_bar_file_search);
        searchView = (SearchView) menuItem.getActionView();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch (item.getItemId()){
            case R.id.signOut:
                if(FirebaseAuth.getInstance().getCurrentUser() != null) {
                    FirebaseAuth.getInstance().signOut();
                }
                Intent loginScreen = new Intent(getApplicationContext(), LoginActivity.class);
                loginScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(loginScreen);
                return true;
            case R.id.sortPopular:
                return false;
            case R.id.sortExtension:
                return false;
            case R.id.sortFileName:
                return false;
            case R.id.sortFileSize:
                return false;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
    }

    BaseFragment getCurrentFragment(){
        return (BaseFragment)adapter.getItem(viewPager.getCurrentItem());
    }

    @Override
    public void onBackPressed(){
        if(!searchView.isIconified()){
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
        int oldPosition = prevTabIndex;
        int newPosition = tab.getPosition();

        if (oldPosition != newPosition) {
            updateFloatingActionButton(oldPosition, newPosition);
            prevTabIndex = newPosition;
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

}
