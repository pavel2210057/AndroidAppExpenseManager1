package com.crazytrends.expensemanager.backupRestore;

import java.util.ArrayList;

public interface OnBackupRestore {
    void getList(ArrayList<RestoreRowModel> arrayList);

    void onSuccess(boolean z);
}
