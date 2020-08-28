package com.example.paybazaarmyket;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.paybazaarmyket.util.IabHelper;


public class PayActivityBazaarMyket extends AppCompatActivity {


    String base64EncodedPublicKey = "MIHNMA0GCSqGSIb3DQEBAQUAA4G7ADCBtwKBrwC8GZE8JaNIkeo7ukmkLZhpS9bYAYepEC5z5cJVDJJvuaAkDbOai+Z+JJ1TgOMuBU46x6Hg22TiFPzwZ7FD4vWXe7bOHjGgxOuA8QTxv6GR+6BcDiKH3W8Umg65DOuE/ngqnYIQj7tA3fOmVKu43ga2PmIi06/mmpIAbZj7R7AuQxMTG3TqZ2JQpUzkiDdYyZ3ZXZnbry9DuoM4VPB/0p6q6JxWW3PxH0CzFkKJo8MCAwEAAQ==";
    IabHelper mHelper;
    private static final String SKU_FULL_VERSION = "FULL_INSTA_FARAN";

    private ProgressBar payProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_bazaar_myket);

        payProgress = findViewById(R.id.payProgress);

        overrideFonts(this, findViewById(android.R.id.content).getRootView());

        mHelper = new IabHelper(this, base64EncodedPublicKey);

        mHelper.startSetup(result -> {
            if (!result.isSuccess()) {
                Toast.makeText(this, "مشکلی پیش آمده,مجدد امتحان کنید", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.backStoreBtn).setOnClickListener(v -> {
            finish();
        });
        findViewById(R.id.payLayoutBtn).setOnClickListener(v -> {
            payProgress.setVisibility(View.VISIBLE);
            goPay();
        });
        findViewById(R.id.cancelBtn).setOnClickListener(v -> {

            finish();
        });

        findViewById(R.id.returnPayBtn).setOnClickListener(v -> {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("برگرداندن خرید");
            alertDialog.setMessage("اگر قبلا پکیج کامل را خریداری کرده اید میتوانید بدون پرداخت هزینه آن را برگردانید. پرداخت های شما بر روی حساب کافه بازارتان ذخیره می شود. پس باید با حسابی که قبلا با آن پکیج را خریده اید در کافه بازار وارد شده باشید. در غیر این صورت برنامه شما را شناسایی نمی کند.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "فهمیدم,خرید را بردگردان",
                    (dialog, which) -> {
                        payProgress.setVisibility(View.VISIBLE);
                        goPay();
                    });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "بازگشت",
                    (dialog, which) -> dialog.dismiss());
            alertDialog.show();
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mHelper.handleActivityResult(requestCode, resultCode, data);
    }

    private void goPay() {

        if (!isCafeInstalled()) {
            Toast.makeText(this, "کافه بازار روی گوشی شما نصب نیست", Toast.LENGTH_LONG).show();
            return;
        }
        mHelper.queryInventoryAsync((result, inventory) -> {
            if (result.isFailure()) {
                Toast.makeText(this, "مشکل پیش آمده, مجدد امتحان کنید", Toast.LENGTH_LONG).show();
                return;
            }
            buyFullVersion();
        });
    }


    private void buyFullVersion() {

        payProgress.setVisibility(View.GONE);

        mHelper.launchPurchaseFlow(this, SKU_FULL_VERSION, 1, (result, info) -> {
            if (result.isFailure()) {
                Toast.makeText(this, "خرید موفقیت آمیز نبود", Toast.LENGTH_SHORT).show();
                return;

            } else if (info.getSku().equals(SKU_FULL_VERSION)) {
                Toast.makeText(this, "با موفقیت انجام شد", Toast.LENGTH_LONG).show();

                //Set Shared Preferences

                finish();
            }
        });
    }


    private boolean isCafeInstalled() {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo("com.farsitel.bazaar", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void overrideFonts(final Context context, final View v) {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    overrideFonts(context, child);
                }
            } else if (v instanceof TextView) {
                ((TextView) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "font/Vazirb.ttf"));
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}