<!DOCTYPE html>
<html>
<head>
<title>DomoHub - Login </title>
</head>
<body>
<br>
    <form method="POST" action='<%= response.encodeURL("j_security_check") %>'>
        <br><br><br><br>
        <table id="contentlogon">
        <tr><td>
        	<h1> DomoHub <br></h1>
        	<table id="contentgray"  style="width: 100%">
        		<caption> Veuillez vous identifier </caption>
	            <tr><tr><td>&nbsp;</td></tr>
	            <tr>
    	            <th align="right">Identifiant : </th>
        	        <td align="left"><input type="text" name="j_username"></td>
	            </tr>
    	        <tr>
        	        <th align="right">Mot de passe : </th>
            	    <td align="left"><input type="password" name="j_password"></td>
	            </tr>
    	        <tr><td>&nbsp;</td></tr>
    	        <tr>
        	        <td align="right"><input type="submit" class="LPbutton"></td>
            	    <td align="left"><input type="reset" class="LPbutton"></td>
	            </tr><tr><td>&nbsp;</td></tr>
        	</table>
        </td></tr>
        </table>
    </form>
</body>
</html>