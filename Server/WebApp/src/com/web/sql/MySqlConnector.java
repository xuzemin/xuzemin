package com.web.sql;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang.StringUtils;

import com.Application.utils.Constant;
import com.Application.utils.HttpUrl;
public class MySqlConnector {
	private static Connection connect;
	private static MySqlConnector mysqlconnect;
	private static Statement stmt;
	public MySqlConnector() {
		try {
			Class.forName(Constant.JDBC);     //加载MYSQL JDBC驱动程序   
			//Class.forName("org.gjt.mm.mysql.Driver");
			System.out.println("Success loading Mysql Driver!");
		}catch (Exception e) {
			System.out.print("Error loading Mysql Driver!");
			e.printStackTrace();
		}
		try {
			connect = DriverManager.getConnection(
					HttpUrl.SQlCONNECTURL,HttpUrl.SQLNAME,HttpUrl.SQLPWD);
			//连接URL为   jdbc:mysql//服务器地址/数据库名  ，后面的2个参数分别是登陆用户名和密码

			System.out.println("Success connect Mysql server!");
			stmt = connect.createStatement();
		}catch (Exception e) {
			System.out.print("get data error!");
			e.printStackTrace();
		}
	}
	public void selectsqlall(String table) {
		ResultSet rs;
		try {
			rs = stmt.executeQuery("select * from "+table+"");
			while (rs.next()) {
				System.out.println(rs.getString("userName"));
				System.out.println(rs.getString("userPassword"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//user 为你表的名称
	}
	public ResultSet selectSql(String table,String name){
		if(connect != null && stmt != null) {
			ResultSet rs;
			try {
				String sql = "select * from "+table+" where userName = '"+name+"'";
				rs = stmt.executeQuery(sql);
				while (rs.next()) {
					return rs;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	public boolean updateSql(String table,String userid,int id,HashMap<String, ?> map){
		if(connect != null && stmt != null) {
			String sql = "update "+ table +" set " ;
			List<Object> list = new ArrayList<Object>();
			Set<String> keys = map.keySet();
			for(String string:keys) {
				System.out.println(string);
				list.add(string + "='"+map.get(string)+"' ");
			}
			sql += ""+StringUtils.join(list,",")+" where "+userid+" = "+id+"";
			System.out.println(sql);
			try {
				return stmt.execute(sql);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}
	public boolean insertSql(String table,HashMap<String, ?> map){
		if(connect != null && stmt != null) {
			String sql = "insert into "+ table +" " ;
			List<Object> key = new ArrayList<Object>();
			List<Object> value = new ArrayList<Object>();
			Set<String> keys = map.keySet();
			for(String string :keys) {
				System.out.println(string);
				key.add(string);
				value.add("'"+map.get(string)+"'");
			}
			sql += "("+StringUtils.join(key,",")+") values ("+StringUtils.join(value,",")+")";
			System.out.println(sql);
			try {
				return stmt.execute(sql);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}
	public static MySqlConnector getInstance() {
		if(mysqlconnect == null) {
			mysqlconnect = new MySqlConnector();
		}
		return mysqlconnect;
	}
	public static void main(String args[]) {
//		MySqlConnector.getInstance().selectSql("huzhiheng");
//		HashMap map = new HashMap();
//		map.put("name", "xzm");
//		map.put("password", "12345");
//		boolean isOK = MySqlConnector.getInstance().insertSql("user",map);
//		MySqlConnector.getInstance().selectsqlall("user");
	}
}
