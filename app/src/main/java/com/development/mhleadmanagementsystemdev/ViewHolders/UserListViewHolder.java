package com.development.mhleadmanagementsystemdev.ViewHolders;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.development.mhleadmanagementsystemdev.Interfaces.ItemClickListener;
import com.development.mhleadmanagementsystemdev.R;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;

public class UserListViewHolder extends RecyclerView.ViewHolder {

    public TextView userName, userType, optionMenu;

    public UserListViewHolder(View itemView) {
        super(itemView);

        userName = itemView.findViewById(R.id.user_name);
        userType = itemView.findViewById(R.id.user_type);
        optionMenu = itemView.findViewById(R.id.menu_option);


        /*itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.onClick(view, getAdapterPosition());
            }
        });*/
    }

    public void setUserName(TextView userName) {
        this.userName = userName;
    }

    public void setUserType(TextView userType) {
        this.userType = userType;
    }
}
