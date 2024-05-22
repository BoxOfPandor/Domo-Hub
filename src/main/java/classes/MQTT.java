// Classe MQTT en lien avec OpenHab (via "curl")// Documentation des requêtes : 
// Récupérer tous les items : 
//    curl -X 'GET' 'http://localhost:8095/rest/items?metadata=.%2A&recursive=true' -H 'accept: application/json' <+ autorisations>
//                   http://              /rest/items/%s/state ==> change l'état (avec ' -d "nouvelle_valeur" '
package classes;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MQTT 
{
	public static String         stData    ; // Données en retour de lanceShellMQTT
	public static int            iOffset   ; // Offset lecture en cours dans stDataShellMQTT  
	public static String         stTraceLastURL ; 
	public static String         stTraceLastReq ; 
	public static String         stTraceError ; 

	private static final String  stTokenOpenHab = "oh.DomoSerial2.EyeqPuXaAYFHfMbY9LRuDM46EID8Tkq4qul7EXB5FSbsIWtjylBMVfTs05uILEzq6ByCYtz4HN4vAjjuwpXkkA";
	//private static final String  stURLOpenHab = "http://localhost:8095" ;
	//private static final String  stURLOpenHab = "http://192.168.1.12:8095" ;
	private static final String  stURLOpenHab = "http://famillepouch.freeboxos.fr:38095" ;

	MQTT ()
	{
		stData = "" ;
		iOffset = 0 ;
	}
	
	
	public static int getEquipementsMQTT (  )
	{
		String st_URL_MQTT = String.format("%s/rest/items?recursive=true", stURLOpenHab);
   		int coderet = lanceShellMQTT ( st_URL_MQTT, true, false, "", stTokenOpenHab) ;
		return coderet ;
	}

	public static int setValueMQTT (String astCodeData, String astValueData)
	{
		String st_URL_MQTT = String.format("%s/rest/items/%s", stURLOpenHab, astCodeData);
   		int coderet = lanceShellMQTT ( st_URL_MQTT, false, false, astValueData , stTokenOpenHab) ;
		return coderet ;
	}
	
	public static int listRules () 
	{
		String st_URL_MQTT = String.format("%s/rest/rules?summary=true&staticDataOnly=false", stURLOpenHab );
   		int coderet = lanceShellMQTT ( st_URL_MQTT, true, false,"" , stTokenOpenHab) ;
		return coderet ;
	}
	
	public static int createRule (String stNewRule) 
	{
		String st_URL_MQTT = String.format("%s/rest/rules", stURLOpenHab );
		/* OK String stData = "{\"triggers\":[{\"id\":\"1\",\"configuration\":{\"itemName\":\"CNTXX_K1\",\"state\":\"ON\"},\"type\":\"core.ItemStateChangeTrigger\"}], " +
						"\"conditions\":[], " +
						"\"actions\":[{\"inputs\":{},\"id\":\"2\",\"configuration\":{\"type\":\"application/vnd.openhab.dsl.rule\",\"script\":\"if ((CNTXX_K1.state \u003d\u003d ON) \u0026\u0026 (_ALARME_Activation.state \u003d\u003d ON))\n{\n  _ALARME_Alerte.state \u003d ON ;\n}\n\"},\"type\":\"script.ScriptAction\"}], " +
						"\"configuration\":{}, \"configDescriptions\":[], " +
						"\"uid\":\"DomoHub_CNTXX_K1\",\"name\":\"Alarme_CNTXX_K1\",\"tags\":[],\"visibility\":\"VISIBLE\"} " ; */
		String stData = String.format ( "{\"triggers\":[{\"id\":\"1\",\"configuration\":{\"itemName\":\"%s\",\"state\":\"ON\"},\"type\":\"core.ItemStateChangeTrigger\"}], " +
						"\"conditions\":[], " +
						"\"actions\":[{\"inputs\":{},\"id\":\"2\",\"configuration\":{\"type\":\"application/vnd.openhab.dsl.rule\",\"script\":\"if ((%s.state \u003d\u003d ON) \u0026\u0026 (_ALARME_Activation.state \u003d\u003d ON))\n{\n  _ALARME_Alerte.state \u003d ON ;\n}\n\"},\"type\":\"script.ScriptAction\"}], " +
						"\"configuration\":{}, \"configDescriptions\":[], " +
						"\"uid\":\"DomoHub_%s\",\"name\":\"Alarme_%s\",\"tags\":[],\"visibility\":\"VISIBLE\"} " ,stNewRule ,stNewRule ,stNewRule ,stNewRule) ;

   		int coderet = lanceShellMQTT ( st_URL_MQTT, false, false, stData , stTokenOpenHab) ;
		return coderet ;
	}
	
	public static int deleteRule (String stDeleteRule) 
	{
		// Format : curl -X DELETE http://famillepouch.freeboxos.fr:38095/rest/rules/DomoHub_DomoHub_toto \
		//               -H 'accept: */*' -u oh.DomoSerial2.EyeqPuXaAYFHfMbY9LRuDM46EID8Tkq4qul7EXB5FSbsIWtjylBMVfTs05uILEzq6ByCYtz4HN4vAjjuwpXkkA: 
		String st_URL_MQTT = String.format("%s/rest/rules/%s", stURLOpenHab , stDeleteRule);
   		int coderet = lanceShellMQTT ( st_URL_MQTT, false, true, "" , stTokenOpenHab) ;
		return coderet ;
	}

	public static int lanceShellMQTT ( String ast_URL_MQTT, boolean abGET, boolean abDelete, String astSetData, String astToken)
	{
		int     iCodeRet  = 0 ;
		String  stLigne   = "" ;  // buffer temporaire
		Process pShell    = null ;
		List<String> command = new ArrayList<String>()  ;
		
		stTraceLastURL = ast_URL_MQTT ; 
		stTraceLastReq = "" ; 
		stTraceError = "" ;

		command.add("curl") ; 
		command.add("-s"); // silent = pas de trace
		// command.add("-v"); // s'il faut tracer le fonctionnement
		command.add("-X") ; // exécute
		if (abGET == true)
		{
			command.add("GET") ;
			command.add(ast_URL_MQTT);
			command.add("-H") ;
			command.add("'accept:application/json'") ;
		}
		else
		{
			if (abDelete == true)
				command.add("DELETE") ; 
			else
				command.add("POST") ; //  	command.add("PUT") ;
		 	command.add( ast_URL_MQTT );
			//command.add("-H") ;
			//command.add("'accept: */*'") ;
			if (abDelete == false)
			{
				command.add("-H") ;
				if (ast_URL_MQTT.contains("/rest/rules"))
					command.add("Content-Type:application/json") ;
				else
					command.add("Content-Type:text/plain") ;
			}
		}
		if (astSetData.equals("") == false)
		{
			command.add("-d") ;
			command.add( astSetData) ;
		}
		command.add("-u");
		command.add( astToken+":" );

		// Trace commande
		for (int i = 0 ; i < command.size(); i++)
			stTraceLastReq = stTraceLastReq+ command.get(i) +" " ;
		
		ProcessBuilder process = new ProcessBuilder(command); 
		
		// Nettoie la structure globale
		stData = "" ;
		iOffset = 0 ;
		
		try {
			pShell = process.start() ;
			BufferedReader br_in  = new BufferedReader(new InputStreamReader(pShell.getInputStream()));
			if (br_in != null)
			{
				while ((stLigne = br_in.readLine()) != null) 
               		stData  = stData  + stLigne ;
			}
			
			BufferedReader br_err = new BufferedReader(new InputStreamReader(pShell.getErrorStream()));
			stTraceError = "" ;
			if (br_err != null)
			{
				while ((stLigne = br_err.readLine()) != null) 
                	stTraceError = stTraceError + stLigne+ " <br> " ;// stLigne contient les erreurs 
			}
			
            pShell.waitFor();
            pShell.destroy();
        } catch (Exception e) {
        	stTraceError = stTraceError + " IOException:"+e.getMessage() ; 
        	iCodeRet = 84 ;
        }

		return iCodeRet ;
	}

}