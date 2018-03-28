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
 * Servlet implementation class AddCourse
 */
@WebServlet("/AddCourse")
public class AddCourse extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection cn;
	private PreparedStatement ps,ps1,ps2,ps3;
	private ResultSet rs;
	private RequestDispatcher rd;
	private PrintWriter out;
	private HttpSession session;
	@Override
	protected void finalize() throws Throwable {
		cn.close();
		ps.close();
		ps1.close();
		ps2.close();
		ps3.close();
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
		try
		{
			String id=(String)session.getAttribute("id");
			ps.setString(1, id);
			rs=ps.executeQuery();
			rs.next();
			String dept=request.getParameter("t3");
			String name=rs.getString(2);
			ps1.setString(1, dept);
			ps1.setString(2, request.getParameter("t1"));
			ps1.setString(3,name);
			ps1.setString(4, request.getParameter("t2"));
			ps1.setString(5, id);
			ps1.execute();
			out.println("<h2 align='center'>Course Added Successful</h2>");
		    ps3.setString(1, request.getParameter("t2"));
		    ps3.setString(2,dept);
		    rs=ps3.executeQuery();
		    while(rs.next())
		    {
		    	ps2.setString(1,request.getParameter("t1"));
		    	ps2.setInt(2, 0);
		    	ps2.setInt(3, 0);
		    	ps2.setString(4, rs.getString(1));
		    	ps2.setString(5,"no");
		    	ps2.execute();
		    }
		}
		catch(SQLException k)
		{
			out.print("<h2 align='center'>Course name must be unique Try Again</h2>");
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
			ps=cn.prepareStatement("select * from lec where login_id=?");
			ps1=cn.prepareStatement("insert into courses values(?,?,?,?,?)");
			ps2=cn.prepareStatement("insert into attendance values(?,?,?,?,?)");
			ps3=cn.prepareStatement("select login_id from stu where year=? and dept=?");
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
