at-library-api
=========================

Настройка проекта
====================
Подключите зависимость
```xml
<dependency>
      <groupId>ru</groupId>
      <artifactId>at-library-api</artifactId>
      <version>29.04.2021</version>
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
Значения параметров таблицы и частей url можно указывать в properties

После подключения всех плагинов и зависимостей вы можете запускать проект автотестов командами:
=========================


- Запуск удаленно на Selenoid
```mvn
clean 
test 
allure:serve
```
- Запуск тестов с тегами (И)
```mvn
clean 
test 
-Dcucumber.options="--tags @api --tags @web --plugin io.qameta.allure.cucumber4jvm.AllureCucumber4Jvm --plugin com.epam.reportportal.cucumber.ScenarioReporter"
allure:serve 
```

- Запуск тестов с тегами (ИЛИ)
```mvn
clean
test
-Dcucumber.options="--tags @api,@web --plugin io.qameta.allure.cucumber4jvm.AllureCucumber4Jvm --plugin com.epam.reportportal.cucumber.ScenarioReporter"
allure:serve 
```

Пояснение к командам:
=========================

```mvn
clean - очистка проекта
```

```mvn
test - запуск тестов
```

```mvn
allure:serve - запуск allure отчетов
```
