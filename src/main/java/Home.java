

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


/**
 * Servlet implementation class Home
 */
public class Home extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Home() {
        super();
        
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.getWriter().append("Served at: ").append(request.getContextPath());
		ArrayList<String>stListePieces = null ;
		
		response.setContentType("text/html");
        PrintWriter printWriter  = response.getWriter();
  
        // En tête général
        printWriter.println("<!DOCTYPE html>") ; // balise indique format HTML5
        printWriter.println("<html lang=\"fr-FR\">");  
        printWriter.println("<head>");  
        printWriter.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");  
        printWriter.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"WebDomoCss.css\" /> ");
        printWriter.println("<title> Home </title>");
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
		printWriter.println("</td><td><H1> "+ "DomoHub - Home" + "</H1></td></tr></table>");
        
		// Contenu
		printWriter.println("<table id=\"content3\"><tr><td>");
		Item.getItems (printWriter) ;
   		printWriter.println("<p></p>");
		printWriter.println("<table width=\"100%\" class=\"imagetable\" id=\"contentgray\" ><caption>Liste des items : </caption>");
        printWriter.println("<tr><th width=120>Pièce</th><th width=60>Id</th><th width=200>Libellé</th><th width=100>Type</th><th>Valeur</th></tr>");
   		for (int i = 0 ; i< Constants.dataItems.size(); i++)
   		{
   			Item myItem = Constants.dataItems.get(i) ;
   			String stType = "" ;
   			String stValeur = "" ;
   			if (myItem.iTypeItem ==  Constants.CST_iTypeLamp)
   			{
   				stType = "Lampe" ;
   				stValeur = myItem.stState ; 
   			}
   			if (myItem.iTypeItem ==  Constants.CST_iTypeGrad)
   			{
   				stType = "Gradateur" ;
   				if (myItem.stState.equals("0"))
   					stValeur = "OFF" ;
   				else
   					stValeur = "ON (" + myItem.stState+" %)";
   			}	
   			if (myItem.iTypeItem ==  Constants.CST_iTypeVolet)
   			{
   				stType = "Persienne" ;
   				if (myItem.stState.equals("0"))
   					stValeur = "Bas" ;
   				else
   				{
   		 			if (myItem.stState.equals("100"))
   						stValeur = "Haut" ;
	   				else
	   					stValeur = "Ouvert ("+ myItem.stState+" %)";
   				}
   			}
	        printWriter.println("<tr><td>" + myItem.stPiece + "</td><td>" + myItem.stIdItem + "</td><td>" +myItem.stLibItem+ "</td><td>" +stType+ "</td><td>" +stValeur+ "</td></tr>");
   		}
        printWriter.println("</table>");
   		
   		stListePieces = Item.getListePieces() ;
   		printWriter.println("<br>");
		printWriter.println("<table width=\"200\" class=\"imagetable\" id=\"contentgray\" ><caption>Pièces : </caption>");
        printWriter.println("<tr><th>Pièce</th></tr>");
   		for (int i = 0 ; i< stListePieces.size(); i++)
   		{
	        printWriter.println("<tr><td>" +stListePieces.get(i)+ "</td></tr>");
   		}
        printWriter.println("</table>");
           		   		
        // Footer
		printWriter.println("</td></tr></table> ");
		printWriter.println("<table id=\"content3\" style=\"color:#FFFFFF; background:#000000 \"> "); // 
	    printWriter.println("     <tr><td>Logiciel Hub Domotique MQTT Thibault Pouch </td>");	
		printWriter.println(" </tr></table>");
        printWriter.println("</body>");
      	printWriter.println("</html>");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		doGet(request, response);
	}

}
