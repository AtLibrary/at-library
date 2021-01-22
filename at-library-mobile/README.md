at-library-mobile
=========================

Настройка проекта
====================
Подключите зависимость
```xml
<dependency>
      <groupId>ru</groupId>
      <artifactId>at-library-mobile</artifactId>
      <version>22.01.2021</version>
</dependency>
```

Примеры шагов
=======================
```gherkin
# language: ru
@mobile
Функционал: Тестирование мобильного приложения

  Сценарий: Проверка общей суммы всех покупок
    Когда экран "Главный" загрузился
    И выполнено нажатие на кнопку "iphone"
    И выполнено нажатие на кнопку "mouse"
    И выполнено нажатие на кнопку "ps4"
    И выполнено нажатие на кнопку "photo"
    И выполнено нажатие на кнопку "keyboard"
    Тогда значение поля "money" равно "117300"
```

Работа с экраном
====================
При написании сценария для работы с элементами экрана его необходимо задать как текущий.
Таким образом можно получить доступ к методам взаимодействия с элементами, описанным в CorePage.

Новую текущий экран можно установить шагом
```gherkin
Когда экран "<Имя экрана>" загрузилась
```

Для экрана Главный шаг может выглядеть так
```gherkin
Когда экран "Главный" загрузилась
```

- Каждая экран, с которой предполагается взаимодействие, должна быть описана в классе наследующемся от CorePage.
- Для экрана и ее элементов следует задать имя на русском, через аннотацию Name, чтобы искать можно было именно по русскому описанию.
- Элементы экрана ищутся по локаторам, указанным в аннотации FindBy и должны иметь тип SelenideElement или List<SelenideElement>.

Пример описания экрана:
```java
@Name("Главный")
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

Инициализация экрана
=================================
Если непобходимо создавать собсвенные шаги по работе с mobile элементами'

Пример инициализации экрана "BCS demo аккаунт":
```java
public class HomePageSteps {
    private CoreScenario coreScenario = CoreScenario.getInstance();

    @И("выбор всех товаров и проверка обшей суммы: \"([^\"]*)\"")
    public void loginSystem(String money) {
        money = getPropertyOrStringVariableOrValue(money);
        HomePage homePage =
                (HomePage) coreScenario.getPage("Главный");

        homePage.getElement("iphone").click();
        homePage.getElement("mouse").click();
        homePage.getElement("ps4").click();
        homePage.getElement("photo").click();
        homePage.getElement("notebook").click();

        homePage.getElement("money").shouldHave(Condition.exactText(money));
    }
}

```

- Для экрана инициализируется карта ее элементов - это те поля, что помечены аннотацией Name.
- Кроме того, осуществляется проверка, что загружена требуемая экран.
- экран считается загруженной корректно, если за отведенное по умолчанию время были загружены основные ее элементы. (по умолчанию проверка загрузки элментов отключена) Вклчается параметром:
```mvn
-Dappeared=true
```
- Основными элементами являются поля класса экрана с аннотацией Name, но без аннотации Optional.
- Аннотация Optional указывает на то, что элемент является не обязательным для принятия решения о загрузке экрана.
- Например, если на экране есть список, который раскрывается после нажатия не него, т.е. видим не сразу после загрузки экрана, его можно пометить как Optional.
- Реализована возможность управления временем ожидания появления элемента на экране.
- Чтобы установить timeout, отличный от базового, нужно добавить в properties строку: waitingAppearTimeout=150000

Доступ к элементам экрана
============================
Данные строки позволяют по имени элемента найти его в карте элементов текущей экрана.

```java
homePage.getElement("iphone").click();
homePage.getElement("mouse").click();
homePage.getElement("ps4").click();
homePage.getElement("photo").click();
homePage.getElement("notebook").click();

homePage.getElement("money").shouldHave(Condition.exactText(money));
 ```


Блоки на экране
============================
Реализована возможность описывать блоки на экране (Page Element)

Например:
```java
@FindBy(className = "header")
@Name("Шапка экрана")
public HeaderBlock header;
```
При загрузке экрана будут учитываться элементы, описанные в блоке


После подключения всех плагинов и зависимостей вы можете запускать проект автотестов командами:
=========================


- Запуск удаленно на Selenoid
```mvn
clean 
test 
-DplatformName=iOS 
-DdeviceName="iPhone 6s" 
-DplatformVersion=12.4 
-Dapp=ru.admitadteam.SimpleScoreSwift 
-Dselenide.remote=http://localhost:4444/wd/hub/ 
-Dproxy=172.18.62.68:8080 
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

```mvn
-DplatformName=iOS - передается платформа на которой будут запущены тесты
```

```mvn
-DdeviceName="iPhone 6s" - передается имя девайса на котором будут запущены тесты
```

```mvn
-DplatformVersion=12.4 - передается версия платформы на которой будут запущены тесты
```

```mvn
-Dapp=ru.admitadteam.SimpleScoreSwift - передается название проекта который будет запущен для прогона тестов
```

```mvn
-Dselenide.remote=http://localhost:4444/wd/hub/ -Dproxy=172.18.62.68:8080 - для запуска тестов на selenoid
```
