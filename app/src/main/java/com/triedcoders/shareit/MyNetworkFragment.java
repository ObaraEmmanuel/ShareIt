package com.triedcoders.shareit;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyNetworkFragment extends BaseFragment {


    public MyNetworkFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_network, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        floatingAction = getActivity().findViewById(R.id.floatingAction);
        /*floatingAction.setOnClickListener(view_ -> {
            //do stuff
        });*/
    }
    public int getFloatingActionButtonColor() {
        return R.color.colorAccent;
    }
    public int getFloatingActionButtonIcon() {
        return R.drawable.add_member;
    }

}
