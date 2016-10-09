package com.lifeix.swagger.test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Iterator;

import com.venizeng.swagger.read.CombineJsons;
import com.venizeng.swagger.read.FileUtil;

import aQute.lib.io.IO;
import io.swagger.codegen.SwaggerCodegen;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;

public class Footaball {

	public static void main(String[] args) throws Exception {
		File rootDir = new File("src/main/resources/");
		File[] files = rootDir.listFiles();
		for (File temp : files) {
			String root = temp.getAbsolutePath();
			try {
				doSomething(root + File.separator, temp.getName());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		File swaggerDir = new File("swagger/");
		files = swaggerDir.listFiles();

		String root = "smb://lifeix:lifeix@192.168.50.199/立方网/cfootball/football-api/";
		deleteSMB(root);
		for (File swaggerFile : files) {
			String fileName = FileUtil.getFileName(swaggerFile);

			/**
			 * 生成html
			 */
			String htmlRoot = "d:/out/" + fileName;
			SwaggerCodegen.main(new String[] { "generate", "-i", swaggerFile.getAbsolutePath(), "-l", "html2", "-o", "d:/out/" + fileName });
			
			String remoteDir = root + fileName;
			/**
			 * 写入html
			 */
			writeSMB(remoteDir, new File(htmlRoot + "/index.html"));
			/**
			 * 写入swagger
			 */
			writeSMB(remoteDir, swaggerFile);
		}
	}

	private static void deleteSMB(String dirpath) {
		try {
			SmbFile remoteFile = new SmbFile(dirpath);
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

	private static void writeSMB(String dirpath, File file) {
		InputStream in = null;
		OutputStream out = null;
		try {
			System.out.println(dirpath);
			SmbFile dir = new SmbFile(dirpath);
			if (!dir.exists()) {
				dir.mkdir();
			}
			
			SmbFile remoteFile = new SmbFile(dirpath + "/" + file.getName());
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

	private static void doSomething(String root, String name) throws FileNotFoundException {
		File swaggerFile = new File(root + "app/swagger.json");
		File pathRoot = new File(root + "app/paths");
		File definationRoot = new File(root + "definitions");
		String json = new CombineJsons().toSwaggerJson(swaggerFile, definationRoot, pathRoot);
		String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
		path = path.substring(0, path.indexOf("target"));
		File dir = new File(path + "/swagger/");
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File file = new File(dir.getAbsolutePath() + File.separator + name + ".json");
		PrintWriter out = new PrintWriter(new FileOutputStream(file));
		out.println(json);
		out.close();
	}

}
