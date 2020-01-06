package com.rad5.chatapp.Adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class FragmentaAdapter extends FragmentPagerAdapter {

    List<Fragment> fragments =  new ArrayList<>();
    List<String>fragmentNmae = new ArrayList<>();

    public FragmentaAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        return fragments.get(position);
    }

    @Override
    public int getCount() {

        return fragments.size();
    }
    public  void addFragments(Fragment fragment, String title){
        fragments.add(fragment);
        fragmentNmae.add(title);

    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentNmae.get(position);
    }
}
