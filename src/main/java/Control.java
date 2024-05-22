import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Arrays;

import classes.DomoHub;
import classes.Element;
import classes.MQTT;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Control
 */
public class Control extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Control() {
        super();

    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

//		response.getWriter().append("Served at: ").append(request.getContextPath());


		response.setContentType("text/html");
        PrintWriter printWriter  = response.getWriter();
		ArrayList<String>stListePieces = null ;

		String[] selectedOptions = request.getParameterValues("option");
        int numberOfSelectedOptions = 0;
        if (selectedOptions != null) {
            numberOfSelectedOptions = selectedOptions.length;
        }
		ArrayList<String> foundUids = new ArrayList<>();
  
  		/**/
  		
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
		printWriter.println("</td><td><H1> "+ "DomoHub - Control" + "</H1></td></tr></table>");
        
		// Contenu
		printWriter.println("<form method=\"GET\" action=\"Control\">");
		printWriter.println("<table id=\"content3\"><tr><td>");
		Element.getItems (printWriter);
		printWriter.println("<p></p>");
		MQTT.listRules () ;
		String mqttResponse = MQTT.stData;
		String[] elements = mqttResponse.split(",");
	    printWriter.println("<table width=\"100%\" id=\"contentgray\">");
		for (String element : elements) {
			if (element.contains("\"uid\"")) {
				String uid = element.split(":")[1].trim();
				uid = uid.substring(1, uid.length() - 1); // Enlever les guillemets
				if (uid.startsWith("DomoHub_")) {
					uid = uid.substring("DomoHub_".length());
					foundUids.add(uid);
				}
			}
		}
		//printWriter.println("<tr><td> data = </td><td>" + MQTT.stData + "</td></tr> ");
	    printWriter.println("</table>");


		//liste boutton
		printWriter.println("  <input type=\"hidden\" name=\"Action\" value=\"CreateRule\" >  <input type=\"submit\" value=\"Validé\" class=\"LPbutton\">");

		//Table items
		printWriter.println("<table width=\"100%\" class=\"imagetable\" id=\"contentgray\" ><caption>Liste des items : </caption>");
		printWriter.println("<tr><th width=50>Actif</th><th width=20>Create</th><th width=20>Delete</th><th width=120>Pièce</th><th width=60>Id</th><th width=200>Libellé</th><th width=100>Type</th></tr>");
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
			if (foundUids.contains(myItem.stIdItem)) {
				printWriter.println("<tr><td> Actif </td>");
			} else {
				printWriter.println("<tr><td> Desactif </td>");
			}
			if (foundUids.contains(myItem.stIdItem)) {
				printWriter.println("<td><input type=\"checkbox\" name=\"add_" + myItem.stIdItem + "\" value=\"" +  myItem.stIdItem + "\"disabled/></td>");
			} else {
				printWriter.println("<td><input type=\"checkbox\" name=\"add_" + myItem.stIdItem + "\" value=\"" +  myItem.stIdItem + "\"/></td>");
			}
			if (foundUids.contains(myItem.stIdItem)) {
				printWriter.println("<td><input type=\"checkbox\" name=\"del_" + myItem.stIdItem + "\" value=\"" +  myItem.stIdItem + "\"/></td>");
			} else {
				printWriter.println("<td><input type=\"checkbox\" name=\"del_" + myItem.stIdItem + "\" value=\"" +  myItem.stIdItem + "\"disabled/></td>");
			}
			//printWriter.println("<td><input type=\"checkbox\" name=\"del_" + myItem.stIdItem + "\" value=\"" +  myItem.stIdItem + "\"/></td>");
			printWriter.println("<td>" + myItem.stPiece + "</td>");
			printWriter.println("<td>" + myItem.stIdItem + "</td>");
			printWriter.println("<td>" + myItem.stLibItem + "</td>");
			printWriter.println("<td>" + stType + "</td>");
   		}
        printWriter.println("</table>");
		printWriter.println("</form>");

		//action Boutton
		/*if (stAction.equals("CreateRule"))
		{
			MQTT.createRule ("DomoHub_toto") ;
	        printWriter.println("<table width=\"100%\" id=\"contentgray\">");
			printWriter.println("<tr><td> req = </td><td>" + MQTT.stTraceLastReq + "</td></tr> ");
			printWriter.println("<tr><td> data = </td><td>" + MQTT.stData + "</td></tr> ");
			printWriter.println("<tr><td> err = </td><td>" + MQTT.stTraceError + "</td></tr> ");
	        printWriter.println("</table>");
		}*/
			Enumeration<String> parameterNames = request.getParameterNames();
			ArrayList<String> selectedCheckboxes = new ArrayList<>();
			while (parameterNames.hasMoreElements()) {
				String paramName = parameterNames.nextElement();
				if (paramName.startsWith("add_")) {
					String[] paramValues = request.getParameterValues(paramName);
					selectedCheckboxes.addAll(Arrays.asList(paramValues));
				}
			}
			for (String checkboxValue : selectedCheckboxes) {
				MQTT.createRule(checkboxValue);
			}
			parameterNames = request.getParameterNames();
			selectedCheckboxes = new ArrayList<>();
			while (parameterNames.hasMoreElements()) {
				String paramName = parameterNames.nextElement();
				if (paramName.startsWith("del_")) {
					String[] paramValues = request.getParameterValues(paramName);
					selectedCheckboxes.addAll(Arrays.asList(paramValues));
				}
			for (String checkboxValue : selectedCheckboxes) {
				MQTT.deleteRule("DomoHub_" + checkboxValue);
			}
		}
           		   		
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
