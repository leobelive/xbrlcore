package net.gbicc.xbrl.ent.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionUtil {
	/**
	 * 获得数据库连接
	 * 
	 * 需要读取配置文件来实现
	 * 
	 * @return
	 */
	public static Connection getConnection() {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			return DriverManager.getConnection(
					"jdbc:oracle:thin:@WIN-87HT1S3JKHF:1521:xbrlbs", "xbrlssf",
					"xbrlssf");
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
			return null;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return null;
		}
	}
}
