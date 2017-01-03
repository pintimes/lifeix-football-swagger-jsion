package com.football.api.test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

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

	private static String SMBDIR = "smb://192.168.50.199/立方网/cfootball/football-api/";
	// private static String SMBDIR = "smb://root:Lifeix2016@192.168.1.18";
	private NtlmPasswordAuthentication auth = null;

	public static void main(String[] args) throws Exception {
		GenerateRemotes generateRemotes = new GenerateRemotes();
		generateRemotes.init();
		generateRemotes.generateRemote(new File("swagger/comment.json"));
		// generateAll();
	}

	private void init() throws Exception {
		
		auth = new NtlmPasswordAuthentication("192.168.50.199", "lifeix", "lifeix"); // 先登录验证
		InetAddress ip = InetAddress.getByName("192.168.50.199");
		UniAddress myDomain = new UniAddress(ip);
		SmbSession.logon(myDomain, auth);
	}

	private void generateAll() {
		File swaggerDir = new File("swagger/");
		File[] files = swaggerDir.listFiles();
		for (File swaggerFile : files) {
			generateRemote(swaggerFile);
		}
	}

	private void generateRemote(File swaggerFile) {
		String fileName = FileUtil.getFileName(swaggerFile);
		/**
		 * 生成html
		 */
		String htmlRoot = "d:/out/" + fileName;
		SwaggerCodegen.main(new String[] { "generate", "-i", swaggerFile.getAbsolutePath(), "-l", "html", "-o", "d:/out/" + fileName });
		/**
		 * 删除remote文件夹
		 */
		String remoteDir = SMBDIR + fileName + "/";
		deleteSMB(remoteDir);
		/**
		 * 写入html
		 */
		writeSMB(remoteDir, new File(htmlRoot + "/index.html"));
		/**
		 * 写入swagger
		 */
		writeSMB(remoteDir, swaggerFile);
	}

	private void deleteSMB(String dirpath) {
		try {
			SmbFile remoteFile = getRemoteFile(dirpath);
			remoteFile.connect(); // 尝试连接

			SmbFile[] files = remoteFile.listFiles();
			for (SmbFile smbFile : files) {
				smbFile.delete();
			}
		} catch (Exception e) {
			String msg = "发生错误：" + e.getLocalizedMessage();
			System.out.println(msg);
		}
	}

	private void writeSMB(String dirpath, File file) {
		InputStream in = null;
		OutputStream out = null;
		try {
			System.out.println(dirpath);
			SmbFile dir = getRemoteFile(dirpath);
			if (!dir.exists()) {
				dir.mkdir();
			}

			SmbFile remoteFile = new SmbFile(dirpath + file.getName());
			System.out.println("remoteFilepath ---> " + remoteFile.getPath());
			remoteFile.connect(); // 尝试连接

			in = new BufferedInputStream(new FileInputStream(file));
			out = new BufferedOutputStream(new SmbFileOutputStream(remoteFile));

			byte[] buffer = new byte[4096];
			int len = 0; // 读取长度
			while ((len = in.read(buffer, 0, buffer.length)) != -1) {
				out.write(buffer, 0, len);
			}
			out.flush(); // 刷新缓冲的输出流
		} catch (Exception e) {
			String msg = "发生错误：" + e.getLocalizedMessage();
			System.out.println(msg);
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (Exception e) {
			}
		}
	}

	private SmbFile getRemoteFile(String filepath) throws Exception {
		SmbFile smbFile = new SmbFile(filepath, auth);
		return smbFile;
	}

}