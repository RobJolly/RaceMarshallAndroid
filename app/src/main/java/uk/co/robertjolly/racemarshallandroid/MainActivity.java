package uk.co.robertjolly.racemarshallandroid;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import java.lang.reflect.Array;
import java.util.ArrayList;

import uk.co.robertjolly.racemarshallandroid.ui.main.SectionsPagerAdapter;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private SectionsPagerAdapter pagerAdapter = getPagerAdapter();

    private SectionsPagerAdapter getPagerAdapter() {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(this, getSupportFragmentManager(), getRacers());

         return adapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // pagerAdapter.setRacers(getRacers());
        setContentView(R.layout.activity_main);
        ViewPager viewPager = findViewById(R.id.view_pager);
        ((ViewPager)findViewById(R.id.view_pager)).setElevation(12); //Doesn't work when set in XML, unsure why but fix when I know;
        viewPager.setAdapter(pagerAdapter);


        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }

    private ArrayList<Integer> getRacers() {
        ArrayList<Integer> racers = new ArrayList<>();
        for (int i = 0; i < 121; i++) {
            racers.add(i);
        }
        return racers;
    }
}