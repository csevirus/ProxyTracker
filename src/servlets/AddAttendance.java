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

@WebServlet("/addattendance")
public class AddAttendance extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection cn;
	private PreparedStatement ps,ps2,ps3;
	private RequestDispatcher rd;
	private ResultSet rs;
	private PrintWriter out;
	private HttpSession session;
    @Override
	protected void finalize() throws Throwable {
		cn.close();
		ps.close();
		ps2.close();
		ps3.close();
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
			String id=session.getAttribute("id").toString();
			ps3.setString(1, request.getParameter("t1"));
			ps3.setString(2, id);
			rs=ps3.executeQuery();
			rs.next();
			if(rs.getInt(1)==1)
			{
				ps.setString(1, request.getParameter("t1"));
				ps.execute();
				ps2.setString(1, request.getParameter("t1"));
				ps2.execute();
				out.print("<h3 align='center'>please wait until all students mark their attendance</h3><br><form action=\"./markattendance\" method=\"post\"style=\"border:1px solid #ccc\"><div class=\"container\"><input type=\"hidden\"name=\"t1\"value="+request.getParameter("t1")+"><div class=\"clearfix\"> <button type=\"submit\" class=\"signupbtn\">View Roll_Call</button> </div></div</form>");
			}
			else
				out.println("<h2 align='center'>Course Name Must Exist Under Your Login_id</h2>");
		}
		catch(SQLException k)
		{
		    out.println("<h2 align='center'>Course Name Must Exist Under Your Login_id</h2>");
			k.printStackTrace();
		}
	}
	public void init() throws ServletException {
		try
		{
			ServletContext ctx=getServletConfig().getServletContext();
			Class.forName(ctx.getInitParameter("driver"));
			cn=DriverManager.getConnection(ctx.getInitParameter("url"),ctx.getInitParameter("uname"),ctx.getInitParameter("pwd"));
			ps=cn.prepareStatement("update attendance set Total_classes=Total_classes+1  where Course_Name=?");
			ps2=cn.prepareStatement("update attendance set visible=\"yes\" where Course_Name=?");
			ps3=cn.prepareStatement("select count(*) from courses where name=? and lec_login_id=?");
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
		doGet(request, response);
	}

}
