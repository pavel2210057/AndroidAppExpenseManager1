package com.crazytrends.expensemanager.appBase.models.spinner;



import androidx.databinding.BaseObservable;

public class SpinnerRowModel extends BaseObservable {
    private int id;
    private boolean isSelected;
    private String label;
    private int type;
    private String value;

    public int getId() {
        return this.id;
    }

    public void setId(int i) {
        this.id = i;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String str) {
        this.label = str;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String str) {
        this.value = str;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int i) {
        this.type = i;
    }


    public Boolean isSelected() {
        return this.isSelected;
    }

    public void setSelected(boolean z) {
        this.isSelected = z;
        notifyChange();
    }
}
