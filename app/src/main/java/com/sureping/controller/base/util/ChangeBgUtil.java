package com.sureping.controller.base.util;

import android.content.Context;
import android.view.MenuInflater;
import android.view.View;
import android.widget.PopupMenu;

import com.sureping.controller.R;

/**
 * @author sureping
 * @create 19-4-23.
 */
public class ChangeBgUtil {
    public static void selectFile(Context context, View view, PopupMenu.OnMenuItemClickListener listener){
        PopupMenu popupMenu =new PopupMenu(context,view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.select_file, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(listener);
        popupMenu.show();
    }
}
