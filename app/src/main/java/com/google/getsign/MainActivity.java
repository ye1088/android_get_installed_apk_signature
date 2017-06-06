package com.google.getsign;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.MultiAutoCompleteTextView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.utils.GetApkSignatureByUninstall;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int EDITTEXT_ON_FOCUS = 0;
    MultiAutoCompleteTextView packageName_et ;
    Button selectApk_bt ;
    Button getSign_bt ;
    Button copy_sign_bt ;
    TextView sign_tv ;
    AlertDialog.Builder build;
    MyAdapter myAdapter;
    String[] sign_type;
    List<ApplicationInfo> apps;

   Handler handler = new Handler(){
       @Override
       public void handleMessage(Message msg) {
           switch (msg.what){
               case EDITTEXT_ON_FOCUS:
                   String packageName = packageName_et.getText().toString();
                   if (packageName.contains(",")){

                       packageName_et.setText(packageName.substring(0,packageName.lastIndexOf(",")));
                   }
                   Log.e("MainActivity","handler zhong ");
                   handler.sendEmptyMessageDelayed(EDITTEXT_ON_FOCUS,1000);
                   break;
           }
       }
   };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

    }




    private void init() {
        packageName_et = (MultiAutoCompleteTextView) findViewById(R.id.pack_name_et);
        selectApk_bt = (Button) findViewById(R.id.selectApk);
        getSign_bt = (Button) findViewById(R.id.getSign);
        copy_sign_bt = (Button) findViewById(R.id.copy_sign);
        sign_tv = (TextView) findViewById(R.id.signTxt);

        build = new AlertDialog.Builder(this);
        build.setTitle("选择查看的apk");
        PackageManager packageManager = this.getPackageManager();
        apps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

        myAdapter = new MyAdapter(apps, this,packageManager);

        packageName_et.setAdapter(new ArrayAdapter<String>(this,R.layout.auto_complete,getAllPackageName(apps)));
        packageName_et.setThreshold(2);
        packageName_et.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        packageName_et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){

                    handler.sendEmptyMessage(EDITTEXT_ON_FOCUS);
                }else {
                    removeHandlerMessage();
                }
            }
        });



        selectApk_bt.setOnClickListener(this);
        getSign_bt.setOnClickListener(this);
        sign_tv.setOnClickListener(this);
        copy_sign_bt.setOnClickListener(this);


        build.setIcon(R.mipmap.ic_launcher);

        sign_type = new String[]{"MD5短签名","链式长签名","charArray式"};

    }

    private ArrayList getAllPackageName(List<ApplicationInfo> apps) {
        ArrayList<String> list = new ArrayList<>();
        for (ApplicationInfo info :
                apps) {
            list.add(info.packageName);
        }
        return list;
    }


    @Override
    protected void onStop() {
        removeHandlerMessage();
        super.onStop();
    }

    @Override
    protected void onDestroy() {

        removeHandlerMessage();
        super.onDestroy();
    }

    private void removeHandlerMessage() {
        handler.removeCallbacksAndMessages(null);

    }

    @Override
    protected void onStart() {
        super.onStart();
        handler.sendEmptyMessage(EDITTEXT_ON_FOCUS);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.pack_name_et:
//                packageName_et.setText("");
                break;
            case R.id.selectApk:

                selectApk();

                break;
            case R.id.getSign:

                String packageName = packageName_et.getText().toString();
                if (packageName.contains(",")){
                    packageName = packageName.substring(0,packageName.lastIndexOf(","));
                }
                getSign(packageName);
                break;
            case R.id.signTxt:

                break;
            case R.id.copy_sign:
                copySign();
                break;
            default:
                break;

        }
    }

    private void selectApk() {

        build.setAdapter(myAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                packageName_et.setText(apps.get(which).packageName);
                dialog.dismiss();
            }
        });
        build.show();
    }

    private void copySign() {
        ClipboardManager manager = (ClipboardManager) MainActivity.this.getSystemService(Activity.CLIPBOARD_SERVICE);
        manager.setText(sign_tv.getText().toString().trim());
        Toast.makeText(this, R.string.copy_finish, Toast.LENGTH_SHORT).show();
    }

    private void getSign(final String packageName) {
        build.setTitle("选择签名类型");
        build.setAdapter(null,null);

        build.setItems(sign_type, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Signature[] rawSignatures = MainActivity.this.getRawSignature(MainActivity.this, packageName);
                if(rawSignatures == null || rawSignatures.length == 0) {
                    MainActivity.this.errout("signs is null");
                }
                else {
                    copy_sign_bt.setVisibility(View.VISIBLE);
                    int signature_len = rawSignatures.length;
                    switch (which){
                        case 0:
                            for(int i = 0; i < signature_len; ++i) {

                                errout(MD5.getMessageDigest(rawSignatures[i].toByteArray()));
                            }
                            break;
                        default:
                        case 1:
                            errout(toCharsString(rawSignatures[0].toByteArray()));
                            break;
                        case 2:
                            System.out.println(rawSignatures[0].toByteArray().toString());

                            break;
                    }

                }
                dialog.dismiss();
            }

        });
        build.show();

    }

    /**
     * 将签名转成转成可见字符串
     * 这个其实就是讲
     * @param sigBytes
     * @return
     */
    private static String toCharsString(byte[] sigBytes) {
        byte[] sig = sigBytes;
        final int N = sig.length;
        final int N2 = N * 2;
        char[] text = new char[N2];
        for (int j = 0; j < N; j++) {
            byte v = sig[j];
            int d = (v >> 4) & 0xf;
            text[j * 2] = (char) (d >= 10 ? ('a' + d - 10) : ('0' + d));
            d = v & 0xf;
            text[j * 2 + 1] = (char) (d >= 10 ? ('a' + d - 10) : ('0' + d));
        }
        return new String(text);
    }

    private Signature[] getRawSignature(Activity context, String packageName) {

        PackageInfo pm_info;
        Signature[] signatures = null;
        if(packageName != null && packageName.length() != 0) {
            PackageManager manager = context.getPackageManager();
            try {
                pm_info = manager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
                signatures = pm_info.signatures;
                return signatures;
            }catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                copy_sign_bt.setVisibility(View.INVISIBLE);
                this.errout("NameNotFoundException");
            }


        }
        else {
            copy_sign_bt.setVisibility(View.INVISIBLE);
            this.errout("getSignature, packageName is null");
        }

        return null;
    }


    private void errout(String nameNotFoundException) {
        Log.e("MainActivity","MainActivity Log : "+nameNotFoundException);
        sign_tv.setText(nameNotFoundException);
    }


}
