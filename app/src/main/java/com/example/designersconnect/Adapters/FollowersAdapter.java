package com.example.designersconnect.Adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.designersconnect.Fragments.FollowersFragment;
import com.example.designersconnect.Fragments.FollowingFragment;

public class FollowersAdapter extends FragmentPagerAdapter {

    String userId;
    public FollowersAdapter(@NonNull FragmentManager fm, String userId) {
        super(fm);
        this.userId = userId;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if(position == 0){
            return FollowingFragment.getInstance(userId);
        }
        else{
            return FollowersFragment.getInstance(userId);
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if(position == 0)
            return "Following";
        else
            return "Followers";
    }
}
