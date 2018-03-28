package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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
 * Servlet implementation class DelAttendance
 */
@WebServlet("/delattendance")
public class DelAttendance extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection cn;
	private PreparedStatement ps,ps1;
	private RequestDispatcher rd;
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
			ps=cn.prepareStatement("delete from m_attendance where Stu_id=?");
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
			String z[]=request.getParameterValues("t1");
			for(int i=0;i<z.length;i++)
			{
				ps.setString(1, z[i]);
				ps.execute();
			}
			out.print("<h2 align='center'>Proxies Deleted</h2><form action=\"./markattendance\" method=\"post\"style=\"border:1px solid #ccc\"><div class=\"container\"><input type=\"hidden\"name=\"t1\"value="+request.getParameter("t2")+"><div class=\"clearfix\"><button type=\"submit\" class=\"signupbtn\">Back to Roll_Call</button></div></div></form>");
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
