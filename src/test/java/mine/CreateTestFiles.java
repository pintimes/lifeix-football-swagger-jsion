package mine;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.JSONObject;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import jcifs.UniAddress;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbSession;

/**
 * @author xule
 * @version 2017年3月28日 上午9:56:25
 */
public class CreateTestFiles {
    private static String SMBDIR = "smb://192.168.1.17/fb/api/";

    private static Set<String> include = new HashSet<>(
            Arrays.asList("wemedia/wemedia.json", "user/user.json"));

    public static void main(String[] args) {
        try {
            NtlmPasswordAuthentication auth = getAuth();
            List<String> filesPath = getAllJsonFilesPath(SMBDIR, auth);
            /**
             * 遍历所有include中包含的json文件并解析
             */
            for (String filePath : filesPath) {
                if (!include.contains(filePath.replace(SMBDIR, ""))) {
                    continue;
                }
                /**
                 * 创建文件map集合
                 */
                Map<String, FileContent> fileMap = createFileMap(filePath, auth);
                /**
                 * 生成文件
                 */
                Set<String> themes = fileMap.keySet();
                /**
                 * 为每一个主题生成monitor文件和checker文件
                 */
                for (String theme : themes) {
                    File file = new File("C:\\Users\\dd\\Desktop\\ApiTestFiles\\" + theme);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    /**
                     * 生成monitor文件
                     */
                    createMonitorFile(file, fileMap.get(theme));
                    /**
                     * 生成checker文件
                     */
                    createCheckerFile(file, theme);
                }

            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void createMonitorFile(File file, FileContent fileContent) {
        String fileName = fileContent.getFileName() + ".java";
        writeContent(file.getPath() + "\\" + fileName, fileContent.toString());
        System.out.println(fileName + "创建成功！");
    }

    private static void createCheckerFile(File file, String theme) {
        String checkerFileContent = "import java.util.List;" + "\r\n\r\npublic class " + theme
                + "Checker extends GenericChecker{" + "\r\n\r\n\t@Override"
                + "\r\n\tpublic void checkHttpCode(String code) throws Exception {"
                + "\r\n\t\tsuper.checkHttpCode(code);" + "\r\n\t\t//TODO 详细检测" + "\r\n\t}"
                + "\r\n\r\n\t@Override" + "\r\n\tpublic <T> void checkList(List<T> list) throws Exception {"
                + "\r\n\t\tsuper.checkList(list);" + "\r\n\t\t//TODO 详细检测" + "\r\n\t}" + "\r\n\r\n\t@Override"
                + "\r\n\tpublic <T> void checkSingle(T obj) throws Exception {"
                + "\r\n\t\tsuper.checkSingle(obj);" + "\r\n\t\t//TODO 详细检测" + "\r\n\t}\r\n\r\n}";
        String checkerFileName = theme + "Checker.java";
        writeContent(file.getPath() + "\\" + checkerFileName, checkerFileContent);
        System.out.println(checkerFileName + "创建成功！");
    }

    private static Map<String, FileContent> createFileMap(String filePath, NtlmPasswordAuthentication auth)
            throws MalformedURLException {
        Map<String, FileContent> fileMap = new HashMap<>();
        /**
         * 读取json文件内容
         */
        String text = readFile(new SmbFile(filePath, auth));
        /**
         * 获得api文档json对象
         */
        JSONObject jsonObject = new JSONObject(text);
        /**
         * 获得api文档中所有path
         */
        JSONObject pathsJson = jsonObject.getJSONObject("paths");
        /**
         * 遍历所有path
         */
        for (String path : pathsJson.keySet()) {
            String theme_lowercase = getThemeLowercase(path);
            String theme = firstUpcase(theme_lowercase);
            FileContent fileContent = fileMap.get(theme);
            if (fileContent == null) {
                fileContent = new FileContent(theme, theme_lowercase);
                fileMap.put(theme, fileContent);
            }

            JSONObject methodsJson = pathsJson.getJSONObject(path);
            Set<String> methods = methodsJson.keySet();
            /**
             * 遍历path对应的所有方法(get/post/put/...)
             */
            for (String method : methods) {
                JSONObject methodJsonObj = methodsJson.getJSONObject(method);
                String operationId = methodJsonObj.getString("operationId");
                fileContent.setUrlMap(fileContent.getUrlMap() + "\r\n\t\turlMap.put(\"" + operationId
                        + "Url\",root+\"" + path + "\");");
                fileContent.setFunctions(fileContent.getFunctions() + "\r\n\r\n\t/**\r\n\t * "
                        + methodJsonObj.getString("description") + "\r\n\t */" + "\r\n\tpublic void "
                        + operationId + "() throws Exception {" + "\r\n\t\tString url=urlMap.get(\""
                        + operationId + "Url\");" + "\r\n\t\t//TODO 发送" + method + "请求\r\n\t}");
            }
            fileMap.put(theme, fileContent);
        }
        return fileMap;
    }

    private static String getThemeLowercase(String path) {
        String[] split = path.split("/");
        String theme = split[2];
        if (theme.endsWith("s")) {
            theme = theme.substring(0, theme.length() - 1);
        }
        return theme;
    }

    /**
     * 获得文件夹下所有json文件列表
     * 
     * @author xule
     * @version 2017年3月20日 上午11:14:01
     * @param
     * @return List<String>
     * @throws Exception
     */
    private static List<String> getAllJsonFilesPath(String path, NtlmPasswordAuthentication auth)
            throws Exception {
        try {
            SmbFile smbFile = new SmbFile(path, auth);
            if (!smbFile.isDirectory()) {
                if (path.endsWith(".json")) {
                    return new ArrayList<>(Arrays.asList(path));
                }
                return null;
            }
            if (!path.endsWith("/")) {
                path += "/";
            }
            smbFile = new SmbFile(path, auth);
            SmbFile[] listFiles = smbFile.listFiles();
            List<String> filesPath = new ArrayList<>();
            for (SmbFile smbFile2 : listFiles) {
                String path2 = smbFile2.getPath();
                if (!smbFile2.isDirectory() && path2.endsWith(".json")) {
                    filesPath.add(path2);
                    continue;
                }
                List<String> filesPath2 = getAllJsonFilesPath(path2, auth);
                if (!CollectionUtils.isEmpty(filesPath2)) {
                    filesPath.addAll(filesPath2);
                }

            }
            return filesPath;
        } catch (Exception e) {
            throw new Exception("获取json文件列表失败，异常信息：" + e.getMessage());
        }
    }

    /**
     * 获取授权
     * 
     * @author xule
     * @version 2017年3月20日 上午11:06:49
     * @param
     * @return NtlmPasswordAuthentication
     */
    private static NtlmPasswordAuthentication getAuth() throws Exception {
        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("", "football", "Lifeix2016"); // 先登录验证
        InetAddress ip = InetAddress.getByName("192.168.1.17");
        UniAddress myDomain = new UniAddress(ip);
        SmbSession.logon(myDomain, auth);
        System.setProperty("jcifs.smb.client.dfs.disabled", "true");
        return auth;
    }

    /**************************************************************************************************************************************/
    /**
     * 读取文件
     * 
     * @author xule
     * @version 2017年3月20日 上午11:10:22
     * @param
     * @return String
     */
    private static String readFile(SmbFile smbFile) {
        InputStream is = null;
        BufferedReader br = null;
        try {
            is = new BufferedInputStream(new SmbFileInputStream(smbFile));
            br = new BufferedReader(new InputStreamReader(is));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line);
            }
            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (br != null) {
                    br.close();
                }
            } catch (Exception e2) {
            }
        }
        return null;
    }

    /**
     * 写入文件
     */
    private static void writeContent(String filepath, String content) {
        if (StringUtils.isEmpty(content)) {
            return;
        }
        writeContent(filepath, content.getBytes());
    }

    private static void writeContent(String filepath, byte[] datas) {
        FileOutputStream fos = null;
        try {
            File file = new File(filepath);
            file.delete();
            file.createNewFile();
            fos = new FileOutputStream(file);
            fos.write(datas);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String firstUpcase(String name) {
        if (StringUtils.isEmpty(name)) {
            return name;
        }
        String first = name.substring(0, 1);
        return name.replaceFirst(first, first.toUpperCase());
    }

}
