/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ctv.settings.security.permission;

import android.app.AppOpsManager;
import android.content.pm.PackageManager;


public final class Permission {
    private final String mName;
    private final int mAppOp;

    private boolean mGranted;
    private boolean mAppOpAllowed;
    private int mFlags;


    public static final int FLAG_PERMISSION_USER_FIXED =  1 <<1;
    public static final int FLAG_PERMISSION_SYSTEM_FIXED =  1 << 4;
    public static final int FLAG_PERMISSION_POLICY_FIXED =  1 << 2;
    public static final int FLAG_PERMISSION_USER_SET = 1 << 0;
    public static final int FLAG_PERMISSION_GRANTED_BY_DEFAULT =  1 << 5;
    public static final int FLAG_PERMISSION_REVOKE_ON_UPGRADE =  1 << 3;
    public static final int OP_NONE = -1;

    public Permission(String name, boolean granted,
                      int appOp, boolean appOpAllowed, int flags) {
        mName = name;
        mGranted = granted;
        mAppOp = appOp;
        mAppOpAllowed = appOpAllowed;
        mFlags = flags;
    }

    public String getName() {
        return mName;
    }

    public int getAppOp() {
        return mAppOp;
    }

    public int getFlags() {
        return mFlags;
    }

    public boolean hasAppOp() {
        return mAppOp != OP_NONE;
    }

    public boolean isGranted() {
        return mGranted;
    }

    public void setGranted(boolean mGranted) {
        this.mGranted = mGranted;
    }

    public boolean isAppOpAllowed() {
        return mAppOpAllowed;
    }

    public boolean isUserFixed() {
        return (mFlags & FLAG_PERMISSION_USER_FIXED) != 0;
    }

    public void setUserFixed(boolean userFixed) {
        if (userFixed) {
            mFlags |= FLAG_PERMISSION_USER_FIXED;
        } else {
            mFlags &= ~FLAG_PERMISSION_USER_FIXED;
        }
    }

    public boolean isSystemFixed() {
        return (mFlags & FLAG_PERMISSION_SYSTEM_FIXED) != 0;
    }

    public boolean isPolicyFixed() {
        return (mFlags & FLAG_PERMISSION_POLICY_FIXED) != 0;
    }

    public boolean isUserSet() {
        return (mFlags & FLAG_PERMISSION_USER_SET) != 0;
    }

    public boolean isGrantedByDefault() {
        return (mFlags & FLAG_PERMISSION_GRANTED_BY_DEFAULT) != 0;
    }

    public void setUserSet(boolean userSet) {
        if (userSet) {
            mFlags |= FLAG_PERMISSION_USER_SET;
        } else {
            mFlags &= ~FLAG_PERMISSION_USER_SET;
        }
    }

    public void setPolicyFixed(boolean policyFixed) {
        if (policyFixed) {
            mFlags |= FLAG_PERMISSION_POLICY_FIXED;
        } else {
            mFlags &= ~FLAG_PERMISSION_POLICY_FIXED;
        }
    }

    public boolean shouldRevokeOnUpgrade() {
        return (mFlags & FLAG_PERMISSION_REVOKE_ON_UPGRADE) != 0;
    }

    public void setRevokeOnUpgrade(boolean revokeOnUpgrade) {
        if (revokeOnUpgrade) {
            mFlags |= FLAG_PERMISSION_REVOKE_ON_UPGRADE;
        } else {
            mFlags &= ~FLAG_PERMISSION_REVOKE_ON_UPGRADE;
        }
    }

    public void setAppOpAllowed(boolean mAppOpAllowed) {
        this.mAppOpAllowed = mAppOpAllowed;
    }
}