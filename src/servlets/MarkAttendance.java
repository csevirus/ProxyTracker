package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class MarkAttendance
 */
@WebServlet("/markattendance")
public class MarkAttendance extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection cn;
	private PreparedStatement ps,ps2;
	private RequestDispatcher rd;
	private ResultSet rs;
	private PrintWriter out;
	private HttpSession session;
    protected void finalize() throws Throwable {
		cn.close();
		ps.close();
		
		ps2.close();
		
		super.finalize();
	}
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		out = response.getWriter();
		session = request.getSession(false);
		if(session==null)
	     {
	    	 rd=request.getRequestDispatcher("./index.html");
	    	 out.println("<h2 align='center'>session expired login again</h2>");
	    	 rd.include(request, response);
	     }
	     else
	     {
	    	 rd=request.getRequestDispatcher("./lectemplate.html");
	    	 rd.include(request, response);
	     }
		try
		{
			ps.setString(1, request.getParameter("t1"));
			ps2.setString(1, request.getParameter("t1"));
			ps2.execute();
			rs=ps.executeQuery();
			out.println("<form action=\"./delattendance\"method=\"post\"style=\"border:1px solid #ccc\"><div class=\"container\"><input type=\"hidden\" name=\"t2\"value="+request.getParameter("t1")+"><div style='overflow-x:auto;'><table align='center'border=\"1\" width=\"200\" height=\"50\"><caption>Present Students</caption><thead><tr><td align=\"center\">Student ID</td><td align=\"center\">Proxy</td></thead><tbody>");
			while(rs.next())
	        {
	           out.print("<tr><td align=\"center\">"+rs.getString(1)+"</td><td><input type=\"checkbox\" name=\"t1\" value="+rs.getString(1)+"></td></tr>");    	
	        }
			out.print("</tbody></table></div><div class=\"clearfix\"><button type=\"submit\" class=\"signupbtn\">Delete Proxy</button></div></div></form>");
			out.print("<form action=\"./submitattendance\"method=\"post\"style=\"border:1px solid #ccc\"><div class=\"container\"><input type=\"hidden\"name=\"t1\" value="+request.getParameter("t1")+"><div class=\"clearfix\"><button type=\"submit\" class=\"signupbtn\">Mark Attendance</button></div></div></form>");
		}
		catch(SQLException k)
		{
			rd=request.getRequestDispatcher("./ta.html");
		    out.println("<h2 align='center'>Course Name Must Exist Under Your Login_id</h2>");
		    rd.include(request, response);
			k.printStackTrace();
		}
	}
	@Override
	public void init() throws ServletException {
		try
		{
			ServletContext ctx=getServletConfig().getServletContext();
			Class.forName(ctx.getInitParameter("driver"));
			cn=DriverManager.getConnection(ctx.getInitParameter("url"),ctx.getInitParameter("uname"),ctx.getInitParameter("pwd"));
			ps=cn.prepareStatement("select Stu_id from m_attendance where Course_Name=?");
			ps2=cn.prepareStatement("update attendance set visible=\"no\" where Course_Name=?");
		}
		catch(ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch(SQLException k)
		{
			k.printStackTrace();
		}
		super.init();
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
