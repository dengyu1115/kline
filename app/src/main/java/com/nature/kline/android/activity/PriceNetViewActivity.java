package com.nature.kline.android.activity;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.nature.kline.android.util.ViewUtil;
import com.nature.kline.android.view.PriceNetView;
import com.nature.kline.common.manager.PriceNetManager;
import com.nature.kline.common.model.PriceNet;
import com.nature.kline.common.util.InstanceHolder;

import java.util.List;

/**
 * 债券线图
 * @author nature
 * @version 1.0.0
 * @since 2020/4/5 15:46
 */
public class PriceNetViewActivity extends AppCompatActivity {

    private final PriceNetManager priceNetManager = InstanceHolder.get(PriceNetManager.class);

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PriceNetView priceNetView = new PriceNetView(this);
        priceNetView.data(this.data());
        this.setContentView(priceNetView);
        ViewUtil.initActivity(this);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    private List<PriceNet> data() {
        String code = this.getIntent().getStringExtra("code");
        return priceNetManager.listByCode(code);
    }

}
