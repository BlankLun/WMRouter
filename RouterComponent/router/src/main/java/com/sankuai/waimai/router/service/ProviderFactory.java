package com.sankuai.waimai.router.service;


import com.sankuai.waimai.router.utils.ProviderPool;

import androidx.annotation.NonNull;

public class ProviderFactory implements IFactory {

    public static final IFactory INSTANCE = new ProviderFactory();

    private ProviderFactory() {

    }

    @NonNull
    @Override
    public <T> T create(@NonNull Class<T> clazz) throws Exception {
        return ProviderPool.create(clazz);
    }
}
