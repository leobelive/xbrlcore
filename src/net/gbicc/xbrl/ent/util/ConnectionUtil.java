package net.gbicc.xbrl.ent.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import net.gbicc.xbrl.ent.model.DBConfig;

public class ConnectionUtil {
	/**
	 * 获得数据库连接
	 * 
	 * 需要读取配置文件来实现
	 * 
	 * @return
	 */
	public Connection getConnection() {
		try {
			DBConfig dbc = getConfig();
			Class.forName(dbc.getDriver());
			return DriverManager.getConnection(dbc.getUrl(), dbc.getUsername(),
					dbc.getPassword());
		} catch (ClassNotFoundException clfex) {
			clfex.printStackTrace();
			return null;
		} catch (SQLException sqlex) {
			sqlex.printStackTrace();
			return null;
		} catch (IOException ioex) {
			ioex.printStackTrace();
			return null;
		}
	}

	/**
	 * 通过配置文件读取数据库配置信息
	 * 
	 * @return
	 */
	private DBConfig getConfig() throws IOException {
		DBConfig dbc = new DBConfig();
		// 生成配置文件的路径
		String rootPath = this.getClass().getResource("").getPath();
		rootPath = rootPath.substring(0, rootPath.indexOf("classes") + 8);
		String pPath = rootPath + "configuration/" + "jdbc.properties";
		Properties p = new Properties();
		p.load(new FileInputStream(new File(pPath)));
		dbc.setDriver(p.getProperty("driver"));
		dbc.setUrl(p.getProperty("url"));
		dbc.setUsername(p.getProperty("username"));
		dbc.setPassword(p.getProperty("password"));
		return dbc;
	}
}
