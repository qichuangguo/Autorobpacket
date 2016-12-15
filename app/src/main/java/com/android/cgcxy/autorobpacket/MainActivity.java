package com.android.cgcxy.autorobpacket;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("首页");

        BaseAccessibilityService.getInstance().init(this);
        Intent intent = new Intent(this,MyService.class);
        startService(intent);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId()==android.R.id.home){

            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void onclickHelp(View view){

        startActivity(new Intent(this,HelpActivity.class));

    }

    public void onclickStart(View view){

        if (!BaseAccessibilityService.getInstance().checkAccessibilityEnabled("22222")){
            BaseAccessibilityService.getInstance().goAccess();
        }
    }
}
