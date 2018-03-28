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

/**
 * Servlet implementation class StuRegister
 */
@WebServlet("/reg3")
public class StuRegister extends HttpServlet {
	private Connection cn;
	private PreparedStatement ps,ps1;
	@Override
	public void init() throws ServletException {
		try
		{
			ServletContext ctx=getServletConfig().getServletContext();
			Class.forName(ctx.getInitParameter("driver"));
			cn=DriverManager.getConnection(ctx.getInitParameter("url"),ctx.getInitParameter("uname"),ctx.getInitParameter("pwd"));
			ps=cn.prepareStatement("insert into stu values(?,?,?,?)");
			ps1=cn.prepareStatement("insert into login values(?,?,\"stu\")");
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

	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String dept=request.getParameter("t1");
		String name=request.getParameter("t2");
		String id=request.getParameter("t3");
		String pwd=request.getParameter("t4");
		String year=request.getParameter("t5");
		PrintWriter out=response.getWriter();
		RequestDispatcher rd=request.getRequestDispatcher("/register3.html");
		RequestDispatcher rdl=request.getRequestDispatcher("/index.html");
		try {
			ps.setString(1, dept);
			ps.setString(2, name);
			ps.setString(3, id);
			ps.setString(4, year);
			ps1.setString(1, id);
			ps1.setString(2, pwd);
			if((0<ps.executeUpdate())&&(0 < ps1.executeUpdate()))
			out.print("<h2 align='center'>Registered Successfully</h2>");
			rdl.include(request, response);
		}
		catch (SQLException e)
		{
			out.print("<h2 align='center'>Department must exist and LoginID must be Unique</h2>");
			rd.include(request, response);
			e.printStackTrace();		
		}	
	}
	@Override
	protected void finalize() throws Throwable {
		cn.close();
		ps.close();
		ps1.close();
		super.finalize();
	}
}
