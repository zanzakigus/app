/*
 * Copyright (C) 2017 Google Inc. All Rights Reserved.
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
package ipn.mx.app.notification.mock;

import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationCompat.MessagingStyle;
import androidx.core.app.Person;
import androidx.core.graphics.drawable.IconCompat;

import ipn.mx.app.R;

import java.util.ArrayList;

/** Mock data for each of the Notification Style Demos. */
public final class MockDatabase {


    /** Represents standard data needed for a Notification. */
    public abstract static class MockNotificationData {

        // Standard notification values:
        protected String mContentTitle;
        protected String mContentText;
        protected int mPriority;

        // Notification channel values (O and above):
        protected String mChannelId;
        protected CharSequence mChannelName;
        protected String mChannelDescription;
        protected int mChannelImportance;
        protected boolean mChannelEnableVibrate;
        protected int mChannelLockscreenVisibility;

        // Notification Standard notification get methods:
        public String getContentTitle() {
            return mContentTitle;
        }

        public String getContentText() {
            return mContentText;
        }

        public int getPriority() {
            return mPriority;
        }

        // Channel values (O and above) get methods:
        public String getChannelId() {
            return mChannelId;
        }

        public CharSequence getChannelName() {
            return mChannelName;
        }

        public String getChannelDescription() {
            return mChannelDescription;
        }

        public int getChannelImportance() {
            return mChannelImportance;
        }

        public boolean isChannelEnableVibrate() {
            return mChannelEnableVibrate;
        }

        public int getChannelLockscreenVisibility() {
            return mChannelLockscreenVisibility;
        }

        public void setmContentTitle(String mContentTitle) {
            this.mContentTitle = mContentTitle;
        }

        public void setmContentText(String mContentText) {
            this.mContentText = mContentText;
        }

        public void setmPriority(int mPriority) {
            this.mPriority = mPriority;
        }

        public void setmChannelId(String mChannelId) {
            this.mChannelId = mChannelId;
        }

        public void setmChannelName(CharSequence mChannelName) {
            this.mChannelName = mChannelName;
        }

        public void setmChannelDescription(String mChannelDescription) {
            this.mChannelDescription = mChannelDescription;
        }

        public void setmChannelImportance(int mChannelImportance) {
            this.mChannelImportance = mChannelImportance;
        }

        public void setmChannelEnableVibrate(boolean mChannelEnableVibrate) {
            this.mChannelEnableVibrate = mChannelEnableVibrate;
        }

        public void setmChannelLockscreenVisibility(int mChannelLockscreenVisibility) {
            this.mChannelLockscreenVisibility = mChannelLockscreenVisibility;
        }
    }

    public static Uri resourceToUri(Context context, int resId) {
        return Uri.parse(
                ContentResolver.SCHEME_ANDROID_RESOURCE
                        + "://"
                        + context.getResources().getResourcePackageName(resId)
                        + "/"
                        + context.getResources().getResourceTypeName(resId)
                        + "/"
                        + context.getResources().getResourceEntryName(resId));
    }
}
