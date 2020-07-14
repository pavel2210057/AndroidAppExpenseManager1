package com.crazytrends.expensemanager.pdfReport;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build.VERSION;


import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.crazytrends.expensemanager.R;
import com.crazytrends.expensemanager.appBase.utils.AppConstants;
import com.crazytrends.expensemanager.appBase.utils.Constants;
import com.crazytrends.expensemanager.appBase.utils.RecyclerItemClick;
import java.io.File;
import java.util.ArrayList;

public class ReportPdfAdapter extends RecyclerView.Adapter {
    private Context context;
    public RecyclerItemClick itemClick;
    private ArrayList<File> list;

    private class RowHolder extends RecyclerView.ViewHolder implements OnClickListener {
        ImageView imgIcon;
        ImageView imgOptions;
        TextView txtDate;
        TextView txtName;
        TextView txtSize;

        public RowHolder(View view) {
            super(view);
            this.txtName = (TextView) view.findViewById(R.id.txtName);
            this.txtDate = (TextView) view.findViewById(R.id.txtDate);
            this.txtSize = (TextView) view.findViewById(R.id.txtSize);
            this.imgOptions = (ImageView) view.findViewById(R.id.imgOptions);
            this.imgIcon = (ImageView) view.findViewById(R.id.imgIcon);
            view.setOnClickListener(this);
            this.imgOptions.setOnClickListener(this);
        }

        public void onClick(View view) {
            if (view.getId() == R.id.imgOptions) {
                ReportPdfAdapter.this.showMenu(this.imgOptions, getAdapterPosition());
            } else {
                ReportPdfAdapter.this.openFile(getAdapterPosition());
            }
        }
    }

    public ReportPdfAdapter(Context context2, ArrayList<File> arrayList, RecyclerItemClick recyclerItemClick) {
        this.context = context2;
        this.list = arrayList;
        this.itemClick = recyclerItemClick;
    }

    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new RowHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.report_pdf_item_act, viewGroup, false));
    }



    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof RowHolder) {
            RowHolder rowHolder = (RowHolder) viewHolder;
            rowHolder.imgIcon.setImageResource(isPdf(i) ? R.drawable.pdf : R.drawable.xls);
            rowHolder.txtName.setText(((File) this.list.get(i)).getName());
            TextView textView = rowHolder.txtDate;
            long lastModified = ((File) this.list.get(i)).lastModified();
            StringBuilder sb = new StringBuilder();
            sb.append("dd/MM/yyyy ");
            sb.append(Constants.showTimePattern);
            textView.setText(AppConstants.getFormattedDate(lastModified, sb.toString()));
            rowHolder.txtSize.setText(AppConstants.getFileSize(((File) this.list.get(i)).length()));
        }
    }

    private boolean isPdf(int i) {
        return ((File) this.list.get(i)).toString().contains(".pdf");
    }

    public int getItemCount() {
        return this.list.size();
    }

    public void showMenu(ImageView imageView, final int i) {
        PopupMenu popupMenu = new PopupMenu(this.context, imageView);
        popupMenu.inflate(R.menu.report_options_menu);
        popupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menuDelete :
                        ReportPdfAdapter.this.deleteFile(i);
                        ReportPdfAdapter.this.itemClick.onClick(i, 0);
                        return true;
                    case R.id.menuOpen :
                        ReportPdfAdapter.this.openFile(i);
                        return true;
                    case R.id.menuShare :
                        ReportPdfAdapter.this.shareFile(i);
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }

    public void openFile(int i) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if (VERSION.SDK_INT > 23) {
            intent.setDataAndType(FileProvider.getUriForFile(this.context, "com.crazytrends.expensemanager.provider", (File) this.list.get(i)), isPdf(i) ? "application/pdf" : "application/vnd.ms-excel");
        } else {
            intent.setDataAndType(Uri.fromFile((File) this.list.get(i)), isPdf(i) ? "application/pdf" : "application/vnd.ms-excel");
        }
        try {
            this.context.startActivity(Intent.createChooser(intent, "Open File"));
        } catch (ActivityNotFoundException unused) {
            Toast.makeText(this.context, "No app to read File", Toast.LENGTH_LONG).show();
        }
    }


    public void shareFile(int i) {
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("*/*");
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if (VERSION.SDK_INT > 23) {
            intent.putExtra("android.intent.extra.STREAM", FileProvider.getUriForFile(this.context, "com.crazytrends.expensemanager.provider", (File) this.list.get(i)));
        } else {
            intent.putExtra("android.intent.extra.STREAM", Uri.fromFile((File) this.list.get(i)));
        }
        try {
            this.context.startActivity(Intent.createChooser(intent, "Share File "));
        } catch (ActivityNotFoundException unused) {
            Toast.makeText(this.context, "No app to read File", Toast.LENGTH_LONG).show();
        }
    }


    public void deleteFile(int i) {
        File file = (File) this.list.get(i);
        try {
            if (!file.exists()) {
                return;
            }
            if (file.delete()) {
                this.list.remove(i);
                notifyItemRemoved(i);
                Toast.makeText(this.context, "File deleted.", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this.context, "File can't be deleted.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
