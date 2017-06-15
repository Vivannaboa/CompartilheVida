package br.com.compartilhevida.compartilhevida.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import br.com.compartilhevida.compartilhevida.fragment.post.MyPostsFragment;
import br.com.compartilhevida.compartilhevida.fragment.post.RecentPostsFragment;

import static br.com.compartilhevida.compartilhevida.fragment.TabFragment.int_items;

/**
 * Created by Admin on 3/1/2017.
 */

public class TabsAdapter extends FragmentPagerAdapter {

    public TabsAdapter(FragmentManager fm)
    {
        super(fm);
    }
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return  new RecentPostsFragment();
            case 1:
                return new MyPostsFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return int_items;
    }

    public CharSequence getPageTitle(int position){
        switch (position){
            case 0:
                return "Meus Posts";
            case 1:
                return "Recentes";
        }

        return null;
    }
}
