at-library
=========================

Quick start
=========================
Подробное описание как писать Автотесты. От установки java до создания отчета с результатом прогона АТ

Ссылка на confluence:

https://jira.bcs.ru:4464/confluence/display/QA/Quick+start

Если нет доступа, то необходимо завести СЗ на доступ к space: Quality Assurance

BDD библиотека
=======================
BDD библиотека шагов для тестирования на основе:

- cucumber
- selenide
- rest-assured
- webdrivermanager
- appium
- allure

Тесты пишутся на русском языке и представляют собой пользовательские сценарии, которые являются пользовательской документации на приложение.

Архитектура проекта
====================
Проект разбит на 4 модуля:

- at-library-api - шаги для написания API тестов
- at-library-core - общий набор шагов (подключается по умлючанию при подключении любого модуля с шагами)
- at-library-mobile - шаги для написания MOBILE тестов
- at-library-web - шаги для написания WEB тестов

В каждом модуле создан файл README.md описывающий подключение и работу с этим модулем.
В корневом README.md описано подключение необходимых репозитория\плагинов в pom.xml


Подключение репозиториев:
====================

```xml
    <distributionManagement>
        <snapshotRepository>
            <id>snapshots</id>
            <name>s-cicd-artif-01.global.bcs-snapshots</name>
            <url>https://artifactory.gitlab.bcs.ru/artifactory/bcs-main-snapshots</url>
        </snapshotRepository>
        <repository>
            <id>bcs-main-releases</id>
            <url>https://artifactory.gitlab.bcs.ru/artifactory/bcs-main-releases</url>
        </repository>
    </distributionManagement>
    <repositories>
        <repository>
            <id>bcs-main-releases</id>
            <url>https://artifactory.gitlab.bcs.ru/artifactory/bcs-main-releases</url>
            <releases>
                <enabled>true</enabled>
            </releases>
            <snapshots>
                <enabled>false</enabled>
            </snapshots>
        </repository>
        <repository>
            <id>snapshots</id>
            <name>s-cicd-artif-01.global.bcs-snapshots</name>
            <url>https://artifactory.gitlab.bcs.ru/artifactory/bcs-main-snapshots</url>
            <releases>
                <enabled>false</enabled>
            </releases>
            <snapshots>
                <enabled>true</enabled>
            </snapshots>
        </repository>
    </repositories>
```

Подключение плагинов:
====================

Настроенный для UTF-8 плагин компиляции:
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.8.0</version>
    <configuration>
        <encoding>UTF-8</encoding>
        <source>1.8</source>
        <target>1.8</target>
    </configuration>
</plugin>
```
Запускает тесты и генерирует отчёты по результатам их выполнения:

В threadCount указыается количество потоков
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>2.22.1</version>
    <configuration>
        <threadCount>1</threadCount>
        <parallel>classes</parallel>
        <testFailureIgnore>true</testFailureIgnore>
        <argLine>
            -javaagent:"${settings.localRepository}/org/aspectj/aspectjweaver/1.9.1/aspectjweaver-1.9.1.jar"
            -Dcucumber.options="
            --plugin io.qameta.allure.cucumber4jvm.AllureCucumber4Jvm --plugin com.epam.reportportal.cucumber.ScenarioReporter"
        </argLine>
    </configuration>
    <dependencies>
        <dependency>
            <groupId>org.aspectj</groupId>
            <artifactId>aspectjweaver</artifactId>
            <version>1.9.1</version>
        </dependency>
    </dependencies>
</plugin>
```
Для просмотра allure отчетов:
```xml
<plugin>
    <groupId>io.qameta.allure</groupId>
    <artifactId>allure-maven</artifactId>
    <version>2.10.0</version>
    <configuration>
        <reportVersion>2.10.0</reportVersion>
        <resultsDirectory>allure-results</resultsDirectory>
    </configuration>
</plugin>
```


После подключения всех полагинов и зависимостей вы можете соберить проект командами:
=========================
Запуск локально на ubuntu
```mvn
clean test -Dselenide.browser=chrome  -Djava.net.useSystemProxies=true allure:serve
```

Запуск локально на windows
```mvn
clean test -Dselenide.browser="internet explorer" -Dwebdriver.ie.driver="C:\\Program Files\\Selenium\\Drivers\\IEDriver\\IEDriverServer.exe" allure:serve
```
Имена ключей для прописавание path к разным браузерам:
```
"webdriver.chrome.driver"
"webdriver.edge.driver"
"webdriver.ie.driver"
"webdriver.opera.driver"
"phantomjs.binary.path"
"webdriver.gecko.driver"
``` 

Запуск удаленно на Selenoid
```mvn
clean test -Dselenide.browser="chrome" -Dremote=http://test:test-password@selenoid.t-global.bcs:4444/wd/hub/ -Dproxy=http://172.18.62.68:8080 allure:serve
clean test -Dselenide.browser="internet explorer" -Dremote=http://test:test-password@selenoid.t-global.bcs:4444/wd/hub/ -Dproxy=http://172.18.62.68:8080 allure:serve
```
Запуск тестов с тегами (И)
```mvn
clean test allure:serve -Dcucumber.options="--tags @api --tags @web --plugin io.qameta.allure.cucumber4jvm.AllureCucumber4Jvm"
```
Запуск тестов с тегами (ИЛИ)
```mvn
clean test allure:serve -Dcucumber.options="--tags @api,@web --plugin io.qameta.allure.cucumber4jvm.AllureCucumber4Jvm"
```

Просмотр в Selenoid:
=========================
```url
http://selenoid.t-global.bcs/#/
```

Просмотр отчета в ReportPortal:
=========================
```url
https://reportportal.t-global.bcs/
```

Объяснение:
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

```mvn
-Dbrowser=chrome - использовать браузер chrome для прогона тестов
```

```mvn
-Djava.net.useSystemProxies=true - установив для этого свойства значение true, использовать настройки прокси-сервера системы
```
```mvn
-Dremote=http://selenoid.t-global.bcs:4444/wd/hub/ -Dproxy=http://172.18.62.68:8080 - для запуска тестов на selenoid
```
Чтобы установить базовый url(для api и ui тестов) его можно указать в application.properties по ключу baseURI=https://ef.tusvc.bcs.ru
или передать параметром (если передан параметр и присутсивует в application.properties то будет использован тот что передан параметром)

```mvn
-DbaseURI=https://url.you.need
