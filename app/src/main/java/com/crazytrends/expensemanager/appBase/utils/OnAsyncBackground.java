package com.crazytrends.expensemanager.appBase.utils;

public interface OnAsyncBackground {
    void doInBackground();

    void onPostExecute();

    void onPreExecute();
}
