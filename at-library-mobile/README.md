at-library-mobile
=========================

Настройка проекта
====================
Подключите зависимость
```xml
<dependency>
      <groupId>ru.bcs</groupId>
      <artifactId>at-library-mobile</artifactId>
      <version>20.07.2019</version>
</dependency>
```

Примеры шагов
=======================
```gherkin
# language: ru
@mobile
Функционал: Тестирование мобильного приложения

  Сценарий: Проверка общей суммы всех покупок
    Когда страница "Домашняя" загрузилась
    И выполнено нажатие на кнопку "iphone"
    И выполнено нажатие на кнопку "mouse"
    И выполнено нажатие на кнопку "ps4"
    И выполнено нажатие на кнопку "photo"
    И выполнено нажатие на кнопку "keyboard"
    Тогда значение поля "money" равно "117300"
```

Работа с страницами
====================
При написании сценария для работы с элементами страницы ее необходимо задать как текущую.
Таким образом можно получить доступ к методам взаимодействия с элементами, описанным в CorePage.

Новую текущую страницу можно установить шагом
```gherkin
Когда страница "<Имя страницы>" загрузилась
```

Для страницы домашняя шаг может выглядеть так
```gherkin
Когда страница "Домашняя" загрузилась
```

- Каждая страница, с которой предполагается взаимодействие, должна быть описана в классе наследующемся от CorePage.
- Для страницы и ее элементов следует задать имя на русском, через аннотацию Name, чтобы искать можно было именно по русскому описанию.
- Элементы страницы ищутся по локаторам, указанным в аннотации FindBy и должны иметь тип SelenideElement или List<SelenideElement>.

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
=================================
Если непобходимо создавать собсвенные шаги по работе с mobile элементами'

Пример инициализации страницы "BCS demo аккаунт":
```java
public class HomePageSteps {
    private CoreScenario coreScenario = CoreScenario.getInstance();

    @И("выбор всех товаров и проверка обшей суммы: \"([^\"]*)\"")
    public void loginSystem(String money) {
        money = getPropertyOrStringVariableOrValue(money);
        HomePage homePage =
                (HomePage) coreScenario.getPage("Домашняя");

        homePage.getElement("iphone").click();
        homePage.getElement("mouse").click();
        homePage.getElement("ps4").click();
        homePage.getElement("photo").click();
        homePage.getElement("notebook").click();

        homePage.getElement("money").shouldHave(Condition.exactText(money));
    }
}

```

- Для страницы инициализируется карта ее элементов - это те поля, что помечены аннотацией Name.
- Кроме того, осуществляется проверка, что загружена требуемая страница.
- Страница считается загруженной корректно, если за отведенное по умолчанию время были загружены основные ее элементы. (по умолчанию проверка загрузки элментов отключена) Вклчается параметром:
```mvn
-Dappeared=true
```
- Основными элементами являются поля класса страницы с аннотацией Name, но без аннотации Optional.
- Аннотация Optional указывает на то, что элемент является не обязательным для принятия решения о загрузке страницы.
- Например, если на странице есть список, который раскрывается после нажатия не него, т.е. видим не сразу после загрузки страницы, его можно пометить как Optional.
- Реализована возможность управления временем ожидания появления элемента на странице.
- Чтобы установить timeout, отличный от базового, нужно добавить в application.properties строку: waitingAppearTimeout=150000

Доступ к элементам страницы
============================
Данные строки позволяют по имени элемента найти его в карте элементов текущей страницы.

```java
homePage.getElement("iphone").click();
homePage.getElement("mouse").click();
homePage.getElement("ps4").click();
homePage.getElement("photo").click();
homePage.getElement("notebook").click();

homePage.getElement("money").shouldHave(Condition.exactText(money));
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


После подключения всех плагинов и зависимостей вы можете запускать проект автотестов командами:
=========================


- Запуск удаленно на Selenoid
```mvn
clean test -DplatformName=iOS -DdeviceName="iPhone 6s" -DplatformVersion=12.2 -Dapp=ru.admitadteam.SimpleScoreSwift -Dselenide.remote=http://test:test-password@selenoid.t-global.bcs:4444/wd/hub/ -Dproxy=http://172.18.62.68:8080 allure:serve
```
- Запуск тестов по тегам (И)
```mvn
clean test -DplatformName=iOS -DdeviceName="iPhone 6s" -DplatformVersion=12.2 -Dapp=ru.admitadteam.SimpleScoreSwift -Dselenide.remote=http://test:test-password@selenoid.t-global.bcs:4444/wd/hub/ -Dproxy=http://172.18.62.68:8080 -Dcucumber.options="--tags @api --tags @web --plugin io.qameta.allure.cucumber4jvm.AllureCucumber4Jvm --plugin com.epam.reportportal.cucumber.ScenarioReporter"
```
- Запуск тестов по тегам (ИЛИ)
```mvn
clean test -DplatformName=iOS -DdeviceName="iPhone 6s"  -Dapp=ru.admitadteam.SimpleScoreSwift -Dselenide.remote=http://test:test-password@selenoid.t-global.bcs:4444/wd/hub/ -Dproxy=http://172.18.62.68:8080 -Dcucumber.options="--tags @api,@web --plugin io.qameta.allure.cucumber4jvm.AllureCucumber4Jvm --plugin com.epam.reportportal.cucumber.ScenarioReporter"
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
-DplatformVersion=12.2 - передается версия платформы на которой будут запущены тесты
```

```mvn
-Dapp=ru.admitadteam.SimpleScoreSwift - передается название проекта который будет запущен для прогона тестов
```

```mvn
-Dselenide.remote=http://test:test-password@selenoid.t-global.bcs:4444/wd/hub/ -Dproxy=http://172.18.62.68:8080 - для запуска тестов на selenoid
```
