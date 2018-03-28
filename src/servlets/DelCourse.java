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


@WebServlet("/DelCourse")
public class DelCourse extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection cn;
	private PreparedStatement ps,ps1,ps2;
	private RequestDispatcher rd;
	private PrintWriter out;
	private ResultSet rs;
	private HttpSession session;
    protected void finalize() throws Throwable {
		cn.close();
		ps.close();
		ps1.close();
		ps2.close();
		super.finalize();
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
		try {
			ps.setString(1, request.getParameter("t1"));
			ps.setString(2, session.getAttribute("id").toString());
			ps1.setString(1, request.getParameter("t1"));
			ps2.setString(1, request.getParameter("t1"));
			ps2.setString(2, session.getAttribute("id").toString());
			rs=ps2.executeQuery();
			rs.next();
			if(rs.getInt(1)==1)
			{
				ps1.execute();ps.execute();
				out.print("<h2 align='center'>Course Deleted</h2>");
			}
			else
			{
				out.print("<h2 align='center'>Course Name Must Exist under your Login_ID Try Again</h2>");
			}
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
	}
	public void init() throws ServletException {
		try
		{
			ServletContext ctx=getServletConfig().getServletContext();
			Class.forName(ctx.getInitParameter("driver"));
			cn=DriverManager.getConnection(ctx.getInitParameter("url"),ctx.getInitParameter("uname"),ctx.getInitParameter("pwd"));
			ps=cn.prepareStatement("delete from courses where name=?and lec_login_id=?");
			ps2=cn.prepareStatement("select count(*) from courses where name=?and lec_login_id=?");
			ps1=cn.prepareStatement("delete from attendance where Course_Name=?");
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
}
