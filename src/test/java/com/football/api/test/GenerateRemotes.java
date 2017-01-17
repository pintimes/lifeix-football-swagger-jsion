package com.football.api.test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;

import com.venizeng.swagger.read.FileUtil;

import io.swagger.codegen.SwaggerCodegen;
import jcifs.UniAddress;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;
import jcifs.smb.SmbSession;

/**
 * java读取共享文件夹 参考 http://blog.csdn.net/zht666/article/details/27079995
 * 
 * @author zengguangwei
 *
 */
public class GenerateRemotes {

	// private static String SMBDIR =
	// "smb://192.168.50.199/立方网/cfootball/football-api/";
	private static String SMBDIR = "smb://192.168.1.17/fb/api/";

<<<<<<< HEAD
    public static void main(String[] args) {
        generateRemote(new File("swagger/user.json"));
         generateAll();
    }
=======
	public static void main(String[] args) throws Exception {
		GenerateRemotes generateRemotes = new GenerateRemotes();
		generateRemotes.process();
	}
>>>>>>> refs/remotes/origin/master

	private void process() throws Exception {
		/**
		 * 登录
		 */
		NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("", "football", "Lifeix2016"); // 先登录验证
		InetAddress ip = InetAddress.getByName("192.168.1.17");
		UniAddress myDomain = new UniAddress(ip);
		SmbSession.logon(myDomain, auth);
		System.setProperty("jcifs.smb.client.dfs.disabled", "true");

		File swaggerDir = new File("swagger/");
		File[] files = swaggerDir.listFiles();
		for (File swaggerFile : files) {
			String fileName = FileUtil.getFileName(swaggerFile);
			try {
				process(auth, fileName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void process(NtlmPasswordAuthentication auth, String name) throws Exception {
		SmbFile dir = new SmbFile(SMBDIR + name + "/", auth);
		if (!dir.exists()) {
			dir.mkdir();
		}
		// 生成Swagger
		File swaggerFile = new File("swagger/" + name + ".json");
		SmbFile json = new SmbFile(SMBDIR + name + "/" + name + ".json", auth);
		writeFile(swaggerFile, json);
		// 生成Html1
		String tempFileDir = "d:/temp4/html/" + name + "/";
		SwaggerCodegen.main(new String[] { "generate", "-i", swaggerFile.getAbsolutePath(), "-l", "html", "-o", tempFileDir });
		SmbFile html = new SmbFile(SMBDIR + name + "/index.html", auth);
		writeFile(new File(tempFileDir + "index.html"), html);
		// 生成Html2
		String tempFileDir2 = "d:/temp4/html2/" + name + "/";
		SwaggerCodegen.main(new String[] { "generate", "-i", swaggerFile.getAbsolutePath(), "-l", "html2", "-o", tempFileDir2 });
		SmbFile html2 = new SmbFile(SMBDIR + name + "/index2.html", auth);
		writeFile(new File(tempFileDir2 + "index.html"), html2);
	}

	private void writeFile(File source, SmbFile smbFile) {
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