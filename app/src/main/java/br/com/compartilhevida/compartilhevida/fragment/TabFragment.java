package br.com.compartilhevida.compartilhevida.fragment;


import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.compartilhevida.compartilhevida.R;
import br.com.compartilhevida.compartilhevida.adapter.TabsAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class TabFragment extends Fragment {

    public  static TabLayout mTabLayout;
    public  static ViewPager viewPager;
    public  static int int_items= 2;
    AppBarLayout mAppBarLayout;


    public TabFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_tab,null);

        mTabLayout=(TabLayout)v.findViewById(R.id.tabs);

        viewPager=(ViewPager)v.findViewById(R.id.viewpager);
        //set an adpater;
        viewPager.setAdapter(new TabsAdapter( getChildFragmentManager()));
        mTabLayout.setupWithViewPager(viewPager);
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);

        mTabLayout.post(new Runnable() {
            @Override
            public void run() {
                mTabLayout.setupWithViewPager(viewPager);
            }
        });

        return v;
    }

}
