package com.mudrahome.MHLMS.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.mudrahome.MHLMS.R;

public class SelectUserForOfferFragment extends Fragment implements View.OnClickListener {

    private Button delhiButton;
    private Button indoreButton;
    private Button jaipurButton;
    private Button gwaliorButton;
    private Button ahmedabadButton;
    private Button telecallerButton;
    private Button salesmanButton;

    private boolean delhiButtonActive = true;
    private boolean indoreButtonActive = false;
    private boolean jaipurButtonActive = false;
    private boolean gwaliorButtonActive = false;
    private boolean ahmedabadButtonActive = false;
    private boolean telecallerButtonActive = true;
    private boolean salesmanButtonActive = false;

    private ScrollView delhiTelecallerScrollView;
    private ScrollView indoreTelecallerScrollView;
    private ScrollView jaipurTelecallerScrollView;
    private ScrollView gwaliorTelecallerScrollView;
    private ScrollView ahmedabadTelecallerScrollView;
    private ScrollView delhiSalesmanScrollView;
    private ScrollView indoreSalesmanScrollView;
    private ScrollView jaipurSalesmanScrollView;
    private ScrollView gwaliorSalesmanScrollView;
    private ScrollView ahmedabadSalesmanScrollView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup) inflater.inflate(
                R.layout.fragment_select_user_for_offer, container, false);

        delhiButton = v.findViewById(R.id.delhi_button);
        indoreButton = v.findViewById(R.id.indore_button);
        jaipurButton = v.findViewById(R.id.jaipur_button);
        gwaliorButton = v.findViewById(R.id.gwalior_button);
        ahmedabadButton = v.findViewById(R.id.ahmedabad_button);
        telecallerButton = v.findViewById(R.id.telecaller_button);
        salesmanButton = v.findViewById(R.id.salesman_button);

        delhiButton.setOnClickListener(this);
        indoreButton.setOnClickListener(this);
        jaipurButton.setOnClickListener(this);
        gwaliorButton.setOnClickListener(this);
        ahmedabadButton.setOnClickListener(this);
        telecallerButton.setOnClickListener(this);
        salesmanButton.setOnClickListener(this);

        delhiTelecallerScrollView = v.findViewById(R.id.delhi_telecaller_scroll_view);
        indoreTelecallerScrollView = v.findViewById(R.id.indore_telecaller_scroll_view);
        jaipurTelecallerScrollView = v.findViewById(R.id.jaipur_telecaller_scroll_view);
        gwaliorTelecallerScrollView = v.findViewById(R.id.gwalior_telecaller_scroll_view);
        ahmedabadTelecallerScrollView = v.findViewById(R.id.ahmedabad_telecaller_scroll_view);
        delhiSalesmanScrollView = v.findViewById(R.id.delhi_salesmen_scroll_view);
        indoreSalesmanScrollView = v.findViewById(R.id.indore_salesman_scroll_view);
        jaipurSalesmanScrollView = v.findViewById(R.id.jaipur_salesman_scroll_view);
        gwaliorSalesmanScrollView = v.findViewById(R.id.gwalior_salesman_scroll_view);
        ahmedabadSalesmanScrollView = v.findViewById(R.id.ahmedabad_salesman_scroll_view);

        showDelhiTelecallerScrollView();

        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.telecaller_button:
                if (salesmanButtonActive) {

                    if (delhiButtonActive) {
                        showDelhiTelecallerScrollView();
                        hideDelhiSalesmanScrollView();
                    } else if (indoreButtonActive) {
                        showIndoreTelecallerScrollView();
                        hideIndoreSalesmanScrollView();
                    } else if (jaipurButtonActive) {
                        showJaipurTelecallerScrollView();
                        hideJaipurSalesmanScrollView();
                    } else if (gwaliorButtonActive) {
                        showGwaliorTelecallerScrollView();
                        hideGwaliorSalesmanScrollView();
                    } else {
                        showAhmedabadTelecallerScrollView();
                        hideAhmedabadSalesmanScrollView();
                    }
                }
                setTelecallerButtonWhite();
                setSalesmanButtonGray();
                break;

            case R.id.salesman_button:
                if (telecallerButtonActive) {

                    if (delhiButtonActive) {
                        showDelhiSalesmanScrollView();
                        hideDelhiTelecallerScrollView();
                    } else if (indoreButtonActive) {
                        hideIndoreTelecallerScrollView();
                        showIndoreSalesmanScrollView();
                    } else if (jaipurButtonActive) {
                        hideJaipurTelecallerScrollView();
                        showJaipurSalesmanScrollView();
                    } else if (gwaliorButtonActive) {
                        hideGwaliorTelecallerScrollView();
                        showGwaliorSalesmanScrollView();
                    } else {
                        showAhmedabadSalesmanScrollView();
                        hideAhmedabadTelecallerScrollView();
                    }
                    setTelecallerButtonGray();
                    setSalesmanButtonWhite();
                }
                break;

            case R.id.delhi_button:
                if (indoreButtonActive) {
                    if (telecallerButtonActive) {
                        hideIndoreTelecallerScrollView();
                        showDelhiTelecallerScrollView();
                    } else {
                        hideIndoreSalesmanScrollView();
                        showDelhiSalesmanScrollView();
                    }
                    setDelhiButtonWhite();
                    setIndoreButtonGray();
                } else if (jaipurButtonActive) {
                    if (telecallerButtonActive) {
                        hideJaipurTelecallerScrollView();
                        showDelhiTelecallerScrollView();
                    } else {
                        hideJaipurSalesmanScrollView();
                        showDelhiSalesmanScrollView();
                    }
                    setDelhiButtonWhite();
                    setJaipurButtonGray();
                } else if (gwaliorButtonActive) {
                    if (telecallerButtonActive) {
                        hideGwaliorTelecallerScrollView();
                        showDelhiTelecallerScrollView();
                    } else {
                        hideGwaliorSalesmanScrollView();
                        showDelhiSalesmanScrollView();
                    }
                    setDelhiButtonWhite();
                    setGwaliorButtonGray();
                } else if (ahmedabadButtonActive) {
                    if (telecallerButtonActive) {
                        hideAhmedabadTelecallerScrollView();
                        showDelhiTelecallerScrollView();
                    } else {
                        hideAhmedabadSalesmanScrollView();
                        showDelhiSalesmanScrollView();
                    }
                    setDelhiButtonWhite();
                    setAhmedabadButtonGray();
                }
                break;

            case R.id.indore_button:
                if (delhiButtonActive) {
                    if (telecallerButtonActive) {
                        hideDelhiTelecallerScrollView();
                        showIndoreTelecallerScrollView();
                    } else {
                        hideDelhiSalesmanScrollView();
                        showIndoreSalesmanScrollView();
                    }
                    setIndoreButtonWhite();
                    setDelhiButtonGray();
                } else if (jaipurButtonActive) {
                    if (telecallerButtonActive) {
                        hideJaipurTelecallerScrollView();
                        showIndoreTelecallerScrollView();
                    } else {
                        hideJaipurSalesmanScrollView();
                        showIndoreSalesmanScrollView();
                    }
                    setIndoreButtonWhite();
                    setJaipurButtonGray();
                } else if (gwaliorButtonActive) {
                    if (telecallerButtonActive) {
                        hideGwaliorTelecallerScrollView();
                        showIndoreTelecallerScrollView();
                    } else {
                        hideGwaliorSalesmanScrollView();
                        showIndoreSalesmanScrollView();
                    }
                    setIndoreButtonWhite();
                    setGwaliorButtonGray();
                } else if (ahmedabadButtonActive) {
                    if (telecallerButtonActive) {
                        hideAhmedabadTelecallerScrollView();
                        showIndoreTelecallerScrollView();
                    } else {
                        hideAhmedabadSalesmanScrollView();
                        showIndoreSalesmanScrollView();
                    }
                    setIndoreButtonWhite();
                    setAhmedabadButtonGray();
                }
                break;

            case R.id.jaipur_button:
                if (indoreButtonActive) {
                    if (telecallerButtonActive) {
                        hideIndoreTelecallerScrollView();
                        showJaipurTelecallerScrollView();
                    } else {
                        hideIndoreSalesmanScrollView();
                        showJaipurSalesmanScrollView();
                    }
                    setJaipurButtonWhite();
                    setIndoreButtonGray();
                } else if (delhiButtonActive) {
                    if (telecallerButtonActive) {
                        hideDelhiTelecallerScrollView();
                        showJaipurTelecallerScrollView();
                    } else {
                        hideDelhiSalesmanScrollView();
                        showJaipurSalesmanScrollView();
                    }
                    setJaipurButtonWhite();
                    setDelhiButtonGray();
                } else if (gwaliorButtonActive) {
                    if (telecallerButtonActive) {
                        hideGwaliorTelecallerScrollView();
                        showJaipurTelecallerScrollView();
                    } else {
                        hideGwaliorSalesmanScrollView();
                        showJaipurSalesmanScrollView();
                    }
                    setJaipurButtonWhite();
                    setGwaliorButtonGray();
                } else if (ahmedabadButtonActive) {
                    if (telecallerButtonActive) {
                        hideAhmedabadTelecallerScrollView();
                        showJaipurTelecallerScrollView();
                    } else {
                        hideAhmedabadSalesmanScrollView();
                        showJaipurSalesmanScrollView();
                    }
                    setJaipurButtonWhite();
                    setAhmedabadButtonGray();
                }
                break;

            case R.id.gwalior_button:
                if (indoreButtonActive) {
                    if (telecallerButtonActive) {
                        hideIndoreTelecallerScrollView();
                        showGwaliorTelecallerScrollView();
                    } else {
                        hideIndoreSalesmanScrollView();
                        showGwaliorSalesmanScrollView();
                    }
                    setGwaliorButtonWhite();
                    setIndoreButtonGray();
                } else if (jaipurButtonActive) {
                    if (telecallerButtonActive) {
                        hideJaipurTelecallerScrollView();
                        showGwaliorTelecallerScrollView();
                    } else {
                        hideJaipurSalesmanScrollView();
                        showGwaliorSalesmanScrollView();
                    }
                    setGwaliorButtonWhite();
                    setJaipurButtonGray();
                } else if (delhiButtonActive) {
                    if (telecallerButtonActive) {
                        hideDelhiTelecallerScrollView();
                        showGwaliorTelecallerScrollView();
                    } else {
                        showGwaliorSalesmanScrollView();
                        hideDelhiSalesmanScrollView();
                    }
                    setGwaliorButtonWhite();
                    setDelhiButtonGray();
                } else if (ahmedabadButtonActive) {
                    if (telecallerButtonActive) {
                        hideAhmedabadTelecallerScrollView();
                        showGwaliorTelecallerScrollView();
                    } else {
                        hideAhmedabadSalesmanScrollView();
                        showGwaliorSalesmanScrollView();
                    }
                    setGwaliorButtonWhite();
                    setAhmedabadButtonGray();
                }
                break;

            case R.id.ahmedabad_button:
                if (indoreButtonActive) {
                    if (telecallerButtonActive) {
                        hideIndoreTelecallerScrollView();
                        showAhmedabadTelecallerScrollView();
                    } else {
                        hideIndoreSalesmanScrollView();
                        showAhmedabadSalesmanScrollView();
                    }
                    setAhmedabadButtonWhite();
                    setIndoreButtonGray();
                } else if (jaipurButtonActive) {
                    if (telecallerButtonActive) {
                        hideJaipurTelecallerScrollView();
                        showAhmedabadTelecallerScrollView();
                    } else {
                        hideJaipurSalesmanScrollView();
                        showAhmedabadSalesmanScrollView();
                    }
                    setAhmedabadButtonWhite();
                    setJaipurButtonGray();
                } else if (gwaliorButtonActive) {
                    if (telecallerButtonActive) {
                        hideGwaliorTelecallerScrollView();
                        showAhmedabadTelecallerScrollView();
                    } else {
                        hideGwaliorSalesmanScrollView();
                        showAhmedabadSalesmanScrollView();
                    }
                    setAhmedabadButtonWhite();
                    setGwaliorButtonGray();
                } else if (delhiButtonActive) {
                    if (telecallerButtonActive) {
                        showAhmedabadTelecallerScrollView();
                        hideDelhiTelecallerScrollView();
                    } else {
                        showAhmedabadSalesmanScrollView();
                        hideDelhiSalesmanScrollView();
                    }
                    setAhmedabadButtonWhite();
                    setDelhiButtonGray();
                }
                break;
        }
    }

    private void setDelhiButtonWhite() {
        delhiButton.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        delhiButtonActive = true;
    }

    private void setJaipurButtonWhite() {
        jaipurButton.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        jaipurButtonActive = true;
    }

    private void setIndoreButtonWhite() {
        indoreButton.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        indoreButtonActive = true;
    }

    private void setGwaliorButtonWhite() {
        gwaliorButton.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        gwaliorButtonActive = true;
    }

    private void setAhmedabadButtonWhite() {
        ahmedabadButton.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        ahmedabadButtonActive = true;
    }

    private void setSalesmanButtonWhite() {
        salesmanButton.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        salesmanButtonActive = true;
    }

    private void setTelecallerButtonWhite() {
        telecallerButton.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        telecallerButtonActive = true;
    }

    private void setDelhiButtonGray() {
        delhiButton.setBackgroundColor(getResources().getColor(R.color.colorGray));
        delhiButtonActive = false;
    }

    private void setIndoreButtonGray() {
        indoreButton.setBackgroundColor(getResources().getColor(R.color.colorGray));
        indoreButtonActive = false;
    }

    private void setJaipurButtonGray() {
        jaipurButton.setBackgroundColor(getResources().getColor(R.color.colorGray));
        jaipurButtonActive = false;
    }

    private void setGwaliorButtonGray() {
        gwaliorButton.setBackgroundColor(getResources().getColor(R.color.colorGray));
        gwaliorButtonActive = false;
    }

    private void setAhmedabadButtonGray() {
        ahmedabadButton.setBackgroundColor(getResources().getColor(R.color.colorGray));
        ahmedabadButtonActive = false;
    }

    private void setTelecallerButtonGray() {
        telecallerButton.setBackgroundColor(getResources().getColor(R.color.colorGray));
        telecallerButtonActive = false;
    }

    private void setSalesmanButtonGray() {
        salesmanButton.setBackgroundColor(getResources().getColor(R.color.colorGray));
        salesmanButtonActive = false;
    }

    private void showDelhiTelecallerScrollView() {
        if (delhiTelecallerScrollView.getChildCount() > 0)
            Toast.makeText(getContext(), "Empty", Toast.LENGTH_SHORT).show();
        delhiTelecallerScrollView.setVisibility(View.VISIBLE);
    }

    private void showIndoreTelecallerScrollView() {
        indoreTelecallerScrollView.setVisibility(View.VISIBLE);
    }

    private void showJaipurTelecallerScrollView() {
        jaipurTelecallerScrollView.setVisibility(View.VISIBLE);
    }

    private void showGwaliorTelecallerScrollView() {
        gwaliorTelecallerScrollView.setVisibility(View.VISIBLE);
    }

    private void showAhmedabadTelecallerScrollView() {
        ahmedabadTelecallerScrollView.setVisibility(View.VISIBLE);
    }

    private void showDelhiSalesmanScrollView() {
        delhiSalesmanScrollView.setVisibility(View.VISIBLE);
    }

    private void showIndoreSalesmanScrollView() {
        indoreSalesmanScrollView.setVisibility(View.VISIBLE);
    }

    private void showJaipurSalesmanScrollView() {
        jaipurSalesmanScrollView.setVisibility(View.VISIBLE);
    }

    private void showGwaliorSalesmanScrollView() {
        gwaliorSalesmanScrollView.setVisibility(View.VISIBLE);
    }

    private void showAhmedabadSalesmanScrollView() {
        ahmedabadSalesmanScrollView.setVisibility(View.VISIBLE);
    }

    private void hideDelhiTelecallerScrollView() {
        delhiTelecallerScrollView.setVisibility(View.GONE);
    }

    private void hideIndoreTelecallerScrollView() {
        indoreTelecallerScrollView.setVisibility(View.GONE);
    }

    private void hideJaipurTelecallerScrollView() {
        jaipurTelecallerScrollView.setVisibility(View.GONE);
    }

    private void hideGwaliorTelecallerScrollView() {
        gwaliorTelecallerScrollView.setVisibility(View.GONE);
    }

    private void hideAhmedabadTelecallerScrollView() {
        ahmedabadTelecallerScrollView.setVisibility(View.GONE);
    }

    private void hideDelhiSalesmanScrollView() {
        delhiSalesmanScrollView.setVisibility(View.GONE);
    }

    private void hideIndoreSalesmanScrollView() {
        indoreSalesmanScrollView.setVisibility(View.GONE);
    }

    private void hideJaipurSalesmanScrollView() {
        jaipurSalesmanScrollView.setVisibility(View.GONE);
    }

    private void hideGwaliorSalesmanScrollView() {
        gwaliorSalesmanScrollView.setVisibility(View.GONE);
    }

    private void hideAhmedabadSalesmanScrollView() {
        ahmedabadSalesmanScrollView.setVisibility(View.GONE);
    }
}