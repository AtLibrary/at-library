At-Library-Core
=========================

Настройка проекта
====================

Подключение репозитория:
```xml
<repositories>
    <repository>
        <id>autotest-mvn-repo</id>
        <url>https://raw.github.com/Antonppavlov/autotest/mvn-repo/</url>
        <snapshots>
            <enabled>true</enabled>
            <updatePolicy>always</updatePolicy>
        </snapshots>
    </repository>
</repositories>
```
Подключение завимости:
```xml
<dependency>
    <groupId>ru.bcs</groupId>
    <artifactId>at-library-core</artifactId>
    <version>1.0.0</version>
</dependency>
```

BDD библиотека
=======================
BDD библиотека шагов для тестирования на основе cucumber и selenide.
Тесты пишутся на русском языке и представляют собой пользовательские сценарии, которые могут выступать в качестве пользовательской документации на приложение.

Для написания тестового сценария достаточно подключить библиотеку и воспользоваться любым готовым шагом из ru.bcs.at.library.core.steps

Например:
```
Функционал: Страница депозитов
  Сценарий: Открытие депозита
    Допустим совершен переход на страницу "Депозиты" по ссылке "depositsUrl"
    Когда выполнено нажатие на кнопку "Открыть депозит"
    Тогда страница "Открытие депозита" загрузилась
```


application.properties
=======================
Для указания дополнительных параметров или тестовых данных создайте в своем проекте файл application.properties
в main/java/resources

Работа со страницами
====================
Для работы с элементами страницы ее необходимо задать как текущую.
Таким образом можно получить доступ к методам взаимодействия с элементами, описанным в CorePage.

Новую текущую страницу можно установить шагом
```Когда страница "<Имя страницы>" загрузилась```

Для страницы депозитов шаг может выглядеть так
```Когда страница "Депозиты" загрузилась```

Каждая страница, с которой предполагается взаимодействие, должна быть описана в классе наследующемся от CorePage.
Для страницы и ее элементов следует задать имя на русском, через аннотацию Name, чтобы искать можно было именно по русскому описанию.
Элементы страницы ищутся по локаторам, указанным в аннотации FindBy и должны иметь тип SelenideElement или List<SelenideElement>.

Пример описания страницы:
```java
    @Name("Депозиты")
    public class DepositsPage extends CorePage {

        @FindBy(css = ".deposit_open")
        @Name("Открыть депозит")
        private SelenideElement depositOpenButton;

        @FindBy(css = ".deposit_close")
        @Name("Закрыть депозит")
        private SelenideElement depositCloseButton;

        @FindBy(css = ".deposit_list")
        @Name("Список депозитов")
        private List<SelenideElement> depositList;
    }
```

Инициализация страницы
Страница инициализируется каждый раз, когда вызываются методы initialize(<Имя класса страницы>.class)

Пример инициализации страницы "Депозиты":
```
DepositsPage page = (DepositsPage) getCurrentPage();
CoreScenario.setCurrentPage(page.initialize().appeared());
```

Пример получения конкретной страницы:
```
DepositsPage page = CoreScenario.getPage(DepositsPage.class);
```

Другой способ работы с методами страницы - это использование CoreScenario.withPage
Пример использования: ```withPage(TestPage.class, page -> { some actions with TestPage methods});```

Для страницы инициализируется карта ее элементов - это те поля, что помечены аннотацией Name.
Кроме того, осуществляется проверка, что загружена требуемая страница.
Страница считается загруженной корректно, если за отведенное по умолчанию время были загружены основные ее элементы.
Основными элементами являются поля класса страницы с аннотацией Name, но без аннотации Optional.
Аннотация Optional указывает на то, что элемент является не обязательным для принятия решения о загрузке страницы.
Например, если на странице есть список, который раскрывается после нажатия не него, т.е. видим не сразу после загрузки страницы,
его можно пометить как Optional.
Реализована возможность управления временем ожидания появления элемента на странице.
Чтобы установить timeout, отличный от базового, нужно добавить в application.properties строку
waitingAppearTimeout=150000

Доступ к элементам страницы
============================
Данные строки позволяют по имени элемента найти его в карте элементов текущей страницы.

```
CoreScenario.getCurrentPage().getElement("Открыть депозит")
CoreScenario.getCurrentPage().getElementsList("Список депозитов")
 ```
