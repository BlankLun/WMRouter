package com.sankuai.waimai.router.demo.advanced.services;


import com.sankuai.waimai.router.annotation.RouterService;
import com.sankuai.waimai.router.demo.lib2.DemoConstant;

import androidx.fragment.app.Fragment;

/**
 * Created by jzj on 2018/4/19.
 */
@RouterService(interfaces = Fragment.class, key = DemoConstant.TEST_FRAGMENT)
public class TestFragment extends Fragment {

}
