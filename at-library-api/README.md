at-library-api
=========================

Настройка проекта
====================
Подключите зависимость
```xml
<dependency>
      <groupId>ru.bcs</groupId>
      <artifactId>at-library-api</artifactId>
      <version>08.11.2019</version>
</dependency>
```

Примеры шагов с REST запросами
=======================

В библиотеке реализована возможность отправки REST запросов и сохранения ответа в переменную.

Поддерживаются следующие типы запросов: GET, POST, PUT, DELETE.
   ```gherkin
    Когда выполнен POST запрос на URL "url.token" с headers и parameters из таблицы. Полученный ответ сохранен в переменную "token_response"
      | HEADER         | Content-Type | application/x-www-form-urlencoded |
      | FORM_PARAMETER | username     | ОСКО.login                        |
      | FORM_PARAMETER | password     | ОСКО.password                     |
      | FORM_PARAMETER | grant_type   | password                          |
      | FORM_PARAMETER | client_id    | ef-front                          |
  ```
В таблице переменных поддерживаются типы: header, parameter, body
Для body-параметра сейчас поддерживается как работа с телом запроса, хранящимся в папке restBodies, так и с указанием текста body в самом шаге в соответствующей ячейке
Значения параметров таблицы и частей url можно указывать в application.properties