Блоки на странице
============================
Реализована возможность описывать блоки на странице (Page Element)
Например:
```
@FindBy(className = "header")
@Name("Шапка страницы")
public HeaderBlock header;
```
При загрузке страницы будут учитываться элементы, описанные в блоке

Screenshots
============================
Реализован кастомный StepFormatter. При подключении его к проеку с тестами, становится достуна опция снятия скриншотов после желаемого или каждого шага. ```@CucumberOptions(format = {"pretty", "ru.bcs.at.library.core.core.formatters.StepFormatter"})```


Аннотация @Screenshot, указанная над кастомным тестовым шагом, позволит добавить скриншот после прохождения этого шага в отчет.


Есть также возможность получать скриншоты после каждого шага всех сценариев. Для этого необходимо задать системную переменную takeScreenshotAfterSteps=true.

Работа с REST запросами
=======================

В библиотеке реализована возможность отправки REST запросов и сохранения ответа в переменную.

Поддерживаются следующие типы запросов: GET, POST, PUT, DELETE.
   ```Когда выполнен POST запрос на URL "{depositsApi}deposits/{docNumber}/repay" с headers и parameters из таблицы. Полученный ответ сохранен в переменную
       | header | applicationId | test            |
       | header | customerId    | <userCus>       |
       | body   | repayment     | <fileForCreate> |
  ```
В таблице переменных поддерживаются типы: header, parameter, body
Для body-параметра сейчас поддерживается как работа с телом запроса, хранящимся в папке restBodies, так и с указанием текста body в самом шаге в соответствующей ячейке
Значения параметров таблицы и частей url можно указывать в application.properties

Отображение в отчете справочной информации
============================================

Для того, чтобы в отчете появился блок Output с информацией, полезной для анализа отчета, можно воспользоваться следующим методом
 ```
CoreScenario.write("Текущий URL = " + currentUrl + " \nОжидаемый URL = " + expectedUrl);
 ```

Проверка логического выражения
===============================
У нас есть шаг, который например может выглядеть так:
 ```
Тогда верно что "amountToPay == amountMonthly + penalty + 100"
 ```
Важно отметить, что равенство проверяется использованием операнда "==", неравенство, как "!="

Использование переменных
=========================
Иногда есть необходимость использовать значения из одного шага в последующих.
Для этого реализовано хранилище переменных в CoreScenario.
Для сохранения/изъятия переменных используются методы setVar/getVar.

Сохранение переменной в хранилище:
```CoreScenario.setVar(<имя переменной>, <значение переменной>);```

Получение значения переменной из хранилища:
```CoreScenario.getVar(<имя переменной>)```

Blacklist
=========================================
Иногда есть необходимость добавить определенные ресурсы в черный список.
Для этого была реализована возможность создать файл без расширения ```blacklist``` . Его необходимо положить в ```src/main/java/resources/```

```blacklist``` предназначен для хранения нежелательных, указанных пользователем ресурсов, которые будут добавлены в черный список при запуске тестов через ```CustomDriverProvide```.

Формат записи - каждый новый ресурс с новой строки, например:

     .*ru.fp.kaspersky-labs.com.*
     http://google.com/ 200
При необходимости можно указывать возвращаемый статус код через пробел, например:
```http://google.com/ 200```

Если статус код не указан, по умолчанию будет присвоен ```404```

Краткое описание главных классов
=================================

```ru.bcs.at.library.core.cucumber.api.CoreEnvironment```
Используется для хранения страниц и переменных внутри сценария
scenario - Сценарий из Cucumber.api, с которым связана среда

```ru.bcs.at.library.core.cucumber.api.CorePage```
Класс для реализации паттерна PageObject. Тут описаны основные методы взаимодействия с элементами страницы

```ru.bcs.at.library.core.cucumber.api.CoreScenario```
Позволяет заполнить хранилище переменных, существующее в рамках одного сценария, значениями и читать эти значения при необходимости.

```ru.bcs.at.library.core.steps.DefaultApiSteps```
Шаги для тестирования API, доступные по умолчанию в каждом новом проекте

```ru.bcs.at.library.core.steps.DefaultSteps```
Шаги для тестирования UI, доступные по умолчанию в каждом новом проекте

