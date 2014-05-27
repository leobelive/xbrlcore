package net.gbicc.xbrl.ent.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.gbicc.xbrl.ent.model.BasicInfo;
import net.gbicc.xbrl.ent.model.RelationMapping;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SqlUtils {

	private static final Log log = LogFactory.getLog(SqlUtils.class);

	private static final ConnectionUtil connectionUtil = new ConnectionUtil();

	/**
	 * 读取映射配置信息表中的所有表
	 * 
	 * @return
	 */
	public static List<RelationMapping> getMappingRelation(String tablename) {
		Connection conn = null;
		Statement st = null;
		List<RelationMapping> rms = new ArrayList<RelationMapping>();
		try {
			conn = connectionUtil.getConnection();
			st = conn.createStatement();
			String sql = "select ELEMENTNAME, FIELDNAME, TABLENAME, "
					+ "TYPE, PARTEN from TD_ELEMENT_COLUMN_RELATION where tablename = '"
					+ tablename + "'";
			ResultSet rs = st.executeQuery(sql);
			while (rs.next()) {
				RelationMapping rm = new RelationMapping();
				rm.setElementName(rs.getString("ELEMENTNAME"));
				rm.setFieldName(rs.getString("FIELDNAME"));
				rm.setTableName(rs.getString("TABLENAME"));
				rm.setDataType(rs.getString("TYPE"));
				rm.setParten(rs.getString("PARTEN"));
				rms.add(rm);
			}
			return rms;
		} catch (SQLException sqlex) {
			log.info(sqlex.getMessage());
			return null;
		} finally {
			try {
				if (st != null) {
					st.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException sqlex) {
				// e.printStackTrace();
				log.info(sqlex.getMessage());
			}
		}
	}

	/**
	 * 读取映射配置信息表中的所有表
	 * 
	 * @return
	 */
	public static List<String> getTableNames() {
		Connection conn = null;
		Statement st = null;
		List<String> columns = new ArrayList<String>();
		try {
			conn = connectionUtil.getConnection();
			st = conn.createStatement();
			String sql = "select distinct (TABLENAME) from TD_ELEMENT_COLUMN_RELATION where type = 1";
			ResultSet rs = st.executeQuery(sql);
			while (rs.next()) {
				columns.add(rs.getString("TABLENAME"));
			}
			return columns;
		} catch (SQLException sqlex) {
			log.info(sqlex.getMessage());
			return null;
		} finally {
			try {
				if (st != null) {
					st.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException sqlex) {
				// e.printStackTrace();
				log.info(sqlex.getMessage());
			}
		}
	}

	/**
	 * 查找表名下对应 的元素和字段
	 * 
	 * @param name
	 * @param type
	 * @return
	 */
	public static List<BasicInfo> getBasicInfos(String name, String type) {
		List<BasicInfo> list = new LinkedList<BasicInfo>();
		Connection conn = null;
		Statement st = null;
		String sql = "select elementName,fieldName ,tableName from "
				+ "TD_RELATION t where t.tablename in("
				+ "select tableName from"
				+ " TD_RELATION  where elementName = '" + name
				+ "') and fieldname is not null and type ='" + type + "'";
		try {
			conn = connectionUtil.getConnection();
			st = conn.createStatement();
			ResultSet rs = st.executeQuery(sql);
			while (rs.next()) {
				BasicInfo basicInfo = new BasicInfo();
				basicInfo.setElementName(rs.getString("elementName"));
				basicInfo.setFieldName(rs.getString("fieldName"));
				basicInfo.setTableName(rs.getString("tableName"));
				list.add(basicInfo);
			}
			return list;
		} catch (Exception e) {
			// e.printStackTrace();
			return null;
		} finally {
			try {
				if (st != null) {
					st.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 执行SQL
	 * 
	 * @param sql
	 * @return
	 */
	public static boolean executeSql(String sql) {
		boolean flag = false;
		Connection conn = null;
		Statement st = null;
		try {
			conn = connectionUtil.getConnection();
			st = conn.createStatement();
			ResultSet rs = st.executeQuery(sql);
			flag = rs.next();
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		} finally {
			try {
				if (st != null) {
					st.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return flag;
	}

	/**
	 * 以类型为条件，查询基础信息
	 * 
	 * @param type
	 * @return
	 */
	public static List<String> getBasicInfoByType(String type) {
		List<String> list = new ArrayList<String>();
		Connection conn = null;
		Statement st = null;
		String sql = "select distinct(tableName) from TD_ELEMENT_COLUMN_RELATION t"
				+ " where t.type = '" + type + "' and t.fieldname is null";
		try {
			conn = connectionUtil.getConnection();
			st = conn.createStatement();
			ResultSet rs = st.executeQuery(sql);
			while (rs.next()) {
				list.add(rs.getString("tableName"));
			}
			return list;
		} catch (Exception e) {
			// e.printStackTrace();
			return null;
		} finally {
			try {
				if (st != null) {
					st.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static List<BasicInfo> getBasicInfosItem(String tableName,
			String parten) {
		Connection conn = null;
		Statement st = null;
		List<BasicInfo> list = new LinkedList<BasicInfo>();
		String sql = "select elementName,fieldName ,tableName from "
				+ "TD_ELEMENT_COLUMN_RELATION t where t.tablename ='"
				+ tableName + "' and t.parten ='" + parten + "'";
		try {
			conn = connectionUtil.getConnection();
			st = conn.createStatement();
			ResultSet rs = st.executeQuery(sql);
			while (rs.next()) {
				BasicInfo basicInfo = new BasicInfo();
				basicInfo.setElementName(rs.getString("elementName"));
				basicInfo.setFieldName(rs.getString("fieldName"));
				basicInfo.setTableName(rs.getString("tableName"));
				list.add(basicInfo);
			}
			return list;
		} catch (Exception e) {
			// e.printStackTrace();
			return null;
		} finally {
			try {
				if (st != null) {
					st.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 查找具有相同表名的tuple的元素个数
	 * 
	 * @param name
	 * @return
	 */
	public static int getTuples(String name) {
		Connection conn = null;
		Statement st = null;
		String sql = " select count(*) from TD_ELEMENT_COLUMN_RELATION where tableName in("
				+ "select  t.tablename from TD_ELEMENT_COLUMN_RELATION t where t.elementname ='"
				+ name + "' )" + "and type='tuple' and fieldname is null";
		try {
			conn = connectionUtil.getConnection();
			st = conn.createStatement();
			ResultSet rs = st.executeQuery(sql);
			int tupleSize = 0;
			if (rs.next()) {
				tupleSize++;
			}
			return tupleSize;
		} catch (Exception e) {
			// e.printStackTrace();
			return 0;
		} finally {
			try {
				if (st != null) {
					st.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 执行多个SQL，最后提交
	 * 
	 * @param sqls
	 * @return
	 * @throws Exception
	 * @throws Exception
	 */
	public static void executeSQLs(List<String> sqlList) throws SQLException {
		Connection con = null;
		Statement stmt = null;
		try {
			// 创建连接
			con = connectionUtil.getConnection();
			con.setAutoCommit(false);
			stmt = con.createStatement();
			for (int ii = 0; ii < sqlList.size(); ii++) {
				if (sqlList.get(ii) != null) {
					stmt.addBatch((String) sqlList.get(ii));
				}
			}
			stmt.executeBatch();
			con.commit();
			stmt.close();
			con.close();
			stmt = null;
			con = null;
		} catch (Exception e) {
			con.rollback();
			throw e;
		} finally {
			try {
				if (con != null) {
					con.close();
					con = null;
				}
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
			} catch (Exception e) {
				throw e;
			}
		}

	}
}
