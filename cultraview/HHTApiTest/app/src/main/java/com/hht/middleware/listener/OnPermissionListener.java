package com.hht.middleware.listener;

import java.util.List;

/**
 * Author: chenhu
 * Time: 2019/12/19 17:09
 * Description do somethings
 */
public interface OnPermissionListener {

    void onDenied(List<String> deniedPermissions);//拒绝
}
