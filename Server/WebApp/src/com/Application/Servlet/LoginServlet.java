package com.Application.Servlet;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.Application.Object.HttpCode;
import com.Application.Object.Person;
import com.Application.utils.Constant;
import com.google.gson.Gson;
import com.web.sql.MySqlConnector;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String Username;
	private String Password;
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LoginServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		synchronized (this) {
			//			String paramVal = this.config.getInitParameter("name");//获取指定的初始化参数
			Username = request.getParameter("username");
			Password = request.getParameter("password");
			HttpCode httpcode = new HttpCode();
			if(Username !=null && Password !=null) {
				ResultSet rs = MySqlConnector.getInstance().selectSql("userInfo",Username);
				if(rs != null) {
					String passwordback = null;
					try {
						passwordback = rs.getString("userPassword");
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(passwordback.equals(Password)) {
						httpcode.setHttpcode(Constant.CODE_OK);
						httpcode.setMessage("login succes");
						Person person = new Person();
						try {
							person.setUserId(rs.getInt("userId"));
							person.setUserType(rs.getInt("userType"));
							person.setLastlogin(rs.getTimestamp("lastLogintime"));
							person.setMacAddress(rs.getString("macAddress"));
							person.setUserUseName(rs.getString("userUseName"));
							System.out.println(rs.getString("userUseName"));
							httpcode.setInfo(person);
							person = null;
							@SuppressWarnings("rawtypes")
							HashMap<String, Comparable> map = new HashMap<String, Comparable>();
							map.put("lastLogintime", new Timestamp(new java.util.Date().getTime()));
							if(null != request.getParameter("macAddress")) {
								map.put("macAddress", request.getParameter("macAddress"));
							}
							MySqlConnector.getInstance().updateSql("userInfo","userId",rs.getInt("userId"),map);
							map = null;
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}else {
						httpcode.setHttpcode(Constant.CODE_LOGIN_FAIL);
						httpcode.setMessage("login fail,please input the right username and password");
					}
				}else {
					httpcode.setHttpcode(Constant.CODE_ERROR);
					httpcode.setMessage("please input right username and password");
				}
			}else {
				httpcode.setHttpcode(Constant.CODE_ERROR);
				httpcode.setMessage("please input username and password");
			}
			Gson gson=new Gson();
			String stuJson=gson.toJson(httpcode);
			response.setCharacterEncoding("GBK");
//			response.setContentType("text/html;charset=utf-8");
			response.getWriter().append(stuJson);
			
			
			//			response.setContentType("text/html");
			//			PrintWriter out = response.getWriter();
			//			out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
			//			out.println("<HTML>");
			//			out.println("  <HEAD><TITLE>A Servlet</TITLE></HEAD>");
			//			out.println("  <BODY>");
			//			out.print("    This is ");
			//			out.print(this.getClass());
			//			out.println(", using the POST method");
			//			out.println("  </BODY>");
			//			out.println("</HTML>");
			//			out.flush();
			//			out.close();
			//			response.getWriter().append("Served at: ").append(request.getContextPath());
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
