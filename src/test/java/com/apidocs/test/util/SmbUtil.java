package com.apidocs.test.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

import com.lifeix.football.common.util.FileUtil;

import jcifs.UniAddress;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbFileOutputStream;
import jcifs.smb.SmbSession;

/**
 * @author xule
 * @version 2017年4月27日 下午5:36:22
 */
public class SmbUtil {
    
    /**
     * 获取授权
     * @author xule
     * @version 2017年3月20日 上午11:06:49
     * @param
     * @return NtlmPasswordAuthentication
     */
    public static NtlmPasswordAuthentication getAuth() throws Exception {
        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("", "football", "Lifeix2016"); // 先登录验证
        InetAddress ip = InetAddress.getByName("192.168.1.17");
        UniAddress myDomain = new UniAddress(ip);
        SmbSession.logon(myDomain, auth);
        System.setProperty("jcifs.smb.client.dfs.disabled", "true");
        return auth;
    }
    
    public static SmbFile getSmbfile(String smbFilePath) throws Exception{
        return new SmbFile(smbFilePath, getAuth());
    }
    
    /**
     * 创建文件夹
     * @author xule
     * @version 2017年5月5日 上午10:05:01
     * @param 
     * @return SmbFile
     */
    public static SmbFile createDirs(String path) throws Exception{
        SmbFile dir = new SmbFile(path,getAuth());
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }
    
    /**
     * 写入文件
     * @author xule
     * @version 2017年4月27日 下午5:38:14
     * @param 
     * @return void
     * @throws Exception 
     */
    public static void writeFile(String smbFilePath,String content) throws Exception {
        SmbFile smbFile = new SmbFile(smbFilePath, getAuth());
        if (!smbFile.exists()) {
            smbFile.createNewFile();
        }
        OutputStream out = null;
        try {
            out = new BufferedOutputStream(new SmbFileOutputStream(smbFile));
            out.write(content.getBytes());
        } catch (Exception e) {
            throw e;
        } finally {
            if (out!=null) {
                out.close();
            }
        }
    }
    
    /**
     * 读取文件内容
     * @author xule
     * @version 2017年5月2日 上午11:06:10
     * @param 
     * @return String
     */
    public static String readFile(SmbFile smbFile) throws Exception {
        InputStream in=null;
        try {
            in=new BufferedInputStream(new SmbFileInputStream(smbFile));
            return FileUtil.readFileFromStream(in);
        } catch (Exception e) {
            throw e;
        } finally {
            if (in!=null) {
                in.close();
            }
        }
    }
    /**
     * 写入文件
     * @author xule
     * @version 2017年5月5日 下午1:55:52
     * @param 
     * @return void
     */
    public static void writeFile(File source, SmbFile smbFile) {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new BufferedInputStream(new FileInputStream(source));
            out = new BufferedOutputStream(new SmbFileOutputStream(smbFile));
            byte[] buffer = new byte[4096];
            int len = 0; // 读取长度
            while ((len = in.read(buffer, 0, buffer.length)) != -1) {
                out.write(buffer, 0, len);
            }
            out.flush(); // 刷新缓冲的输出流
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
