package com.apidocs.test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
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
     * 写入文件
     * @author xule
     * @version 2017年4月27日 下午5:38:14
     * @param 
     * @return void
     * @throws Exception 
     */
    public static void writeFile(String smbFilePath,String content) throws Exception {
        SmbFile smbFile = new SmbFile(smbFilePath, getAuth());
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
}
