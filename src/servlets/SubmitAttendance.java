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
 * Servlet implementation class SubmitAttendance
 */
@WebServlet("/submitattendance")
public class SubmitAttendance extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection cn;
	private PreparedStatement ps,ps1,ps3;
	private RequestDispatcher rd;
	private ResultSet rs;
	private PrintWriter out;
	private HttpSession session;
    protected void finalize() throws Throwable {
		cn.close();
		ps.close();
		ps1.close();
		ps3.close();
		super.finalize();
	}
	public void init() throws ServletException {
		try
		{
			ServletContext ctx=getServletConfig().getServletContext();
			Class.forName(ctx.getInitParameter("driver"));
			cn=DriverManager.getConnection(ctx.getInitParameter("url"),ctx.getInitParameter("uname"),ctx.getInitParameter("pwd"));			ps=cn.prepareStatement("select Stu_id from m_attendance where Course_Name=?");
			ps1=cn.prepareStatement("update attendance set Attended_classes=Attended_classes+1 where Stu_id=?and Course_Name=?");
			ps3=cn.prepareStatement("delete from m_attendance where Course_Name=?");
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
			ps.setString(1, request.getParameter("t1"));
			rs=ps.executeQuery();
			while(rs.next())
			{
				ps1.setString(1, rs.getString(1));
				ps1.setString(2,request.getParameter("t1"));
				ps1.execute();
			}
			ps3.setString(1,request.getParameter("t1"));
			ps3.execute();
			out.print("<h2 align='center'>Attendance Updated</h2>");
		 }
		 catch(SQLException k)
		 {
			 rd=request.getRequestDispatcher("./ta.html");
			    out.println("<h2 align='center'>Course Name Must Exist Under Your Login_id</h2>");
			    rd.include(request, response);
				k.printStackTrace();
		 }
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
