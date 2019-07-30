at-library-web
=========================

Настройка проекта
====================
Подключите зависимость
```xml
<dependency>
      <groupId>ru.bcs</groupId>
      <artifactId>at-library-web</artifactId>
      <version>18.07.2019</version>
</dependency>
```

Примеры шагов
=======================

```gherkin
# language: ru
@web
Функция: Создание демо аккаунта

  Сценарий: Проверка валидации поля Email
    Когда совершен переход на страницу "BCS demo аккаунт" по ссылке "https://broker.ru/demo"
    И в поле "ФИО" введено значение "Павлов Антон Павлович"
    И в поле "Номер телефона" введено значение "9036704271"
    И в поле "Email" введено значение "555.mail.ru"
    И выполнено нажатие на кнопку "Открыть счет"
    Тогда форма "Окно ввода SMS-кода" скрыта
```

Работа с страницами
====================
Для работы с элементами страницы ее необходимо задать как текущую.
Таким образом можно получить доступ к методам взаимодействия с элементами, описанным в CorePage.

Новую текущую страницу можно установить шагом
```Когда страница "<Имя страницы>" загрузилась```

Для страницы BCS demo аккаунт шаг может выглядеть так
```Когда страница "BCS demo аккаунт" загрузилась```

Каждая страница, с которой предполагается взаимодействие, должна быть описана в классе наследующемся от CorePage.
Для страницы и ее элементов следует задать имя на русском, через аннотацию Name, чтобы искать можно было именно по русскому описанию.
Элементы страницы ищутся по локаторам, указанным в аннотации FindBy и должны иметь тип SelenideElement или List<SelenideElement>.

Пример описания страницы:
```java
   @Name("BCS demo аккаунт")
   public class BrokerDemoPage extends CorePage {
   
       private static final String demoAccountForm = "[class='become-demo__form form js-demo-quik-form'] ";
   
       @Name("ФИО")
       @FindBy(css = demoAccountForm + "[name=\"name\"]")
       private SelenideElement inputFIO;
   
       @Name("Номер телефона")
       @FindBy(css = demoAccountForm + "[name=\"phone\"]")
       private SelenideElement inputPhone;
   
       @Name("Email")
       @FindBy(css = demoAccountForm + "[name=\"email\"]")
       private SelenideElement inputEmail;
   
       @Name("Открыть счет")
       @FindBy(css = demoAccountForm + "[class='become-demo__form-submit'] button")
       private SelenideElement buttonOpenScore;
   }
```

Инициализация страницы
=================================
Если непобходимо создавать собсвенные шаги по работе с web элементами'

Пример инициализации страницы "BCS demo аккаунт":
```java
public class BrokerDemoPageSteps {
    
    private CoreScenario coreScenario = CoreScenario.getInstance();

    @И("создание пользователя с ФИО: \"([^\"]*)\" телефон: \"([^\"]*)\" email: \"([^\"]*)\"")
    public void loginSystem(String fio, String password, String email) {
        fio = getPropertyOrStringVariableOrValue(fio);
        password = getPropertyOrStringVariableOrValue(password);
        email = getPropertyOrStringVariableOrValue(email);

        BrokerDemoPage brokerDemoPage =
                (BrokerDemoPage) coreScenario.getPage("BCS demo аккаунт");

        brokerDemoPage.getElement("ФИО").sendKeys(fio);
        brokerDemoPage.getElement("Номер телефона").sendKeys(password);
        brokerDemoPage.getElement("Email").sendKeys(email);
        
        brokerDemoPage.getElement("Открыть счет").click();
    }
}
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
        brokerDemoPage.getElement("ФИО").sendKeys(fio);
        brokerDemoPage.getElement("Номер телефона").sendKeys(password);
        brokerDemoPage.getElement("Email").sendKeys(email);
        
        brokerDemoPage.getElement("Открыть счет").click();
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
