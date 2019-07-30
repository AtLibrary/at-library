at-library
=========================

Quick start
=========================
https://jira.bcs.ru:4464/confluence/pages/viewpage.action?pageId=158542586

Если нет доступа, то завести СЗ на доступ к space: Quality Assurance

Настройка проекта
====================

Подключение репозитория:
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
Подключите одну из зависимостей(Зависит от проекта)
```xml
<dependency>
      <groupId>ru.bcs</groupId>
      <artifactId>at-library-mobile</artifactId>
      <version>14.07.2019</version>
</dependency>
```

BDD библиотека
=======================
BDD библиотека шагов для тестирования на основе cucumber/selenium/appium.
Тесты пишутся на русском языке и представляют собой пользовательские сценарии, которые могут выступать в качестве пользовательской документации на приложение.

Для написания тестового сценария достаточно подключить библиотеку и воспользоваться любым готовым шагом

Например:
```gherkin
# language: ru
@mobile
Функция: Тестирование мобильного приложения

  Сценарий: Проверка общей суммы всех покупок
    Когда страница "Домашняя" загрузилась
    И выполнено нажатие на кнопку "iphone"
    И выполнено нажатие на кнопку "mouse"
    И выполнено нажатие на кнопку "ps4"
    И выполнено нажатие на кнопку "photo"
    И выполнено нажатие на кнопку "keyboard"
    Тогда значение поля "money" равно "117300"

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
```Когда страница "Домашняя" загрузилась```

Каждая страница, с которой предполагается взаимодействие, должна быть описана в классе наследующемся от CorePage.
Для страницы и ее элементов следует задать имя на русском, через аннотацию Name, чтобы искать можно было именно по русскому описанию.
Элементы страницы ищутся по локаторам, указанным в аннотации FindBy и должны иметь тип SelenideElement или List<SelenideElement>.

Пример описания страницы:
```java
  @Name("Домашняя")
  public class HomePage extends CorePage {
  
      @Name("iphone")
      @FindBy(xpath = "//XCUIElementTypeButton[@name=\"iphone\"]")
      private SelenideElement iphone;
  
      @Name("mouse")
      @FindBy(xpath = "//XCUIElementTypeButton[@name=\"mouse\"]")
      private SelenideElement mouse;
  
      @Name("ps4")
      @FindBy(xpath = "//XCUIElementTypeButton[@name=\"ps4\"]")
      private SelenideElement ps4;
  
      @Name("photo")
      @FindBy(xpath = "//XCUIElementTypeButton[@name=\"photo\"]")
      private SelenideElement photo;
  
      @Name("keyboard")
      @FindBy(xpath = "//XCUIElementTypeButton[@name=\"keyboard\"]")
      private SelenideElement keyboard;
  
      @Name("notebook")
      @FindBy(xpath = "//XCUIElementTypeButton[@name=\"notebook \"]\n")
      private SelenideElement notebook;
  
      @Name("money")
      @FindBy(xpath = "//XCUIElementTypeStaticText[@name=\"money\"]")
      private SelenideElement money;
      
      @Name("В корзину")
      @FindBy(xpath = "//XCUIElementTypeButton[@name=\"add_to_cart\"]")
      private SelenideElement addToCart;
  
      @Name("Купить")
      @FindBy(xpath = "//XCUIElementTypeButton[@name=\"buy_now_button\"]")
      private SelenideElement buyNowButton;
  }
```

Инициализация страницы
Если непобходимо создавать собсвенные шаги 

Пример инициализации страницы "Депозиты":
```java
HomePage page = (HomePage) getCurrentPage();
CoreScenario.setCurrentPage(page.initialize().appeared());
```

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

```java
CoreScenario.getCurrentPage().getElement("Открыть депозит")
CoreScenario.getCurrentPage().getElementsList("Список депозитов")
 ```
Блоки на странице
============================
Реализована возможность описывать блоки на странице (Page Element)
Например:
```java
@FindBy(className = "header")
@Name("Шапка страницы")
public HeaderBlock header;
```
При загрузке страницы будут учитываться элементы, описанные в блоке


Отображение в отчете справочной информации
============================================

Для того, чтобы в отчете появился блок Output с информацией, полезной для анализа отчета, можно воспользоваться следующим методом
 ```java
CoreScenario.write("Текущий URL = " + currentUrl + " \nОжидаемый URL = " + expectedUrl);
 ```

Использование переменных
=========================
Иногда есть необходимость использовать значения из одного шага в последующих.
Для этого реализовано хранилище переменных в CoreScenario.
Для сохранения/изъятия переменных используются методы setVar/getVar.

Сохранение переменной в хранилище:
```
CoreScenario.setVar(<имя переменной>, <значение переменной>);
```

Получение значения переменной из хранилища:
```
CoreScenario.getVar(<имя переменной>)
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