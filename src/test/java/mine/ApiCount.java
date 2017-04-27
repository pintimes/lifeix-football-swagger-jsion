package mine;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.json.JSONObject;
import org.springframework.util.CollectionUtils;
import jcifs.UniAddress;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbSession;

/**
 * @author xule
 * @version 2017年3月20日 上午10:59:53
 */
public class ApiCount {
    private static String SMBDIR = "smb://192.168.1.17/fb/api/";

    private static Set<String> exclude = new HashSet<>(Arrays.asList(
            "smb://192.168.1.17/fb/api/tytt/tytt.json", "smb://192.168.1.17/fb/api/cbs/cbs.json",
            "smb://192.168.1.17/fb/api/football/football.json"));

    public static void main(String[] args) {
        try {
            NtlmPasswordAuthentication auth = getAuth();
            List<String> filesPath = getAllJsonFilesPath(SMBDIR, auth);
            int count = 0;
            String result = "";
            for (String filePath : filesPath) {
                if (exclude.contains(filePath)) {
                    continue;
                }
                String text = readFile(new SmbFile(filePath, auth));
                JSONObject jsonObject = new JSONObject(text);
                JSONObject pathsJson = jsonObject.getJSONObject("paths");
                int tempCount = 0;
                for (String key : pathsJson.keySet()) {
                    JSONObject methodsJson = pathsJson.getJSONObject(key);
                    Set<String> keySet2 = methodsJson.keySet();
                    tempCount += keySet2.size();
                }
                count += tempCount;
                result += tempCount + "\t" + filePath + "\n";
            }
            System.out.println("api总数：" + count);
            System.out.println(result);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
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
}
