


import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;

import classes.DomoHub;
import classes.Element;
import classes.MQTT;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


/**
 * Servlet implementation class Items
 */
public class Items extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Items() {
        super();

    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String  stCodePiece = "" ;
		boolean bTraite = false ; 
		String  stActionItem = "";
		String  stValueItem = "" ;
			
		response.setContentType("text/html");
        PrintWriter printWriter  = response.getWriter();

        if (request.getParameter("Pieces") != null)
        	stCodePiece = request.getParameter("Pieces");

		// Récupère l'éventuelle action à faire
		Enumeration<String> enumeration = request.getParameterNames();
	    while ((enumeration.hasMoreElements()) && (bTraite == false))
	    {
    	    String parameterName = (String) enumeration.nextElement();
    	    if (parameterName.startsWith("Item_"))
    	    {
    	    	bTraite = true ;
    	    	stActionItem = parameterName.substring(5).trim() ;
    	    	stValueItem = request.getParameter(parameterName);
    	    }
    	}

		// En tête général
        printWriter.println("<!DOCTYPE html>") ; 
        printWriter.println("<html lang=\"fr-FR\">");  
        printWriter.println("<head>");  
        printWriter.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");  
        printWriter.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"WebDomoCss.css\" /> ");
        printWriter.println("<title> Items </title>");
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
		printWriter.println("</td><td><H1> "+ "DomoHub - Items" + "</H1></td></tr></table>");
		printWriter.println("<table id=\"content3\"><tr><td>");

		// Contenu
   		printWriter.println("<form method=\"GET\" action=\"Items\" >");
        printWriter.println("<table width=\"100%\" id=\"contentgray\"><caption>Choix de la pièce</caption><tr><td> ");
        
        // Checkboxes de choix des pièces
        ArrayList<String> stListePieces = Element.getListePieces ( ) ; 
        int iCptPiece = 0 ;
    	String stEtat = "" ;
        printWriter.println("<tr><td> Pièces : ");
        while (iCptPiece < stListePieces.size() )
        {
        	String stId = stListePieces.get(iCptPiece) ;
        	stEtat = "" ;
           	if (stCodePiece.contentEquals(stId) == true)
           		stEtat = "checked" ; 
	        printWriter.println("     <input type=\"radio\" value=\""+ stId +"\" name= \"Pieces\"  class=\"LPbutton\" " + stEtat + "  >" +"&nbsp;"+ stId + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
        	iCptPiece++ ;
        }
        printWriter.println("</td><td>");
        printWriter.println("  <input type=\"hidden\" name=\"Action\" value=\"Refresh\" >  <input type=\"submit\" value=\"Rafraichir...\" class=\"LPbutton\">");
        printWriter.println("</td></tr>");
        printWriter.println("</table></form>");
		printWriter.println("<br>");

      	// Traite l'action à faire : 
      	if (!stActionItem.equals(""))
      	{
      		// Envoi une requete MQTT pour forcer le nouvel état
      		MQTT.setValueMQTT (stActionItem, stValueItem) ;
      		printWriter.println("<p> Requete:" + MQTT.stTraceLastReq + "</p><br>");
      		
      		// Met à jour la donnée en mémoire
      		boolean bTrouve = false ; 
      		Element myItem = null ;
    		myItem = getNextItem (stCodePiece, myItem)  ;
			while ((myItem != null) && (bTrouve == false))
			{
				if (myItem.stIdItem.equals(stActionItem))
				{
					bTrouve = true ;
					myItem.stState = stValueItem ; // par défaut
					// cas particuliers
					if ((stValueItem.equals("OFF")) && (myItem.iTypeItem == DomoHub.CST_iTypeGrad))
						myItem.stState = "0" ; 
					if (stValueItem.equals("Haut"))
						myItem.stState = "100" ; 
					if (stValueItem.equals("Bas"))
						myItem.stState = "0" ; 
					if (stValueItem.contains("%"))
						myItem.stState = myItem.stState.substring(0, myItem.stState.indexOf("%")) ; 
				}
				myItem = getNextItem (stCodePiece, myItem)  ;
			}
      	}
		
		// Affichage des éléments dans un tableau à deux colonnes
		Element myItem = null ;
		int  iColonne = 1 ;
		myItem = getNextItem (stCodePiece, myItem)  ;
		if (myItem == null)  // Aucun item pour cette pièce
			printWriter.println("<br> Aucun item pour cette pièce <br>"); 
		else
		{
			printWriter.println("<table width=\"100%\">"); // tableau invisible
			while (myItem != null)
			{
				if (iColonne == 1)
				{
					printWriter.println("<tr><td width=\"48%\">"); 
					printItem (printWriter, stCodePiece, myItem) ;
					printWriter.println("</td><td  width=\"4%\">&nbsp;"); // colonne vide = espacement
					printWriter.println("</td><td  width=\"48%\">");
					iColonne = 2 ;
				}
				else
				{
					printItem (printWriter, stCodePiece, myItem) ;
					printWriter.println("</td></tr>");
					printWriter.println("<tr><td>&nbsp;</td><td>&nbsp;</td></tr>"); // ligne vide = espacement
					iColonne = 1 ;
				}
				myItem = getNextItem (stCodePiece, myItem)  ;
			}
			printWriter.println("</table>"); // tableau invisible
		}

        // Footer
		printWriter.println("</td></tr></table> ");
		printWriter.println("<table id=\"content3\" style=\"color:#FFFFFF; background:#000000 \"> "); // 
	    printWriter.println("     <tr><td>Logiciel Hub Domotique MQTT Thibault Pouch </td>");	
		printWriter.println(" </tr></table>");
        printWriter.println("</body>");
      	printWriter.println("</html>");
      	
	}


	void printItem (PrintWriter printWriter, String stCodePiece, Element myItem)
	{
		printWriter.println("    <table width=\"100%\" class=\"imagetable\" id=\"contentgray\" >"); // bloc item
        printWriter.println("    <tr><td width=80 align=\"center\"> " + "<img src=\"./img/" + getIcon (myItem)+ "\">" + "<br><font size='4'><b>"+ getValue (myItem)+ "</b></font></td>");
        printWriter.println("    <td>");
        printWriter.println("        <table class='tablenoborder' width=\"100%\"><tr><td width=\"80%\"><b>"+myItem.stLibItem+"</b></td>");
        printWriter.println("        <td width=\"20%\"><font size='1'>"+"["+myItem.stIdItem+"]"+"</font></td></tr>"); 
        printWriter.println("        <tr><td>" + "<form method=\"GET\" >");
	    printWriter.println("            "+ "<input type=\"hidden\" name=\"" +"Pieces"+ "\"  value=\""+stCodePiece+"\" >");        
        if (myItem.iTypeItem == DomoHub.CST_iTypeLamp)
        {
	        printWriter.println("            "+ addButton(myItem,"ON", "ON"));
    	    printWriter.println("            "+ addButton(myItem,"OFF", "OFF"));
        }
        if (myItem.iTypeItem == DomoHub.CST_iTypeGrad)
        {
    	    printWriter.println("            "+ addButton(myItem,"0", "OFF"));
	        printWriter.println("            "+ addButton(myItem,"25", "25 %"));
	        printWriter.println("            "+ addButton(myItem,"50", "50 %"));
	        printWriter.println("            "+ addButton(myItem,"100", "100 %"));
        }
        if (myItem.iTypeItem == DomoHub.CST_iTypeVolet)
        {
	        printWriter.println("            "+ addButton(myItem,"100", "Haut"));
    	    printWriter.println("            "+ addButton(myItem,"50", "50 %"));
    	    printWriter.println("            "+ addButton(myItem,"25", "25 %"));
    	    printWriter.println("            "+ addButton(myItem,"0", "Bas"));
        }
        printWriter.println("            "+ "</form>");
        printWriter.println("        </td><td>" + "result" + "</td></tr>");
        printWriter.println("        </table>");
        printWriter.println("    </td></tr></table>");
	}

	private String addButton (Element amyItem, String astValData, String astValAff)
	{
		String stReturn = "" ;
		stReturn = stReturn + "<input type=\"submit\" name=\"Item_" +amyItem.stIdItem+ "\"  value=\""+astValAff+"\" " ;
		if (amyItem.stState.equals(astValData))
			stReturn = stReturn + " disabled " ;
		stReturn = stReturn + " > " ;
		//stReturn = stReturn + "<input type=\"submit\" value=\""+astValAff+"\" class=\"LPaction\">" ;
		return stReturn ;
	}

	private Element getNextItem (String stCodePiece, Element amyItem) 
	{
  		boolean bTrouvePrec = false ;
  		Element    itNext = null ;
  		int     i = 0 ;
  		if (amyItem == null)
  			bTrouvePrec = true; 
  		while ( (i < DomoHub.dataItems.size()) && (itNext == null))
   		{
   			Element myItem = DomoHub.dataItems.get(i) ;
   			if (bTrouvePrec == true)
   			{
	   			if (myItem.stPiece.equals(stCodePiece))
	   				itNext = myItem ; 
   			}
   			if (myItem == amyItem)
   				bTrouvePrec = true ;
   			i++;
   		}
   		return itNext ;
	}

	private String getIcon (Element myItem)
	{
		String stIcon = "" ;
		if (myItem != null)
		{
   			int iValue = 0 ;
   			if (myItem.iTypeItem ==  DomoHub.CST_iTypeLamp)
   			{
   				if (myItem.stState.equals("ON"))
   					stIcon = "lamp_on_48.png" ;
   				else
   					stIcon = "lamp_off_48.png" ;
   			}
   			if (myItem.iTypeItem ==  DomoHub.CST_iTypeGrad)
   			{
   				iValue = Integer.parseInt(myItem.stState.trim()) ;
   				if (iValue == 0)
   					stIcon = "gradateur2-off.png" ;
   				else
   					stIcon = "gradateur2-on.png" ;
   			}	
   			if (myItem.iTypeItem ==  DomoHub.CST_iTypeVolet)
   			{
   				iValue = Integer.parseInt(myItem.stState.trim()) ;
   				if ((iValue > 95) && (stIcon.equals("")))
   					stIcon = "volet2_100_50.png" ;
   				if ((iValue > 85) && (stIcon.equals("")))
   					stIcon = "volet2_90_50.png" ;
   				if ((iValue > 75) && (stIcon.equals("")))
   					stIcon = "volet2_80_50.png" ;
   				if ((iValue > 65) && (stIcon.equals("")))
   					stIcon = "volet2_70_50.png" ;
   				if ((iValue > 55) && (stIcon.equals("")))
   					stIcon = "volet2_60_50.png" ;
   				if ((iValue > 45) && (stIcon.equals("")))
   					stIcon = "volet2_50_50.png" ;
   				if ((iValue > 35) && (stIcon.equals("")))
   					stIcon = "volet2_40_50.png" ;
   				if ((iValue > 25) && (stIcon.equals("")))
   					stIcon = "volet2_30_50.png" ;
   				if ((iValue > 15) && (stIcon.equals("")))
   					stIcon = "volet2_20_50.png" ;
   				if ((iValue >  5) && (stIcon.equals("")))
   					stIcon = "volet2_10_50.png" ;
   				if ( (stIcon.equals("")))
   					stIcon = "volet2_0_50.png" ;
   			}
		}
		return stIcon ;
	}
	

	private String getValue (Element myItem)
	{
		String stValue = "" ;
		if (myItem != null)
		{
   			int iValue = 0 ;
   			if (myItem.iTypeItem ==  DomoHub.CST_iTypeLamp)
   			{
   				if (myItem.stState.equals("ON"))
   					stValue = "ON" ;
   				else
   					stValue = "OFF" ;
   			}
   			if (myItem.iTypeItem ==  DomoHub.CST_iTypeGrad)
   			{
   				iValue = Integer.parseInt(myItem.stState.trim()) ;
				stValue = iValue + " %" ;
   			}	
   			if (myItem.iTypeItem ==  DomoHub.CST_iTypeVolet)
   			{
   				iValue = Integer.parseInt(myItem.stState.trim()) ;
   				if (iValue < 5)
   					stValue = "Bas" ;
   				else
   				{
	   				if (iValue > 95)
   						stValue = "Haut" ;
   					else
  						stValue = iValue + " %" ;
   				}
   			}
		}
		return stValue ;
	}
	
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
	}

}
