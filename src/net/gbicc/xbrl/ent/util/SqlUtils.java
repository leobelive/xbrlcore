package net.gbicc.xbrl.ent.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.gbicc.xbrl.ent.model.BasicInfo;
import net.gbicc.xbrl.ent.model.PublicAccount;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;

public class SqlUtils {

	private static final Log log = LogFactory.getLog(SqlUtils.class);

	// jdbc对象引入
	private static JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	/**
	 * 查找表名下对应 的元素和字段
	 * 
	 * @param name
	 * @param type
	 * @return
	 */
	public static List<BasicInfo> getBasicInfos(String name, String type) {
		// TODO Auto-generated method stub
		List list = new LinkedList();
		String sql = "select elementName,fieldName ,tableName from "
				+ "TD_RELATION t where t.tablename in("
				+ "select tableName from"
				+ " TD_RELATION  where elementName = '" + name
				+ "') and fieldname is not null and type ='" + type + "'";
		List temp = (List) jdbcTemplate.queryForList(sql);
		if (temp != null && temp.size() > 0) {
			Iterator it = temp.iterator();
			while (it.hasNext()) {
				Map result = (Map) it.next();
				BasicInfo basicInfo = new BasicInfo();
				basicInfo.setElementName(result.get("elementName").toString());
				basicInfo.setFieldName((String) result.get("fieldName"));
				basicInfo.setTableName(result.get("tableName").toString());
				list.add(basicInfo);
			}
		}
		return list;

	}

	public static boolean executeSql(String sql) {
		boolean flag = false;
		Connection conn = null;
		Statement st = null;
		try {
			conn = ConnectionUtil.getConnection();
			st = conn.createStatement();
			ResultSet rs = st.executeQuery(sql);
			flag = true;
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

	public static List getBasicInfoByType(String type) {
		// TODO Auto-generated method stub
		List list = new ArrayList();
		String sql = "select distinct(tableName) from TD_RELATION t"
				+ " where t.type = '" + type + "' and t.fieldname is null";
		List temp = (List) jdbcTemplate.queryForList(sql, new String[] {});
		if (temp != null && temp.size() > 0) {
			Iterator it = temp.iterator();
			while (it.hasNext()) {
				Map result = (Map) it.next();
				list.add(result.get("tableName").toString());
			}
		}
		return list;
	}

	public static List<PublicAccount> getPartenS(String tableName) {
		// TODO Auto-generated method stub
		List list = new LinkedList();
		String sql = " select ACCT_CODE,ACCT_CNAME,ACCT_ENAME,ACCT_CLASS,REMARK_INFO from TD_PUBLIC_ACCOUNT where ACCT_CODE in"
				+ "(select t.parten from td_relation t where t.fieldname is not null and t.tablename='"
				+ tableName + "'group by t.parten) and ACCT_ENAME is null";

		List temp = (List) jdbcTemplate.queryForList(sql, new String[] {});
		if (temp != null && temp.size() > 0) {
			Iterator it = temp.iterator();
			while (it.hasNext()) {
				Map result = (Map) it.next();
				PublicAccount publicAccount = new PublicAccount();
				publicAccount.setCode(result.get("ACCT_CODE").toString());
				publicAccount.setcName(result.get("ACCT_CNAME").toString());
				publicAccount.seteName((String) result.get("ACCT_ENAME"));
				publicAccount.setTableName((String) result.get("ACCT_CLASS"));
				publicAccount.setRemarkInfo((String) result.get("REMARK_INFO"));
				list.add(publicAccount);
			}
		}
		return list;
	}

	public static List<BasicInfo> getBasicInfosItem(String tableName,
			String parten) {
		// TODO Auto-generated method stub
		List list = new LinkedList();
		String sql = "select elementName,fieldName ,tableName from "
				+ "TD_RELATION t where t.tablename ='" + tableName
				+ "' and t.parten ='" + parten + "'";
		List temp = (List) jdbcTemplate.queryForList(sql);
		if (temp != null && temp.size() > 0) {
			Iterator it = temp.iterator();
			while (it.hasNext()) {
				Map result = (Map) it.next();
				BasicInfo basicInfo = new BasicInfo();
				basicInfo.setElementName(result.get("elementName").toString());
				basicInfo.setFieldName((String) result.get("fieldName"));
				basicInfo.setTableName(result.get("tableName").toString());
				list.add(basicInfo);
			}
		}
		return list;
	}

	/**
	 * 查找具有相同表名的TUPLE的元素个数
	 * 
	 * @param name
	 * @return
	 */
	public static int getTuples(String name) {
		// TODO Auto-generated method stub
		String sql = " select count(*) from td_relation where tableName in("
				+ "select  t.tablename from td_relation t where t.elementname ='"
				+ name + "' )" + "and type='tuple' and fieldname is null";
		int tupleSize = jdbcTemplate.queryForInt(sql);
		return tupleSize;
	}

	public static List<BasicInfo> getBasicInfoByAccount(String name, String type) {
		// TODO Auto-generated method stub
		List list = new LinkedList();
		String sql = "select elementName,fieldName ,tableName,a.acct_cname parten from td_relation r ,td_public_account a where "
				+ "  r.parten = a.acct_code and tableName in("
				+ "select t.tablename from td_relation t where t.elementname ='"
				+ name
				+ "')"
				+ "and parten in("
				+ "select a.acct_code from td_public_account a where a.acct_ename='"
				+ name + "') and type ='" + type + "'";
		List temp = (List) jdbcTemplate.queryForList(sql);
		if (temp != null && temp.size() > 0) {
			Iterator it = temp.iterator();
			while (it.hasNext()) {
				Map result = (Map) it.next();
				BasicInfo basicInfo = new BasicInfo();
				basicInfo.setElementName(result.get("elementName").toString());
				basicInfo.setFieldName((String) result.get("fieldName"));
				basicInfo.setTableName(result.get("tableName").toString());
				basicInfo.setParten(result.get("parten").toString());
				list.add(basicInfo);
			}
		}
		return list;
	}

	/**
	 * 执行多个SQL，最后提交
	 * 
	 * @param sqls
	 * @return
	 * @throws Exception
	 * @throws Exception
	 */

	public static void executeSQLs(List sqlList) throws Exception {
		// TODO Auto-generated method stub
		Connection con = null;
		Statement stmt = null;
		try {
			// 创建连接
			con = ConnectionUtil.getConnection();
			con.setAutoCommit(false);
			stmt = con.createStatement();
			StringBuffer sb = new StringBuffer();
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
