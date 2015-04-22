<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
</head>
<body>
<h2>Game Record Upload Sample</h2>

<form method="POST" enctype="multipart/form-data">

    <fieldset>
        <table cellspacing="0">
            <tr>
                <th><label>Device ID</label></th>
                <td>
                    <input name="deviceId" size="128"/><br/>
                </td>
            </tr>
            <tr>
                <th><label>Device Version</label></th>
                <td>
                    <input name="deviceVersion" size="128"/><br/>
                </td>
            </tr>
            <tr>
                <th><label>Game ID</label></th>
                <td>
                    <input name="gameId" size="128"/><br/>
                </td>
            </tr>
            <tr>
                <th><label>Game Version</label></th>
                <td>
                    <input name="gameVersion" size="128"/><br/>
                </td>
            </tr>
            <tr>
                <th><label>Record File</label></th>
                <td>
                    <input name="recordFile" type="file"/><br/>
                </td>
            </tr>
            <tr>
                <th></th>
                <td>
                    <input type="submit"/>
                </td>
            </tr>
        </table>
    </fieldset>
</form>
</body>
</html>
