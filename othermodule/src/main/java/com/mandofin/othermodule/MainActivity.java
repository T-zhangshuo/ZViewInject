package com.mandofin.othermodule;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.zhangshuo.zvanno.ViewInject;
import com.zhangshuo.zvapi.ViewInjector;

public class MainActivity extends Activity {
    @ViewInject(value = M.id.main_btn,clickEvent = "showToast")
    Button button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.om_activity_main);
        ViewInjector.inject(this);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast();
            }
        });
    }

    public void showToast() {
        Toast.makeText(this, "OtherModule click", Toast.LENGTH_SHORT).show();
    }
}
