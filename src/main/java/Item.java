import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Item {
	public String stIdItem ; 
	public String stLibItem ;
	public String stPiece ; 
	public int    iTypeItem ; 
	public String stState ; 

	private static String stDataShellMQTT ; 
	private static int    iOffsetShellMQTT ;
	
	public Item (String astIt, String astLib, int aiType , String astPiece , String astState)
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
		if (Constants.dataItems.size() > 0)
			Constants.dataItems.clear() ;
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
    		}
    		
    		/*
    		 * 	if (myItem.iTypeAPI == Cst_API_OPENHAB)
										{
											String stCURL = Config.get_GlobalParam("DomoSerial", "Curl_OpenHab") ;
											// zzz stRequete = String.format("curl -s -X 'PUT' --header 'Content-Type: text/plain' -H 'Accept: application/json' -d '%s' 'http://%s/rest/items/%s/state'  -u %s: ",
											//		stVRTargetMQTT_modif, stCURL , myItem.stUIDBus , stToken );
											// Exemple requ get equipements : String st_URL_MQTT = String.format("http://%s/rest/items?recursive=false", stCURL);
											stRequete = String.format("http://%s/rest/items/%s/state -d '%s' ", stCURL , myItem.stUIDBus ,stVRTargetMQTT_modif  );
										}
										if (stRequete.length() > 0)
											coderet = lanceShellMQTT ( stRequete , true, "" , stToken) ;*/
			
			// Exploitation des donnes dans stDataShellMQTT
			printWriter.println("Init var GO<br>");
			String stState = "";
			String stType = "";
			String stName = "";
			String stLabel = "";
			String stLink = "";
			int inType = 0;
			printWriter.println("Init var OK<br>");
			while (!stState.equals("NoData")) 
			{
				//printWriter.println("<br>iOffsetShellMQTT start : " + iOffsetShellMQTT + "<br>");
				stLink = SearchData("link", printWriter);
				stState = SearchData("state", printWriter);
				stType = SearchData("type", printWriter);
				stName = SearchData("name", printWriter);
				stLabel = SearchData("label", printWriter);
				if (stType.equals("Switch"))
					inType = Constants.CST_iTypeLamp;
				else if (stType.equals("Dimmer"))
					inType = Constants.CST_iTypeGrad;
				else if (stType.equals("Rolershutter"))
					inType = Constants.CST_iTypeVolet;
				if (inType > 0)
					Constants.dataItems.add (new Item (stName, stLabel, inType, "NULL", stState)) ;
				//printWriter.println("iOffsetShellMQTT End : " + iOffsetShellMQTT + "<br>");
				if (printWriter != null) printWriter.println("Final Out : <br> Name : " + stName + "<br>State : " + stState + "<br>Type : " + stType + "<br> Label : " + stLabel);
				inType = 0;
				
			}

			
			
	} 
	
	private static String SearchData (String stSearch, PrintWriter printwriter)
	{
		//printwriter.println("----------------- Search : \"" + stSearch + "\" ----------------------------<br>");
		String stData = "";
		int index = stDataShellMQTT.indexOf(stSearch);
		
		if (index < 0)
			return "NoData";
		//printwriter.println("index of \"" + stSearch + "\" is : " + index + "<br>");
		stDataShellMQTT = stDataShellMQTT.substring(index+5+3);
		iOffsetShellMQTT = index;
		stData = GetData(stSearch, printwriter);
		stDataShellMQTT = stDataShellMQTT.substring(iOffsetShellMQTT);
		//printwriter.println("<br>-------------------------------------<br>" + stDataShellMQTT + "<br>-------------------------------------<br>");
		return stData;
	}
	
	public static String GetData (String stSearch, PrintWriter printwriter)
	{
		String stData = "";
		
		int indexFin = stDataShellMQTT.indexOf("\",");
		//printwriter.println("index end of \"" + stSearch + "\" is : " + indexFin + "<br>");
		stData = stDataShellMQTT.substring(0, indexFin);
		iOffsetShellMQTT = indexFin;
		//printwriter.println("iOffSetShellQTT end :" + iOffsetShellMQTT + "<br>");
		return stData;
	}
	
	
	public static ArrayList<String> getListePieces ()
	{
		ArrayList<String> listePieces = new ArrayList<String>(); ;
		for (int i=0; i<Constants.dataItems.size(); i++)
		{
			String myPiece = Constants.dataItems.get(i).stPiece ; 
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
