package com.sureping.controller.ui.startup;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.sureping.controller.R;
import com.sureping.controller.base.cycle.BaseActivity;
import com.sureping.controller.ui.home.HomeActivity;

/**
 * @author sureping
 * @create 19-4-21.
 */
public class StartUpActivity extends BaseActivity {
    @Override
    protected int getViewLayout() {
        return R.layout.activity_startup;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent =new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}
