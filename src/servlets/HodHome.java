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

@WebServlet("/hodhome")
public class HodHome extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection cn;
	private PreparedStatement ps,ps1;
	private ResultSet rs;
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
			ps=cn.prepareStatement("select dept from hod where login_id=?");
			ps1=cn.prepareStatement("select name,lec,year from courses where dept=?");
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
	    	 rd=request.getRequestDispatcher("./hodtemplate.html");
	    	 rd.include(request, response);
	     }
		try
		{
			String id=(String)session.getAttribute("id");
			ps.setString(1, id);
			rs=ps.executeQuery();
			rs.next();
			ps1.setString(1, rs.getString(1));
			rs=ps1.executeQuery();
			out.println("<div style='overflow-x:auto;'><table align='center' border=\"1\" width=\"200\" height=\"50\"><caption>Available Course in Your Department</caption><thead><tr><td align=\"center\">Course Name</td><td align=\"center\">Lecturar Name</td><td align=\"center\">Year</td></thead><tbody>");
			while(rs.next())
	        {
	           out.print("<tr><td align=\"center\">"+rs.getString(1)+"</td><td align=\"center\">"+ rs.getString(2)+"</td><td align=\"center\">"+ rs.getString(3)+"</td></tr>");    	
	        }
			out.print("</tbody></table></div>");
		}
		catch(SQLException k)
		{
			k.printStackTrace();
		}
		
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
