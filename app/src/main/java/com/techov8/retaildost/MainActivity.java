package com.techov8.retaildost;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import static com.techov8.retaildost.ItemListActivity.badgeCount;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private  int currentFragment=-1 ;
    private static final int HOME_FRAGMENT = 0;
    private static final int CART_FRAGMENT = 1;
    private static final int ORDERS_FRAGMENT =2;
    private static final int CONTACT_US =3;
    private static final int ACCOUNT_FRAGMENT=4;
    private FrameLayout frameLayout;
    public static Boolean showCart = false;
    public static Activity mainActivity;

    private Window window;
    private  Toolbar toolbar;
    public static  DrawerLayout drawer;
    private Button searchBarBtn;
    private  NavigationView navigationView;
    public static boolean noConnection=true;
    private TextView fullName;
    private ImageView mainImage;
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);
        frameLayout = findViewById(R.id.main_frameLayout);
        fullName =  navigationView.getHeaderView(0).findViewById(R.id.main_full_name);
        mainImage=findViewById(R.id.main_image);
        searchBarBtn=findViewById(R.id.home_search_bar);

        window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        try {
            FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DBqueries.userName = task.getResult().getString("full_name");
                                fullName.setText(DBqueries.userName);
                                DBqueries.AccountID = task.getResult().getString("account_ID");
                                DBqueries.no_of_orders = task.getResult().getLong("no_of_orders");
                            } else {
                                String error = Objects.requireNonNull(task.getException()).getMessage();
                                Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            searchBarBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent searchIntent = new Intent(MainActivity.this, SearchActivity.class);
                    startActivity(searchIntent);
                }
            });


            if (showCart) {
                mainActivity = MainActivity.this;
                drawer.setDrawerLockMode(1);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                goToFragment("My Cart", new MyCartFragment(), -2);
            } else {
                ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, 0, 0);
                drawer.addDrawerListener(toggle);
                toggle.syncState();
                setFragment(new HomeFragment(), HOME_FRAGMENT);

            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(currentFragment == HOME_FRAGMENT) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
            getMenuInflater().inflate(R.menu.main, menu);

            MenuItem cartItem = menu.findItem(R.id.main_cart_icon);
            cartItem.setActionView(R.layout.badge_layout);
            ImageView badgeIcon = cartItem.getActionView().findViewById(R.id.badge_icon);
            badgeIcon.setImageResource(R.mipmap.cart);
            badgeCount = cartItem.getActionView().findViewById(R.id.badge_count);

                if (DBqueries.cartList.size() == 0) {
                    badgeCount.setVisibility(View.INVISIBLE);
                    DBqueries.loadCartList(MainActivity.this,  new Dialog(MainActivity.this),false, badgeCount);
                } else {
                    badgeCount.setVisibility(View.VISIBLE);
                    if (DBqueries.cartList.size() < 99) {
                        badgeCount.setText(String.valueOf(DBqueries.cartList.size()));
                    } else {
                        badgeCount.setText("99");
                    }
                }

            cartItem.getActionView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
            goToFragment("My Cart", new MyCartFragment(), CART_FRAGMENT);

                }
            });
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(noConnection) {
            int id = item.getItemId();
            if (id == R.id.main_cart_icon) {
                navigationView.getMenu().getItem(2).setChecked(true);
                goToFragment("My Cart", new MyCartFragment(), CART_FRAGMENT);
                return true;
            } else if(id==R.id.main_call){
                Uri u = Uri.parse("tel:9304566832");
                Intent i = new Intent(Intent.ACTION_DIAL, u);

                try
                {
                    // Launch the Phone app's dialer with a phone
                    // number to dial a call.
                    startActivity(i);
                }
                catch (SecurityException s)
                {
                    // show() method display the toast with
                    // exception message.
                    Toast.makeText(MainActivity.this, "An error occurred", Toast.LENGTH_LONG)
                            .show();
                }
            }

            else if(id==android.R.id.home){
                if(showCart){
                    mainActivity = null;
                    showCart = false;
                    finish();
                    return  true;
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }
    MenuItem menuItem;
    public  boolean onNavigationItemSelected(MenuItem item){
        final DrawerLayout drawer= findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        menuItem = item;

        drawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

                int id = menuItem.getItemId();
                if (id == R.id.nav_my_home) {
                    invalidateOptionsMenu();
                    setFragment(new HomeFragment(), HOME_FRAGMENT);
                    navigationView.getMenu().getItem(0).setChecked(true);
                } else if (id == R.id.nav_my_orders) {
                    goToFragment("My Orders", new MyOrderFragment(),ORDERS_FRAGMENT);

                }else if(id==R.id.nav_my_cart){
                    goToFragment("My Cart",new MyCartFragment(),CART_FRAGMENT);
                }
                else if (id == R.id.nav_contact_us) {
                    goToFragment("Contact Us", new ContactUsFragment(), CONTACT_US);
                }
                else if (id == R.id.nav_my_account) {
                    goToFragment("My Account", new MyAccountFragment(), ACCOUNT_FRAGMENT);
                } else if (id == R.id.nav_privacy_policy) {
                    
                    Intent viewIntent=new Intent("android.intent.action.VIEW", Uri.parse("http://www.retaildost.in/privacy_policy/"));
                    startActivity(viewIntent);
                } else if (id == R.id.nav_faqs) {
                    Intent viewIntent=new Intent("android.intent.action.VIEW", Uri.parse("http://www.retaildost.in/faqs/"));
                    startActivity(viewIntent);
                } else if (id == R.id.nav_rate_us) {
                    Intent viewIntent=new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=com.techov8.retaildost"));
                    startActivity(viewIntent);
                } else if (id == R.id.nav_sign_out) {

                    FirebaseFirestore.getInstance().collection("USER_IDS").document(DBqueries.AccountID).update("is_logged_in",false)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    SharedPreferences prefs = getSharedPreferences("LoginData", 0);
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putString("data", "no");
                                    editor.apply();

                                    FirebaseAuth.getInstance().signOut();
                                    DBqueries.clearData();
                                    Intent registerIntent = new Intent(MainActivity.this,RegisterActivity.class);
                                    startActivity(registerIntent);
                                    finish();
                                }
                            });

                }
                drawer.removeDrawerListener(this);
            }
        });

        return true;


    }

    private void goToFragment(String title, Fragment fragment, int fragmentNo) {
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(title);
        invalidateOptionsMenu();
        setFragment(fragment,fragmentNo);
        if(fragmentNo == CART_FRAGMENT || showCart) {
            navigationView.getMenu().getItem(2).setChecked(true);
        }
    }

    private void setFragment(Fragment fragment, int fragmentNo) {
        if(fragmentNo != currentFragment) {
            window.setStatusBarColor(Color.parseColor("#1BA9C3"));
            toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
            if(fragmentNo==HOME_FRAGMENT){
                searchBarBtn.setVisibility(View.VISIBLE);
                mainImage.setVisibility(View.VISIBLE);
            }else{
                searchBarBtn.setVisibility(View.GONE);
                mainImage.setVisibility(View.GONE);
            }
            currentFragment = fragmentNo;
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slideout_from_left);
            fragmentTransaction.replace(frameLayout.getId(), fragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onBackPressed(){
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else{
            if(currentFragment == HOME_FRAGMENT){
                currentFragment = -1;
                super.onBackPressed();
            }else{
                if(showCart){
                    mainActivity = null;
                    showCart = false;
                    finish();
                }
                else{

                    invalidateOptionsMenu();
                    setFragment(new HomeFragment(), HOME_FRAGMENT);
                    navigationView.getMenu().getItem(0).setChecked(true);
                }

            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        FirebaseFirestore.getInstance().collection("USER_IDS").document(DBqueries.AccountID).update("is_logged_in",false);

    }
}