```ru.bcs.at.library.core.steps.InitialSetupSteps```
Хуки предустановок, где происходит создание, закрытие браузера, получение скриншотов


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
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>2.22.1</version>
    <configuration>
        <forkCount>10</forkCount>
        <reuseForks>true</reuseForks>
        <includes>
            <include>**/Parallel*IT.class</include>
        </includes>
        <testFailureIgnore>true</testFailureIgnore>
        <argLine>
            -javaagent:"${settings.localRepository}/org/aspectj/aspectjweaver/1.9.1/aspectjweaver-1.9.1.jar" -Dcucumber.options="--plugin io.qameta.allure.cucumber3jvm.AllureCucumber3Jvm"
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
    <version>2.9</version>
    <configuration>
        <reportVersion>2.7.0</reportVersion>
        <resultsDirectory>allure-results</resultsDirectory>
    </configuration>
</plugin>
```
Для распаралеливания тестов:
```xml
<plugin>
    <groupId>com.github.temyers</groupId>
    <artifactId>cucumber-jvm-parallel-plugin</artifactId>
    <version>5.0.0</version>
    <executions>
        <execution>
            <id>generateRunners</id>
            <phase>generate-test-sources</phase>
            <goals>
                <goal>generateRunners</goal>
            </goals>
            <configuration>
                <glue>
                    <package>ru.bcs.at.library.core</package>
                    <!--<package>подключить пакеты содержатие шаги cucumber</package>-->
                </glue>
                <tags>
                    <tag>@bcs</tag>
                </tags>
                <parallelScheme>SCENARIO</parallelScheme>
                <featuresDirectory>src/test/resources/features/</featuresDirectory>
                <cucumberOutputDir>target/cucumber-parallel</cucumberOutputDir>
            </configuration>
        </execution>
    </executions>
</plugin>
```
Подключение swagger-codegen-maven-plugin:
```xml
<plugin>
    <groupId>io.swagger</groupId>
    <artifactId>swagger-codegen-maven-plugin</artifactId>
    <version>2.3.1</version>
    <executions>
        <execution>
            <goals>
                <goal>generate</goal>
            </goals>
            <configuration>
                <inputSpec>${project.basedir}/src/test/resources/schemas/api-docs.json</inputSpec>
                <language>java</language>
                <configOptions>
                    <generateApiTests>false</generateApiTests>
                    <sourceFolder>src/gen/java/main</sourceFolder>
                </configOptions>
            </configuration>
        </execution>
    </executions>
</plugin>
```
Необходимые плагину завимости:
```xml
<dependency>
    <groupId>com.squareup.okhttp3</groupId>
    <artifactId>okhttp</artifactId>
    <version>3.12.0</version>
</dependency>
<dependency>
    <groupId>com.squareup.okhttp3</groupId>
    <artifactId>logging-interceptor</artifactId>
    <version>3.12.0</version>
</dependency>
<dependency>
    <groupId>io.swagger.core.v3</groupId>
    <artifactId>swagger-annotations</artifactId>
    <version>2.0.6</version>
</dependency>
<dependency>
    <groupId>io.gsonfire</groupId>
    <artifactId>gson-fire</artifactId>
    <version>1.8.3</version>
</dependency>
<dependency>
    <groupId>org.threeten</groupId>
    <artifactId>threetenbp</artifactId>
    <version>1.3.8</version>
</dependency>
```

Подключение swagger-codegen-maven-plugin:
```xml
<plugin>
    <groupId>org.jsonschema2pojo</groupId>
    <artifactId>jsonschema2pojo-maven-plugin</artifactId>
    <version>0.5.1</version>
    <configuration>
        <sourceDirectory>${basedir}/src/test/resources/json/schema</sourceDirectory>
        <outputDirectory>${basedir}/src/main/java</outputDirectory>
        <targetPackage>ru.bcs.at.pojo</targetPackage>
        <generateBuilders>true</generateBuilders>
    </configuration>
    <executions>
        <execution>
            <goals>
                <goal>generate</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
Необходимые плагину завимости:
```xml
<dependency>
    <groupId>commons-lang</groupId>
    <artifactId>commons-lang</artifactId>
    <version>2.6</version>
</dependency>
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-annotations</artifactId>
    <version>2.9.7</version>
</dependency>
```