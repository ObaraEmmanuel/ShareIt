package com.triedcoders.shareit;


import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseFragment extends Fragment {
    FloatingActionButton floatingAction;
    protected FileItemAdapter fileAdapter;
    public BaseFragment() {
        // Required empty public constructor
    }

    public int getFloatingActionButtonColor() {
        return -1;
    }

    public int getFloatingActionButtonIcon() {
        return -1;
    }

    public final boolean useFloatingActionButton() {
        int color = getFloatingActionButtonColor();
        int icon = getFloatingActionButtonIcon();
        return color != -1 && icon != -1;
    }

}
