package com.venizeng.swagger.read;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileUtil {
	
	/**
	 * 
	 * @param filepath   xx.text
	 * @return
	 */
	public static String readFileContent(String filepath) {
		try {
			InputStream is = new FileInputStream(filepath);
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			StringBuilder stringBuilder = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				stringBuilder.append(line);
			}
			br.close();
			return stringBuilder.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param filename   xx.text
	 * @return
	 */
	public static String readFileFromResource(String filename) {
		try {
			InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			StringBuilder stringBuilder = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				stringBuilder.append(line);
			}
			br.close();
			return stringBuilder.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 获得文件的名称
	 * @param filename
	 * @return
	 */
	public static String getFileName(File file){
		if (file.isDirectory()) {
			return file.getName();
		}
		String name = file.getName();
		return name.substring(0,name.indexOf("."));
	}

	public static void writeContent(String filepath, String content) {
		try {
			File file = new File(filepath);
			file.delete();
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			DataOutputStream dos = new DataOutputStream(fos);
			dos.writeUTF(content);
			dos.close();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
