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
 * Servlet implementation class AttendRollcall
 */
@WebServlet("/attendrollcall")
public class AttendRollcall extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection cn;
	private PreparedStatement ps,ps1;
	private ResultSet rs;
	private String id;
	private RequestDispatcher rd;
	private PrintWriter out;
	private HttpSession session;
	protected void finalize() throws Throwable {
		cn.close();
		ps.close();
		ps1.close();
		
		super.finalize();
	}
	@Override
	public void init() throws ServletException {
		try
		{
			ServletContext ctx=getServletConfig().getServletContext();
			Class.forName(ctx.getInitParameter("driver"));
			cn=DriverManager.getConnection(ctx.getInitParameter("url"),ctx.getInitParameter("uname"),ctx.getInitParameter("pwd"));
			ps=cn.prepareStatement("select Course_Name from attendance where visible='yes'and Stu_id=?");
			ps1=cn.prepareStatement("insert into m_attendance values(?,?)");
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
	    	 rd=request.getRequestDispatcher("./stutemplate.html");
	    	 rd.include(request, response);
	     }
		try
		{
			id=(String)session.getAttribute("id");
			ps.setString(1, id);
			rs=ps.executeQuery();
			rs.next();
			ps1.setString(1,rs.getString(1));
			ps1.setString(2,id);
			ps1.execute();
			if(ps1.getUpdateCount()==1)
				out.print("<h4 align='center'>Attendance Marked Successfully for "+rs.getString(1)+"</h4>");
			else
				out.print("<h4 align='center'>You cannot mark your attendance more than once </h4><h4 align='center'>or the attendance portal is not available</h4>");
		}
		catch(SQLException k)
		{
			out.print("<h4 align='center'>You cannot mark your attendance more than once</h4><h4 align='center'>or the attendance portal is not available</h4>");
			k.printStackTrace();
		}
		
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
