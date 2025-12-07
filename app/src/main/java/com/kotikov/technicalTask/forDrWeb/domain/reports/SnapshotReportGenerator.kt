package com.kotikov.technicalTask.forDrWeb.domain.reports

import com.kotikov.technicalTask.forDrWeb.presentation.WorkAreaScreen.Target
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun generateHtmlReport(
    deviceInfoMap: Map<String, String>,
    targetList: List<Target>
): String {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
    val currentDate = dateFormat.format(Date())

    val html = """
    <!DOCTYPE html>
    <html>
    <head>
        <meta charset="UTF-8">
        <style>
    body{font-family:Arial,sans-serif;margin:20px;background:#f5f5f5;}
    .report{padding:20px;background:white;border-radius:8px;box-shadow:0 2px 10px rgba(0,0,0,0.1);max-width:100%;overflow-x:auto;}
    h1{color:#333;border-bottom:2px solid #4CAF50;padding-bottom:10px;}
    h2{color:#555;margin-top:25px;border-bottom:1px solid #ddd;padding-bottom:5px;}
    table{border-collapse:collapse;width:100%;margin:15px 0;font-size:14px;table-layout:fixed;word-wrap:break-word;}
    th{background:#4CAF50;color:white;padding:12px 8px;text-align:left;font-weight:bold;}
    td{padding:10px 8px;border-bottom:1px solid #ddd;word-break:break-word;}
    tr:nth-child(even){background:#f9f9f9;}
    .info{padding:10px;background:#e8f5e9;border-radius:5px;margin:10px 0;}
    .footer{margin-top:20px;color:#777;font-size:12px;text-align:center;}
    .hash{font-family:monospace;font-size:12px;color:#666;word-break:break-all;}
    
    /* Колонки с хэшами уже, остальные шире */
    table th:nth-child(3),
    table th:nth-child(5) {width:200px;}
    table th:nth-child(1) {width:40px;}
    table th:nth-child(2) {width:150px;}
    table th:nth-child(4),
    table th:nth-child(6) {width:180px;}
    table th:nth-child(7) {width:180px;}
</style>
    </head>
    <body>
        <div class="report">
            <h1>ОТЧЕТ</h1>
            
            <div class="info">
                <strong>Дата проведения:</strong> $currentDate<br>
                <strong>Формат хэша:</strong> SHA-256<br>
                <strong>Система: Android</strong>  
            </div>
            
            <h2>ТЕСТОВАЯ СРЕДА</h2>
            <table>
                ${
        deviceInfoMap.entries.joinToString("") { (key, value) ->
            "<tr><td><strong>$key:</strong></td><td>$value</td></tr>"
        }
    }
            </table>
            
            <h2>СПИСОК ЦЕЛЕЙ</h2>
            <table>
                <tr>
                    <th>№</th>
                    <th>Название приложения</th>
                    <th>Эталонный хэш</th>
                    <th>Время создания эталонного хэша</th>
                    <th>Актуальный хэш</th>
                    <th>Время создания актуального хэша</th>
                    <th>Пакет</th>
                </tr>
                ${
        targetList.mapIndexed { index, target ->
            val refTime = if (target.referenceHashTimeStamp > 0)
                dateFormat.format(Date(target.referenceHashTimeStamp)) else "—"
            val actTime = if (target.actualHashTimeStamp > 0)
                dateFormat.format(Date(target.actualHashTimeStamp)) else "—"

            """
                    <tr>
                        <td>${index + 1}</td>
                        <td>${target.name}</td>
                        <td class="hash">${target.referenceHash ?: "—"}</td>
                        <td>$refTime</td>
                        <td class="hash">${target.actualHash ?: "—"}</td>
                        <td>$actTime</td>
                        <td>${target.packageName}</td>
                    </tr>
                    """
        }.joinToString("")
    }
            </table>
            
            <div class="footer">
                Отчет сгенерирован автоматически • $currentDate
            </div>
        </div>
    </body>
    </html>
    """.trimIndent()

    println(html)
    return html
}
