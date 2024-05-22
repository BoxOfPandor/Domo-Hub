package classes;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Element {
	public String stIdItem ; 
	public String stLibItem ;
	public String stPiece ; 
	public int    iTypeItem ; 
	public String stState ; 

	private static String stDataShellMQTT ; 
	private static int    iOffsetShellMQTT ;
	
	public Element (String astIt, String astLib, int aiType , String astPiece , String astState)
	{
		stIdItem = astIt; 
		stLibItem = astLib; 
		iTypeItem = aiType; 
		stState = astState; 
		stPiece = astPiece ;
		
	}
	
	/* getItems en dur
	public static void getItems ()
	{
		if (Constants.dataItems.size() > 0)
			Constants.dataItems.clear() ;
		Constants.dataItems.add (new Item ("CNT01-L1", "Test Lamp 1", Constants.CST_iTypeLamp, "Cuisine", "OFF")) ;
		Constants.dataItems.add (new Item ("CNT01-L2", "Test Lamp 2", Constants.CST_iTypeLamp, "Cuisine", "ON")) ;
		Constants.dataItems.add (new Item ("CNT01-G1", "Test Grad 1", Constants.CST_iTypeGrad, "Cuisine", "0")) ;
		Constants.dataItems.add (new Item ("CNT01-G2", "Test Grad 2", Constants.CST_iTypeGrad, "Cuisine", "50")) ;
		Constants.dataItems.add (new Item ("CNT01-VR1", "Test VR 1", Constants.CST_iTypeVolet, "Vestiaire", "100")) ;
		Constants.dataItems.add (new Item ("CNT01-VR2", "Test VR 2", Constants.CST_iTypeVolet, "Vestiaire", "25")) ;
	} */
	
	/* getItems depuis fichier */
	public static void getItems (PrintWriter printWriter)
	{
		if (DomoHub.dataItems.size() > 0)
			DomoHub.dataItems.clear() ;
		
		/* pour tests : chargement depuis fichier extract
		try {
     		File myObj = new File("/opt/tomcat/file.txt");
     		Scanner myReader = new Scanner(myObj);
      		while (myReader.hasNextLine()) {
        		String data = myReader.nextLine();
        		// if (printWriter != null) printWriter.println("Data = "+data);
        		stDataShellMQTT = stDataShellMQTT + data ;
    		 }
     		 myReader.close();
    		} catch (FileNotFoundException e) {
        		if (printWriter != null) printWriter.println("!! ERR = "+e.getMessage());
    		}*/
    		
		int rc = MQTT.getEquipementsMQTT () ;
		// printWriter.println("<p> Commande : "+MQTT.stTraceLastURL+ "<br>" + MQTT.stTraceLastReq +"</p>"); 
		// printWriter.println("<p> Erreurs : "+MQTT.stTraceError+ "<br></p>"); 
			
		if (rc == 0)
		{
			stDataShellMQTT = MQTT.stData ;
			//printWriter.println("Retour MQTT : "+stDataShellMQTT+"<br>");
			// Exploitation des donnes dans stDataShellMQTT
			String stState = "";
			String stType = "";
			String stName = "";
			String stLabel = "";
			String stPiece = "";			
			String stLink = "";
			int inType = 0;

			while (!stLink.equals("NoData")) 
			{
				//printWriter.println("<br>---------- stDataShellMQTT start : " + stDataShellMQTT + "<br>");
				stLink = SearchData("link", printWriter); // positionnement au début du prochain "link"
				stState = SearchData("state", printWriter);
				stType = SearchData("type", printWriter);
				stName = SearchData("name", printWriter);
				stLabel = SearchData("label", printWriter);	
				stPiece = "" ;
				stPiece = SearchData("tags", printWriter);
				if (stPiece.equals("NoData")) 
					stPiece = "" ;
				else
					stPiece =stPiece.replace('"',' ').trim() ;
				if (stType.equals("Switch"))
					inType = DomoHub.CST_iTypeLamp;
				else if (stType.equals("Dimmer"))
					inType = DomoHub.CST_iTypeGrad;
				else if (stType.equals("Rollershutter"))
					inType = DomoHub.CST_iTypeVolet;
				if ((inType > 0) && (!stName.startsWith("Amazon")))
					DomoHub.dataItems.add (new Element (stName, stLabel, inType, stPiece, stState)) ;
				//printWriter.println("iOffsetShellMQTT End : " + iOffsetShellMQTT + "<br>");
				//if (printWriter != null) printWriter.println("Final Out : Name:" + stName + " -- State:" + stState + " -- Type:" + stType + " -- Label:" + stLabel+" -- Piece:"+stPiece+"<br>");
				inType = 0;
				
			}
		}
	} 
	
	private static String SearchData (String stSearch, PrintWriter printwriter)
	{
		//printwriter.println("----------------- Search : \"" + stSearch + "\" ----------------------------<br>");
		String stData = "";
		int index = stDataShellMQTT.indexOf(stSearch);
		
		if (index < 0)
		{
			//printwriter.println(".. Pas trouvé<br>");
			return "NoData";
		}
		//printwriter.println("index of \"" + stSearch + "\" is : " + index + "<br>");
		stDataShellMQTT = stDataShellMQTT.substring(index+stSearch.length()+3).trim();
		iOffsetShellMQTT = index;
		stData = GetData(stSearch, printwriter);
		stDataShellMQTT = stDataShellMQTT.substring(iOffsetShellMQTT);
		//printwriter.println("<br>-------------------------------------<br>" + stDataShellMQTT + "<br>-------------------------------------<br>");
		return stData;
	}
	
	public static String GetData (String stSearch, PrintWriter printwriter)
	{
		String stData = "";
		
		// la fin de l'élément peut être " ou ]
		int index1 = stDataShellMQTT.indexOf("]");
		int index2 = stDataShellMQTT.indexOf("\",");
		int indexFin = 0 ;
		
		if (index2 == -1)
			indexFin = index1 ; 
		else
		{
			if (index1 < index2)
				indexFin = index1 ;
			else
				indexFin = index2 ;
		}
		if (indexFin < 0)
		{
			return "NoData";
		}
		//printwriter.println("index end of \"" + stSearch + "\" is : " + indexFin + "<br>");
		stData = stDataShellMQTT.substring(0, indexFin);
		iOffsetShellMQTT = indexFin;
		//printwriter.println("iOffSetShellQTT end :" + iOffsetShellMQTT + "<br>");
		return stData;
	}
	
	
	public static ArrayList<String> getListePieces ()
	{
		ArrayList<String> listePieces = new ArrayList<String>(); ;
		for (int i=0; i<DomoHub.dataItems.size(); i++)
		{
			String myPiece = DomoHub.dataItems.get(i).stPiece ; 
			boolean bTrouve = false ;
			if (listePieces != null)
			{
				for (int j = 0 ; j< listePieces.size();j++)
				{
					if (listePieces.get(j).equals(myPiece))
						bTrouve = true;
				}
			}
			if (bTrouve == false)
				listePieces.add(myPiece) ;
		}
		return listePieces ;
	}
	
	
	public static int lanceShellMQTT ( PrintWriter printWriter , String ast_URL_MQTT, boolean abGET, String astSetData, String astToken)
	{
		int     iCodeRet  = 0 ;
		String  stLigne   = "" ;  // buffer temporaire
		Process pShell    = null ;
		List<String> command = new ArrayList<String>()  ;
		
		//String [] command = {"curl", ast_URL_MQTT, "Accept:application/json", "-u" ,astToken+":" };
		// curl -X PUT --header "Content-Type: text/plain" --header "Accept: application/json" -d "ON" "http://openhab:8080/rest/items/Heater_Power/state"
		
		command.add("curl") ; 
		command.add("-s");
		command.add("-X") ;
		if (abGET == true)
			command.add("GET") ;
		else
		{
			command.add("PUT") ;
			command.add("--header") ;
			command.add("Content-Type: text/plain") ;
		}
		if (astSetData.equals("") == false)
		{
			command.add("-d") ;
			command.add( astSetData ) ;
		}
		command.add(ast_URL_MQTT);
		command.add("-H") ;
		command.add("Accept:application/json") ;
		command.add("-u");
		command.add( astToken+":" );

		// Trace
		String stTrace = "" ;
		for (int i = 0 ; i < command.size(); i++) 
			stTrace = stTrace + "<" + command.get(i)+"> " ;
		
		ProcessBuilder process = new ProcessBuilder(command); 

		//GlobData.myTrace.addTrace("lanceShellMQTT, url = " + ast_URL_MQTT);
		// Nettoie la structure globale
		stDataShellMQTT = "" ;
		iOffsetShellMQTT = 0 ;
		
		try {
			//pShell = Runtime.getRuntime().exec(astCommandLinux);
			pShell = process.start() ;

			BufferedReader br_in  = new BufferedReader(new InputStreamReader(pShell.getInputStream()));
			BufferedReader br_err = new BufferedReader(new InputStreamReader(pShell.getErrorStream()));

			while ((stLigne = br_in.readLine()) != null) 
            {
                //GlobData.myTrace.addTrace("lanceShellMQTT, lit(in) : " + stLigne);
                stDataShellMQTT = stDataShellMQTT + stLigne ;
            }
			while ((stLigne = br_err.readLine()) != null) 
            {
               if (printWriter != null) printWriter.println("lanceShellMQTT, lit(err) : " + stLigne);
            }
            pShell.waitFor();
            //System.out.println ("lanceShellMQTT, exit : " + pShell.exitValue());
            pShell.destroy();
        } catch (Exception e) {
            if (printWriter != null) printWriter.println("lanceShellMQTT: ERREUR : "+e.getMessage()) ;
        	iCodeRet = 84 ;
        }

		//GlobData.myTrace.addTrace("lanceShellMQTT : OUT : "+stDataShellMQTT) ;

		return iCodeRet ;
	}


}
