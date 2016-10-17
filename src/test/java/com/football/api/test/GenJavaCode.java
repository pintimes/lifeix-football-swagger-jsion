package com.football.api.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.venizeng.swagger.read.FileUtil;

import io.swagger.codegen.SwaggerCodegen;

public class GenJavaCode {

	
	public static void main(String[] args) {
		File swaggerDir = new File("swagger/");
		File[] files = swaggerDir.listFiles();
		for (File swaggerFile : files) {
			try {
				generateCode(FileUtil.getFileName(swaggerFile));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
 	}
	
	private static void generateCode(String module) throws IOException{
		String root = "d:/out/"+module;
		File srcDir = new File(root);
		if (srcDir.exists()) {
			srcDir.delete();
		}
		
		File swaggerFile = new File("swagger/"+module+".json"); 
		SwaggerCodegen.main(new String[] { "generate", "-i", swaggerFile.getAbsolutePath(), "-l", "java", "-o", ""+srcDir.getAbsolutePath() ,"--api-package","com.lifeix.api.controller","--model-package","com.lifeix.api."+module+".model"});

		File controller = new File("D:/WorkSpace/springboot/comment-api-test/src/main/java/com/lifeix/api/"+module+"/controller/");
		if (controller.exists()) {
			controller.delete();
		}
		controller.mkdirs();
		File src =  new File(root+"/src/main/java/com/lifeix/api/controller/");
		copyFolder(src, controller);
		
		File model = new File("D:/WorkSpace/springboot/comment-api-test/src/main/java/com/lifeix/api/"+module+"/model/");
		if (model.exists()) {
			model.delete();
		}
		model.mkdirs();
		src =  new File(root+"/src/main/java/com/lifeix/api/"+module+"/model/");
		copyFolder(src, model);
		System.out.println("Done");
	}
	
	/**
	 * 复制一个目录及其子目录、文件到另外一个目录
	 * @param src
	 * @param dest
	 * @throws IOException
	 */
	private static void copyFolder(File src, File dest) throws IOException {
		if (src.isDirectory()) {
			if (!dest.exists()) {
				dest.mkdir();
			}
			String files[] = src.list();
			for (String file : files) {
				File srcFile = new File(src, file);
				File destFile = new File(dest, file);
				// 递归复制
				copyFolder(srcFile, destFile);
			}
		} else {
			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dest);

			byte[] buffer = new byte[1024];

			int length;
			
			while ((length = in.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}
			in.close();
			out.close();
		}
	}
}
