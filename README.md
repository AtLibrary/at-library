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

- Плагин компиляции:
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
- Плагин запуска тестов и генерирует отчёты по результатам их выполнения:
- В threadCount указыается количество потоков
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
- Плагин для просмотра allure отчетов:
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

Просмотр отчета о выполнении в ReportPortal:
=========================
```url
https://reportportal.t-global.bcs/
```
