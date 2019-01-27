/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.dragonide.raitplacementcell;

import android.net.Uri;

public class Constants {
public String sessID = null;
    /**
     * Account type string.
     */
    public static final String ACCOUNT_TYPE = "com.dragonide.raitplacementcell";

    /**
     * Authtoken type string.
     */
    public static final String AUTHTOKEN_TYPE = "com.dragonide.raitplacementcell";

    public static final Uri BASE_URI = Uri.parse("content://"+ACCOUNT_TYPE);
    public static final String FINISH_SYNC = ACCOUNT_TYPE+ ".FINISH";


    public String getSessID() {
        return sessID;
    }

    public void setSessID(String sessID) {
        this.sessID = sessID;
    }
}
