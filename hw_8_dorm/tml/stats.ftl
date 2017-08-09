<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta charset="UTF-8"/>
    <title>Welcome</title>
</head>
<body>
<!-- send new value here -->
<!-- read value here -->
<!-- show stats here -->

<table>
    <#list stats as stat>
        <tr>
            <td>${stat.key}</td>
            <td>${stat.value}</td>
        </tr>
    </#list>
</table>

<!-- operations log here -->
</body>
</html>