package freaktemplate.store3;

/**
 * Created by Ravi on 29/07/15.
 */

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import freaktemplate.utils.RoundedImageView;

import static android.content.Context.MODE_PRIVATE;


public class FragmentDrawer extends Fragment {

    private static String TAG = FragmentDrawer.class.getSimpleName();
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private View containerView;
    private FragmentDrawerListener drawerListener;
    private RoundedImageView img_profile;
    private TextView txt_nameuser;
    private TextView txt_profile;
    private RelativeLayout ll_profile;

    private static final String MY_PREFS_NAME = "Store";

    public FragmentDrawer() {

    }

    public void setDrawerListener(FragmentDrawerListener listener) {
        this.drawerListener = listener;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflating view layout
        final View layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        txt_nameuser = layout.findViewById(R.id.txt_nameuser);
        img_profile = layout.findViewById(R.id.img_profile);
        txt_profile = layout.findViewById(R.id.txt_profile);
        ll_profile = layout.findViewById(R.id.ll_profile);
        //getting user info if available

        SharedPreferences prefs = layout.getContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String picturePath = prefs.getString("picturepath", null);
        final String userLoginId = prefs.getString("score", null);
        String Full_name = prefs.getString("fullname", null);
        final String user_name = prefs.getString("username", null);
        String Email = prefs.getString("emilid", null);
        Log.e("picturePath",picturePath+"no");
        if (userLoginId == null || userLoginId.equals("delete")) {
            txt_nameuser.setText("Sign In");
            txt_profile.setText("Profile");
        } else {
            txt_nameuser.setText(user_name);
            txt_profile.setText("Profile");
            Picasso.get().load(picturePath).into(img_profile);
        }
        ll_profile = layout.findViewById(R.id.ll_profile);
        ll_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
        drawerListener.onDrawerItemSelected(view, 8);
        mDrawerLayout.closeDrawer(containerView);
//      if (userLoginId == null || userLoginId.equals("delete")) {
//          drawerListener.onDrawerItemSelected(view, 8);
//          mDrawerLayout.closeDrawer(containerView);
//      } else {
//
//      }
            }
        });

        LinearLayout ll_home = layout.findViewById(R.id.ll_home);
        ll_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerListener.onDrawerItemSelected(view, 0);
                mDrawerLayout.closeDrawer(containerView);
            }
        });

        LinearLayout ll_cat = layout.findViewById(R.id.ll_cat);
        ll_cat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerListener.onDrawerItemSelected(view, 1);
                mDrawerLayout.closeDrawer(containerView);
            }
        });

        LinearLayout ll_fav = layout.findViewById(R.id.ll_fav);
        ll_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerListener.onDrawerItemSelected(view, 2);
                mDrawerLayout.closeDrawer(containerView);
            }
        });

        LinearLayout ll_fea = layout.findViewById(R.id.ll_featured);
        ll_fea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerListener.onDrawerItemSelected(view, 3);
                mDrawerLayout.closeDrawer(containerView);
            }
        });

        LinearLayout ll_news = layout.findViewById(R.id.ll_news);
        ll_news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerListener.onDrawerItemSelected(view, 4);
                mDrawerLayout.closeDrawer(containerView);
            }
        });

        LinearLayout ll_addst = layout.findViewById(R.id.ll_AddStore);
        ll_addst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerListener.onDrawerItemSelected(view, 5);
                mDrawerLayout.closeDrawer(containerView);
            }
        });

        LinearLayout ll_about = layout.findViewById(R.id.ll_about);
        ll_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerListener.onDrawerItemSelected(view, 6);
                mDrawerLayout.closeDrawer(containerView);
            }
        });

        LinearLayout ll_terms = layout.findViewById(R.id.ll_terms);
        ll_terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerListener.onDrawerItemSelected(view, 7);
                mDrawerLayout.closeDrawer(containerView);
            }
        });

//        LinearLayout ll_login = layout.findViewById(R.id.ll_login);
//        ll_login.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                drawerListener.onDrawerItemSelected(view, 8);
//                mDrawerLayout.closeDrawer(containerView);
//            }
//        });
        return layout;
    }


    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {
        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActivity().invalidateOptionsMenu();

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);

            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

    }

    public interface FragmentDrawerListener {
        void onDrawerItemSelected(View view, int position);
    }
}
