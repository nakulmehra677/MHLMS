package com.mudrahome.MHLMS.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.mudrahome.MHLMS.R;

public class FeedOfferDetailsFragment extends Fragment {

    private EditText offerName;
    private EditText offerDescription;
    private Button button;

    private String strOfferName;
    private String strOfferDescription;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup) inflater.inflate(
                R.layout.fragment_feed_offer_details, container, false);

        offerName = v.findViewById(R.id.offer_name);
        offerDescription = v.findViewById(R.id.description);
        button = v.findViewById(R.id.select_user_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strOfferName = offerName.getText().toString();
                strOfferDescription = offerDescription.getText().toString();

                if (strOfferName.isEmpty() || strOfferDescription.isEmpty()) {
                    Toast.makeText(getContext(), getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show();
                } else {
                    View pager = getActivity().findViewById(R.id.pager);
                    ((ViewPager) pager).setCurrentItem(1);
                }
            }
        });

        return v;
    }
}
