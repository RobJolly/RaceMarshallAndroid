package uk.co.robertjolly.racemarshallandroid;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import uk.co.robertjolly.racemarshallandroid.data.Checkpoint;
import uk.co.robertjolly.racemarshallandroid.data.Checkpoints;
import uk.co.robertjolly.racemarshallandroid.ui.main.adapters.SectionsPagerAdapter;

public class MainActivity extends AppCompatActivity {

    private SectionsPagerAdapter pagerAdapter = getPagerAdapter();
    private Checkpoints checkpoints;

    private SectionsPagerAdapter getPagerAdapter() {
        checkpoints = setRaceData();
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(this, getSupportFragmentManager(), getCheckpoints());

         return adapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewPager viewPager = findViewById(R.id.mainViewPager);
        ((ViewPager)findViewById(R.id.mainViewPager)).setElevation(12); //Doesn't work when set in XML, unsure why but fix when I know;
        viewPager.setAdapter(pagerAdapter);


        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }

    private Checkpoints getCheckpoints() {
        return checkpoints;
    }

    //here is where I plan to load in or ask for the overall race data. For now this just defaults to 100.
    private Checkpoints setRaceData() {
        Checkpoints checkpoints = new Checkpoints();
        checkpoints.addCheckpoint(new Checkpoint(1, 150));
        return checkpoints;
    }
}