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
 * Servlet implementation class ViewAttendance
 */
@WebServlet("/viewattendance")
public class ViewAttendance extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection cn;
	private PreparedStatement ps,ps1;
	private RequestDispatcher rd;
	private ResultSet rs;
	private PrintWriter out;
	private HttpSession session;
    protected void finalize() throws Throwable {
		cn.close();
		ps.close();
		ps1.close();
		super.finalize();
	}
	public void init() throws ServletException {
		try
		{
			ServletContext ctx=getServletConfig().getServletContext();
			Class.forName(ctx.getInitParameter("driver"));
			cn=DriverManager.getConnection(ctx.getInitParameter("url"),ctx.getInitParameter("uname"),ctx.getInitParameter("pwd"));
			ps=cn.prepareStatement("select Stu_id,Total_classes,Attended_classes from attendance where Course_Name=?");
			ps1=cn.prepareStatement("select count(*) from courses where name=?and lec_login_id=?");
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
	    	 rd=request.getRequestDispatcher("./lectemplate.html");
	    	 rd.include(request, response);
	     }
		try
		{
			ps1.setString(1, request.getParameter("t1"));
			ps1.setString(2, session.getAttribute("id").toString());
			rs=ps1.executeQuery();
			rs.next();
			if(rs.getInt(1)==1)
			{
				ps.setString(1,request.getParameter("t1"));
				rs=ps.executeQuery();
				out.println("<div style='overflow-x:auto;'><table align='center' border=\"1\" width=\"200\" height=\"50\"><caption>Attendance Table for "+request.getParameter("t1")+"</caption><thead><tr><td align=\"center\">Stu_id</td><td align=\"center\">Total_classes</td><td align=\"center\">Attended_classes</td><td align=\"center\">Percentage</td></thead><tbody>");
				while(rs.next())
				{
					out.print("<tr><td align=\"center\">"+rs.getString(1)+"</td><td align=\"center\">"+ rs.getString(2)+"</td><td align=\"center\">"+ rs.getString(3)+"</td><td align=\"center\">"+Float.parseFloat(rs.getString(3))*100/Float.parseFloat(rs.getString(2))+"</td></tr>");
				}
				out.print("</tbody></table></div>");
			}
			else
			{
				out.print("<h2 align='center'>Course Must Exist under your Login_id</h2><br>");
			}
		}
		catch(SQLException k)
		{
			out.print("<h2 align='center'>Invalid Course Name</h2>");
			k.printStackTrace();
		}
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
