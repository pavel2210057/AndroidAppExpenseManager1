package com.crazytrends.expensemanager.appBase.models.toolbar;


import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class ToolbarModel extends BaseObservable {
    private boolean isAdd=false;
    private boolean isBack = true;
    private boolean isBackMenu = true;
    private boolean isDelete=false;
    private boolean isHome=false;
    private boolean isOtherEt=false;
    private boolean isOtherMenu=false;
    private boolean isProgressMenu=false;
    private boolean isSearchMenu=false;
    private boolean isShare=false;
    private boolean isSpinnerMenu=false;
    private String otherEtHint;
    private String otherEtText;
    private String title;

    @Bindable
    public boolean isBackMenu() {
        return this.isBackMenu;
    }

    public void setBackMenu(boolean z) {
        this.isBackMenu = z;
        notifyChange();
    }

    @Bindable
    public boolean isBack() {
        return this.isBack;
    }

    public void setBack(boolean z) {
        this.isBack = z;
        notifyChange();
    }

    @Bindable
    public boolean isHome() {
        return this.isHome;
    }

    public void setHome(boolean z) {
        this.isHome = z;
        notifyChange();
    }

    @Bindable
    public String getTitle() {
        return this.title;
    }

    public void setTitle(String str) {
        this.title = str;
        notifyChange();
    }

    @Bindable
    public boolean isProgressMenu() {
        return this.isProgressMenu;
    }

    public void setProgressMenu(boolean z) {
        this.isProgressMenu = z;
        notifyChange();
    }

    @Bindable
    public boolean isAdd() {
        return this.isAdd;
    }

    public void setAdd(boolean z) {
        this.isAdd = z;
        notifyChange();
    }

    @Bindable
    public boolean isDelete() {
        return this.isDelete;
    }

    public void setDelete(boolean z) {
        this.isDelete = z;
        notifyChange();
    }

    @Bindable
    public boolean isOtherMenu() {
        return this.isOtherMenu;
    }

    public void setOtherMenu(boolean z) {
        this.isOtherMenu = z;
        notifyChange();
    }

    @Bindable
    public boolean isShare() {
        return this.isShare;
    }

    public void setShare(boolean z) {
        this.isShare = z;
        notifyChange();
    }

    @Bindable
    public boolean isOtherEt() {
        return this.isOtherEt;
    }

    public void setOtherEt(boolean z) {
        this.isOtherEt = z;
        notifyChange();
    }

    @Bindable
    public String getOtherEtHint() {
        return this.otherEtHint;
    }

    public void setOtherEtHint(String str) {
        this.otherEtHint = str;
        notifyChange();
    }

    @Bindable
    public String getOtherEtText() {
        return this.otherEtText;
    }

    public void setOtherEtText(String str) {
        this.otherEtText = str;
        notifyChange();
    }

    @Bindable
    public boolean isSearchMenu() {
        return this.isSearchMenu;
    }

    public void setSearchMenu(boolean z) {
        this.isSearchMenu = z;
        notifyChange();
    }

    @Bindable
    public boolean isSpinnerMenu() {
        return this.isSpinnerMenu;
    }

    public void setSpinnerMenu(boolean z) {
        this.isSpinnerMenu = z;
        notifyChange();
    }
}
