

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet implementation class Logout
 */
public class Logout extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Logout() {
        super();
        
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.getWriter().append("Served at: ").append(request.getContextPath());
		
		response.setContentType("text/html");
        PrintWriter printWriter  = response.getWriter();

		// En tête général
        printWriter.println("<!DOCTYPE html>") ; 
        printWriter.println("<html lang=\"fr-FR\">");  
        printWriter.println("<head>");  
        printWriter.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");  
        printWriter.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"WebDomoCss.css\" /> ");
        printWriter.println("<title> Logout </title>");
		printWriter.println("</head>");
		printWriter.println("<body>");
        
        // Menus
        printWriter.println("<table><tr><td width=\"70\">");
		printWriter.println("<nav role=\"navigation\"> <div id=\"menuToggle\">");
		printWriter.println("<input type=\"checkbox\" /> <span></span> <span></span> <span></span>") ;
		printWriter.println("<ul id=\"menu\">");
		printWriter.println("<li><a href=\"Home\">&nbsp&nbspSynthèse</a></li>");
		printWriter.println("<li><a href=\"Items\">&nbsp&nbspItems</a></li>");
		printWriter.println("<li><a href=\"Control\">&nbsp&nbspControl</a></li>");
		printWriter.println("<li><a href=\"Logout\">&nbsp&nbspLogout</a></li>");
		printWriter.println("</ul>");
		printWriter.println("</div> </nav>" ) ;
		printWriter.println("</td><td><H1> "+ "DomoHub - Logout" + "</H1></td></tr></table>");
        
		printWriter.println("<table id=\"content3\"><tr><td>");
		// Contenu
   		printWriter.println("<p><b> Vous êtes déconnecté. </b></p>"); 

      	HttpSession session = request.getSession();
      	session.invalidate();
		
        // Footer
		printWriter.println("</td></tr></table> ");
		printWriter.println("<table id=\"content3\" style=\"color:#FFFFFF; background:#000000 \"> "); // 
	    printWriter.println("     <tr><td>Logiciel Hub Domotique MQTT Thibault Pouch </td>");	
		printWriter.println(" </tr></table>");
        printWriter.println("</body>");
      	printWriter.println("</html>");
  
      	/*session = request.getSession(true);
        String stRedirect = "./Home" ;
            
        if ( !stRedirect.equals("") )  
    	  response.sendRedirect( stRedirect ) ; // force routage vers page spécifiée */
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		doGet(request, response);
	}

}
