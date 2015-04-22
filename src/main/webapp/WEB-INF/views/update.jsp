<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
</head>
<body>
<h2>Game Record Upload Sample</h2>

<form method="POST">

    <fieldset>
        <table cellspacing="0">
            <tr>
                <th><label>Record File</label></th>
                <td>
                    ${model.recordId}<br/>
                </td>
            </tr>
            <tr>
                <th><label>Device ID</label></th>
                <td>
                    ${model.deviceId}<br/>
                </td>
            </tr>
            <tr>
                <th><label>Device Version</label></th>
                <td>
                    ${model.deviceVersion}<br/>
                </td>
            </tr>
            <tr>
                <th><label>Game ID</label></th>
                <td>
                    ${model.gameId}<br/>
                </td>
            </tr>
            <tr>
                <th><label>Game Version</label></th>
                <td>
                    ${model.gameVersion}<br/>
                </td>
            </tr>
            <tr>
                <th><label>Upload Time</label></th>
                <td>
                    ${model.uploadTime}<br/>
                </td>
            </tr>
            <tr>
                <th><label>Record Desc</label></th>
                <td>
                    <textarea name="recordDesc">${model.recordDesc}</textarea><br/>
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
