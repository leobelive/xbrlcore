package net.gbicc.xbrl.ent.model;

public class DBConfig {

	/** 数据库驱动 **/
	private String driver;
	/** 数据库连接字符串 **/
	private String url;
	/** 数据库用户名 **/
	private String username;
	/** 数据库密码 **/
	private String password;

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
