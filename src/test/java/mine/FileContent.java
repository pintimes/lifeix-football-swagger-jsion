package mine;

/**
 * @author xule
 * @version 2017年3月29日 下午3:00:34
 */
public class FileContent {
	private String theme="";
	private String theme_lowercase="";
	private String fileName="";
	private String content="";
	private String urlMap="";
	private String functions="";
	
	public String toString() {
		this.urlMap+="\r\n\t}";
		this.content+=this.urlMap+this.functions+"\r\n}";
		return this.content;
	}

	public FileContent(String theme,String theme_lowercase) {
		super();
		this.theme=theme;
		this.fileName=theme+"MonitorService";
		this.theme_lowercase=theme_lowercase;
		this.content="import java.util.HashMap;"
				+ "\r\nimport java.util.List;"
				+ "\r\nimport java.util.Map;"
				+ "\r\nimport org.slf4j.Logger;"
				+ "\r\nimport org.slf4j.LoggerFactory;"
				+ "\r\nimport com.alibaba.fastjson.JSONObject;"
				+ "\r\n\r\npublic class "+this.fileName+" extends BaseMonitor{"
				+ "\r\n\r\n\tprivate Logger logger = LoggerFactory.getLogger(getClass());"
				+ "\r\n\r\n\tprivate "+theme+"Checker "+theme_lowercase+"Checker=new "+theme+"Checker();";//要写入文件的内容
		this.urlMap="\r\n\r\n\tprivate static Map<String,String> urlMap=new HashMap<>();\r\n\r\n\tpublic void init(){"
				+ "\r\n\t\tString root = appConfig.getApihost()+\"/football\";";
		this.functions="";
	}
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getTheme_lowercase() {
		return theme_lowercase;
	}
	public void setTheme_lowercase(String theme_lowercase) {
		this.theme_lowercase = theme_lowercase;
	}
	public String getTheme() {
		return theme;
	}
	public void setTheme(String theme) {
		this.theme = theme;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getUrlMap() {
		return urlMap;
	}
	public void setUrlMap(String urlMap) {
		this.urlMap = urlMap;
	}
	public String getFunctions() {
		return functions;
	}
	public void setFunctions(String functions) {
		this.functions = functions;
	}
	
}
