package com.sankuai.waimai.router.activity;

import android.app.Activity;
import android.content.Intent;

import com.sankuai.waimai.router.core.UriHandler;
import com.sankuai.waimai.router.core.UriRequest;

import androidx.annotation.NonNull;

/**
 * 通过Class跳转Activity的 {@link UriHandler}
 *
 * Created by jzj on 2017/4/11.
 */
public class ActivityHandler extends AbsActivityHandler {

    @NonNull
    protected final Class<? extends Activity> mClazz;

    /**
     * @param clazz 要跳转的Activity
     */
    public ActivityHandler(@NonNull Class<? extends Activity> clazz) {
        mClazz = clazz;
    }

    @NonNull
    @Override
    protected Intent createIntent(@NonNull UriRequest request) {
        return new Intent(request.getContext(), mClazz);
    }

    @Override
    public String toString() {
        return "ActivityHandler (" + mClazz.getSimpleName() + ")";
    }
}
