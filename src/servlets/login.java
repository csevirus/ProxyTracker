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
 * Servlet implementation class login
 */
@WebServlet("/login")
public class login extends HttpServlet {
	private Connection cn;
	private PreparedStatement ps,ps1;
	private static final long serialVersionUID = 1L;
	protected void finalize() throws Throwable {
		cn.close();
		ps.close();
		ps1.close();
		super.finalize();
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String id= request.getParameter("t1");
		String pwd=request.getParameter("t2");
		PrintWriter out= response.getWriter();
		RequestDispatcher rd;
		try {
			ps.setString(1, id);
			ps.setString(2, pwd);
			ResultSet rs=ps.executeQuery();
			rs.next();
			if(rs.getInt(1)==1)
			{
				ps1.setString(1, id);
				rs= ps1.executeQuery();
				rs.next();
				String des=rs.getString(1);
				HttpSession session = request.getSession();
				session.setAttribute("id", id);
				session.setMaxInactiveInterval(180);
				if(des.equals("hod"))
				{
					response.sendRedirect("./hodhome");
				}
				else if(des.equals("lec"))
				{
					response.sendRedirect("./lechome");
				}
				else if(des.equals("stu"))
				{
					response.sendRedirect("./stuhome");
				}
			}
			else
			{
				out.print("<h2 align='center'>InValid ID or Password</h2>");
				rd=request.getRequestDispatcher("./index.html");
				rd.include(request, response);
			}
		} catch (SQLException e) {	
			e.printStackTrace();
		}
	}
	@Override
	public void init() throws ServletException {
		try
		{
			ServletContext ctx=getServletConfig().getServletContext();
			Class.forName(ctx.getInitParameter("driver"));
			cn=DriverManager.getConnection(ctx.getInitParameter("url"),ctx.getInitParameter("uname"),ctx.getInitParameter("pwd"));
			ps=cn.prepareStatement("select count(*) from login where login_id=? and pwd=?");
			ps1=cn.prepareStatement("select des from login where login_id=?");
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
