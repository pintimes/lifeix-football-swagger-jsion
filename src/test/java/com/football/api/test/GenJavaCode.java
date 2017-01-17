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
		String tempDir = "d:/out/";
		String workspace = "D:/temp";
		File[] files = swaggerDir.listFiles();
		for (File swaggerFile : files) {
			try {
				/**
				 * 生成java代码
				 */
				generateJavaCode(tempDir,workspace,FileUtil.getFileName(swaggerFile));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void generateJavaCode(String tempDir,String workspaceDir,String module) throws IOException {
		String root = tempDir + module;
		File srcDir = new File(root);
		if (srcDir.exists()) {
			srcDir.delete();
		}
		/**
		 * 在临时文件夹创建代码
		 */
		String apiPackage = "com.lifeix.api." + module;
		String modelPackage = "com.lifeix.model." + module;
		File swaggerFile = new File("swagger/" + module + ".json");
		String[] params = new String[] { "generate", "-i", swaggerFile.getAbsolutePath(), "-l", "java", "-o", srcDir.getAbsolutePath(), "--api-package", apiPackage,
				"--model-package", modelPackage };
		SwaggerCodegen.main(params);
		/**
		 * 清除com.lifeix下所有目录
		 */
		String workroot = workspaceDir + "/src/main/java/com/lifeix/";
//		File workspaceFile = new File(workroot);
//		if (workspaceFile.exists()) {
//			workspaceFile.delete();
//		}
//		workspaceFile.mkdirs();
		/**
		 * 将临时文件夹中的API Copy到Workspace Api
		 */
		File api = new File(workroot + "api/" + module + "/");
		if (api.exists()) {
			api.delete();
		}
		api.mkdirs();
		File src = new File(root + "/src/main/java/com/lifeix/api/" + module + "/");
		copyFolder(src, api);
		/**
		 * 将临时文件夹中的Model Copy到Workspace Model
		 */
		File model = new File(workroot + "/model/" + module + "/");
		if (model.exists()) {
			model.delete();
		}
		model.mkdirs();
		src = new File(root + "/src/main/java/com/lifeix/model/" + module + "/");
		copyFolder(src, model);
		System.out.println("Done");
	}

	/**
	 * 复制一个目录及其子目录、文件到另外一个目录
	 * 
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
