package com.google.utils;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by admin on 2017/6/6.
 */

public class GetApkSignatureByUninstall {

    public static boolean copyAssetsApk2Sdcard(Context context){

        AssetManager manager = context.getAssets();
        try {
            if(!SUtils.hasSomeFileInAssetsFileNames("sign_apk.apk",context)){
                   return false;
            }
            InputStream open = manager.open("sign_apk.apk");

            SUtils.copyFile2where(open,"/sdcard/sign_apk.apk");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }


        return true;
    }

    /**
     *  获取 apk 签名 通过 以字节数组的形式出来
     * @param apkPath
     * @return
     * @throws IOException
     * @throws CertificateEncodingException
     */
    public static byte[] getApkSignByCharArray(String apkPath){
        File file = new File(apkPath);


        JarFile jarFile = null;
        try {
            jarFile = new JarFile(file);
            JarEntry je = jarFile.getJarEntry("AndroidManifest.xml");
            byte[] readBuffer = new byte[8192];
            Certificate[] certs = loadCertificates(jarFile, je, readBuffer);
            if (certs != null) {
                for (Certificate c : certs) {
                    return c.getEncoded();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }


    /**
     *  获取 apk 签名 将字节数组以字符创的形式输出
     * @param apkPath
     * @return
     * @throws IOException
     * @throws CertificateEncodingException
     */
    public static String getApkSignByString(String apkPath){
        File file = new File(apkPath);
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(file);
            JarEntry je = jarFile.getJarEntry("AndroidManifest.xml");
            byte[] readBuffer = new byte[8192];
            Certificate[] certs = loadCertificates(jarFile, je, readBuffer);
            if (certs != null) {
                for (Certificate c : certs) {
                    return toCharsString(c.getEncoded());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }



    /**
     * 将签名转成转成可见字符串
     *
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


    /**
     * 加载签名
     *
     * @param jarFile
     * @param je
     * @param readBuffer
     * @return
     */
    private static Certificate[] loadCertificates(JarFile jarFile, JarEntry je,
                                                  byte[] readBuffer) {
        try {
            InputStream is = jarFile.getInputStream(je);
            while (is.read(readBuffer, 0, readBuffer.length) != -1) {
            }
            is.close();
            return je != null ? je.getCertificates() : null;
        } catch (IOException e) {
        }
        return null;
    }
}
