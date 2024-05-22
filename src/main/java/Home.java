


import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import classes.DomoHub;
import classes.Element;
import classes.MQTT;
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
		
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		ArrayList<String>stListePieces = null ;
		
		response.setContentType("text/html");
        PrintWriter printWriter  = response.getWriter();
        
        String stAction = "" ;
        if (request.getParameter("Action") != null)
        	stAction = request.getParameter("Action");
        
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

		Element.getItems (printWriter) ;
		String alarmeActivationValue = "";
		for (Element myItem : DomoHub.dataItems) {
			if (myItem.stIdItem.equals("_ALARME_Activation")) {
				alarmeActivationValue = myItem.stState;
				break;
			}
		}
   		printWriter.println("<table width=\"100%\" id=\"imagetable\"><tr><td width=\"22%\" > ");
		stListePieces = Element.getListePieces() ;
		printWriter.println("	<table width=\"100%\" class=\"imagetable\" id=\"contentgray\" ><caption>Pièces : </caption>");
        printWriter.println("		<tr><th>Pièce</th></tr>");
   		for (int i = 0 ; i< stListePieces.size(); i++)
   		{
	        printWriter.println("		<tr><td>" +stListePieces.get(i)+ "</td></tr>");
   		}
   		printWriter.println("	</table></td>");
		printWriter.println("	<td width=\"5%\"></td>");


		printWriter.println("	<td width=\"73%\">");
   		printWriter.println("		<table width=\"100%\" id=\"contentgray\"><tr> ");
   		printWriter.println("		<td align=\"center\"><img src=\"./img/maison_alarme.png\"></td>");
		printWriter.println("			<td><table width=\"100%\"><tr>");
		if (alarmeActivationValue.equals("OFF")) {
			printWriter.println("				<tr><h2>Statut de l'alarme :    Desactivé</h2></tr>");
		} else {
			printWriter.println("				<tr><h2>Statut de l'alarme :    Activé</h2></tr>");
		}
		printWriter.println("  				<tr><td>");
		printWriter.println("  				<form method=\"GET\" action=\"Home\">");
		printWriter.println("  					<input type=\"submit\" name=\"Action\" value=\"AlarmOn\" class=\"LPbutton\">");
		printWriter.println("  					<input type=\"submit\" name=\"Action\" value=\"AlarmOff\" class=\"LPbutton\">");
		printWriter.println("  				</td></tr></form>");
		printWriter.println("		</td></tr><tr>");
		alarmeActivationValue = "";
		for (Element myItem : DomoHub.dataItems) {
			if (myItem.stIdItem.equals("_ALARME_Alerte")) {
				alarmeActivationValue = myItem.stState;
				break;
			}
		}
		if (alarmeActivationValue.equals("OFF")) {
			printWriter.println("		<table width=\"100%\"><tr><td bgcolor = \"#81A094\" align=\"center\"><h2>Detection :  Fine</h2></td></tr>");
		} else {
			printWriter.println("		<table width=\"100%\"><tr><td bgcolor = \"#5A2328\" align=\"center\"><h2>Detection :  Trigger</h2></td></tr>");
		}
		printWriter.println("</table>");
		printWriter.println("</table>");
		printWriter.println("</td></tr>");
		printWriter.println("</table>");
   		
   		
		printWriter.println("<table width=\"100%\" class=\"imagetable\" id=\"contentgray\" ><caption>Liste des items : </caption>");
        printWriter.println("	<tr><th width=120>Pièce</th><th width=60>Id</th><th width=200>Libellé</th><th width=100>Type</th><th>Valeur</th></tr>");
   		for (int i = 0 ; i< DomoHub.dataItems.size(); i++)
   		{
   			Element myItem = DomoHub.dataItems.get(i) ;
   			String stType = "" ;
   			String stValeur = "" ;
   			if (myItem.iTypeItem ==  DomoHub.CST_iTypeLamp)
   			{
   				stType = "Lampe" ;
   				stValeur = myItem.stState ; 
   			}
   			if (myItem.iTypeItem ==  DomoHub.CST_iTypeGrad)
   			{
   				stType = "Gradateur" ;
   				if (myItem.stState.equals("0"))
   					stValeur = "OFF" ;
   				else
   					stValeur = "ON (" + myItem.stState+" %)";
   			}	
   			if (myItem.iTypeItem ==  DomoHub.CST_iTypeVolet)
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
	        printWriter.println("		<tr><td>" + myItem.stPiece + "</td><td>" + myItem.stIdItem + "</td><td>" +myItem.stLibItem+ "</td><td>" +stType+ "</td><td>" +stValeur+ "</td></tr>");
   		}
        printWriter.println("	</table>");
        if (stAction.equals("AlarmOn")) {
			MQTT.setValueMQTT ("_ALARME_Activation", "ON") ;
			printWriter.println("<table width=\"100%\" id=\"contentgray\">");
			printWriter.println("<tr><td> req = </td><td>" + MQTT.stTraceLastReq + "</td></tr> ");
			printWriter.println("<tr><td> data = </td><td>" + MQTT.stData + "</td></tr> ");
			printWriter.println("<tr><td> err = </td><td>" + MQTT.stTraceError + "</td></tr> ");
			printWriter.println("</table>");
		}
		if (stAction.equals("AlarmOff")) {
			MQTT.setValueMQTT ("_ALARME_Activation", "OFF") ;
			printWriter.println("<table width=\"100%\" id=\"contentgray\">");
			printWriter.println("<tr><td> req = </td><td>" + MQTT.stTraceLastReq + "</td></tr> ");
			printWriter.println("<tr><td> data = </td><td>" + MQTT.stData + "</td></tr> ");
			printWriter.println("<tr><td> err = </td><td>" + MQTT.stTraceError + "</td></tr> ");
			printWriter.println("</table>");
		}
           		   		
        // Footer
		printWriter.println("</td></tr></table>");
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
