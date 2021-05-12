# language: ru
@unit
@web
@web-action-steps
#noinspection NonAsciiCharacters
Функционал: WebActionSteps

########################################################################################################################
############################################# Нажатие (ховер) на элемент ###############################################
########################################################################################################################

  Сценарий: выполнено нажатие на (?:кнопку|ссылку|поле|чекбокс|радиокнопу|текст|элемент) "([^"]*)"
    Когда совершен переход на страницу "Википедия" по ссылке "https://ru.wikipedia.org/"
    И выполнено нажатие на элемент "Сведения о странице"
    То страница "Сведения о странице" загрузилась

  Сценарий: выполнено умное нажатие на (?:кнопку|ссылку|поле|чекбокс|радиокнопу|текст|элемент) "([^"]*)"
    Когда совершен переход на страницу "Практика автоматизации - главная" по ссылке "http://automationpractice.com/"
    И нажатие на элемент с значением "WOMEN" в списке "Категории товаров"
    И страница "Практика автоматизации - магазин" загрузилась
    И выполнено умное нажатие на кнопку "Первый товар - Быстрый просмотр"
    То страница "Товар" загрузилась

  Сценарий: выполнено нажатие c ховером на (?:кнопку|ссылку|поле|чекбокс|радиокнопу|текст|элемент) "([^"]*)"
    Когда совершен переход на страницу "Практика автоматизации - главная" по ссылке "http://automationpractice.com/"
    И нажатие на элемент с значением "WOMEN" в списке "Категории товаров"
    И страница "Практика автоматизации - магазин" загрузилась
    И выполнено нажатие c ховером на ссылку "Первый товар"
    То страница "Товар" загрузилась

  Сценарий: выполнен ховер на (?:кнопку|ссылку|поле|чекбокс|радиокнопу|текст|элемент) "([^"]*)"
    Когда совершен переход на страницу "Практика автоматизации - главная" по ссылке "http://automationpractice.com/"
    И нажатие на элемент с значением "WOMEN" в списке "Категории товаров"
    И страница "Практика автоматизации - магазин" загрузилась
    И выполнен ховер на элемент "Первый товар"
    И выполнено умное нажатие на кнопку "Первый товар - Просмотр"
    То страница "Товар" загрузилась

  Сценарий: выполнено нажатие на элемент с текстом "([^"]*)" [HARDCODING]
    Когда совершен переход на страницу "Практика автоматизации - главная" по ссылке "http://automationpractice.com/"
    И выполнено нажатие на элемент с текстом "Women"
    То страница "Практика автоматизации - магазин" загрузилась

  Сценарий: выполнено нажатие на элемент с текстом "([^"]*)" [VARIABLE]
    Когда совершен переход на страницу "Практика автоматизации - главная" по ссылке "http://automationpractice.com/"
    И установлено значение переменной "toClick" равным "Women"
    И выполнено нажатие на элемент с текстом "toClick"
    То страница "Практика автоматизации - магазин" загрузилась

  Сценарий: выполнено нажатие на элемент с текстом "([^"]*)" [PROPERTIES]
    Когда совершен переход на страницу "Практика автоматизации - главная" по ссылке "http://automationpractice.com/"
    И выполнено нажатие на элемент с текстом "to.click"
    То страница "Практика автоматизации - магазин" загрузилась

  Сценарий: выполнено нажатие на (?:кнопку|ссылку|поле|чекбокс|радиокнопу|текст|элемент) "([^"]*)" в блоке "([^"]*)"
    Когда совершен переход на страницу "Практика автоматизации - главная" по ссылке "http://automationpractice.com/"
    И выполнено нажатие на элемент "Логотип" в блоке "Блок заголовка"
    То страница "Практика автоматизации - главная" загрузилась

########################################################################################################################
############################################## Установка значения в поле ###############################################
########################################################################################################################

  Сценарий: в поле "([^"]*)" введено значение "([^"]*)" [HARDCODING]
    Когда совершен переход на страницу "Главная" по ссылке "url.google"
    И в поле "Поиск" введено значение "тест"
    То поле "Поиск" содержит текст "тест"

  Сценарий: в поле "([^"]*)" введено значение "([^"]*)" [VARIABLE]
    Когда совершен переход на страницу "Главная" по ссылке "url.google"
    И установлено значение переменной "toSearch" равным "тест"
    И в поле "Поиск" введено значение "toSearch"
    То поле "Поиск" содержит текст "тест"

  Сценарий: в поле "([^"]*)" введено значение "([^"]*)" [PROPERTIES]
    Когда совершен переход на страницу "Главная" по ссылке "url.google"
    И в поле "Поиск" введено значение "text.fio"
    То поле "Поиск" содержит текст "Иванов Иван Иванович"

  Сценарий: в поле "([^"]*)" посимвольно набирается значение "([^"]*)" [HARDCODING]
    Когда совершен переход на страницу "Главная" по ссылке "url.google"
    И в поле "Поиск" посимвольно набирается значение "тест"
    То поле "Поиск" содержит текст "тест"

  Сценарий: в поле "([^"]*)" посимвольно набирается значение "([^"]*)" [VARIABLE]
    Когда совершен переход на страницу "Главная" по ссылке "url.google"
    И установлено значение переменной "toSearch" равным "тест"
    И в поле "Поиск" посимвольно набирается значение "toSearch"
    То поле "Поиск" содержит текст "тест"

  Сценарий: в поле "([^"]*)" посимвольно набирается значение "([^"]*)" [PROPERTIES]
    Когда совершен переход на страницу "Главная" по ссылке "url.google"
    И в поле "Поиск" посимвольно набирается значение "text.fio"
    То поле "Поиск" содержит текст "Иванов Иван Иванович"

  Сценарий: в (?:поле|элемент) "([^"]*)" дописывается значение "([^"]*)" [HARDCODING]
    Когда совершен переход на страницу "Главная" по ссылке "url.google"
    И в поле "Поиск" введено значение "тест"
    И в поле "Поиск" дописывается значение " тест"
    То поле "Поиск" содержит текст "тест тест"

  Сценарий: в (?:поле|элемент) "([^"]*)" дописывается значение "([^"]*)" [VARIABLE]
    Когда совершен переход на страницу "Главная" по ссылке "url.google"
    И в поле "Поиск" введено значение "тест"
    И установлено значение переменной "toAdd" равным " тест"
    И в поле "Поиск" дописывается значение "toAdd"
    То поле "Поиск" содержит текст "тест тест"

  Сценарий: в (?:поле|элемент) "([^"]*)" дописывается значение "([^"]*)" [PROPERTIES]
    Когда совершен переход на страницу "Главная" по ссылке "url.google"
    И в поле "Поиск" введено значение "тест "
    И в поле "Поиск" дописывается значение "text.fio"
    То поле "Поиск" содержит текст "тест Иванов Иван Иванович"

  Сценарий: (?:поле|элемент) "([^"]*)" заполняется текущей датой в формате "([^"]*)"
    Когда совершен переход на страницу "Главная" по ссылке "url.google"
    И установлено значение переменной "toCheck" с текущей датой в формате "dd.MM.yyyy"
    И поле "Поиск" заполняется текущей датой в формате "dd.MM.yyyy"
    То поле "Поиск" содержит текст "toCheck"

  Сценарий: (?:поле|элемент) "([^"]*)" заполняется текущей датой в формате "([^"]*)" extra1
    Когда совершен переход на страницу "Главная" по ссылке "url.google"
    И установлено значение переменной "toCheck" с текущей датой в формате "yyyyy.MMMMM.dd GGG hh:mm aaa"
    И поле "Поиск" заполняется текущей датой в формате "yyyyy.MMMMM.dd GGG hh:mm aaa"
    То поле "Поиск" содержит текст "toCheck"

  Сценарий: (?:поле|элемент) "([^"]*)" заполняется текущей датой в формате "([^"]*)" extra2
    Когда совершен переход на страницу "Главная" по ссылке "url.google"
    И установлено значение переменной "toCheck" с текущей датой в формате "dd.MM.yyyy"
    И поле "Поиск" заполняется текущей датой в формате "wrong format"
    То поле "Поиск" содержит текст "toCheck"

  Сценарий: вставлено значение "([^"]*)" в (?:поле|элемент) "([^"]*)" с помощью горячих клавиш [HARDCODING/VARIABLE/PROPERTIES]
    Когда совершен переход на страницу "Главная" по ссылке "url.google"
    И вставлено значение "тест" в поле "Поиск" с помощью горячих клавиш
    То поле "Поиск" содержит текст "тест"
    Тогда очищено поле "Поиск"
    Когда установлено значение переменной "toInsert" равным "тест"
    И вставлено значение "toInsert" в поле "Поиск" с помощью горячих клавиш
    То поле "Поиск" содержит текст "тест"
    Тогда очищено поле "Поиск"
    Когда вставлено значение "text.fio" в поле "Поиск" с помощью горячих клавиш
    То поле "Поиск" содержит текст "Иванов Иван Иванович"

  Сценарий: очищено поле "([^"]*)"
    Когда совершен переход на страницу "Главная" по ссылке "url.google"
    И вставлено значение "тест" в поле "Поиск" с помощью горячих клавиш
    То поле "Поиск" содержит текст "тест"
    Тогда очищено поле "Поиск"
    То поле "Поиск" пусто

  Сценарий: в поле "([^"]*)" введено "([^"]*)" случайных символов на (кириллице|латинице)
    Когда совершен переход на страницу "Главная" по ссылке "url.google"
    И в поле "Поиск" введено "7" случайных символов на кириллице
    То в поле "Поиск" содержится 7 символов

  Сценарий: в поле "([^"]*)" введено "([^"]*)" случайных символов на (кириллице|латинице) extra
    Когда совершен переход на страницу "Главная" по ссылке "url.google"
    И в поле "Поиск" введено "8" случайных символов на латинице
    То в поле "Поиск" содержится 8 символов

  Сценарий: в поле "([^"]*)" введено "([^"]*)" случайных символов на (кириллице|латинице) и сохранено в переменную "([^"]*)"
    Когда совершен переход на страницу "Главная" по ссылке "url.google"
    И в поле "Поиск" введено "9" случайных символов на кириллице и сохранено в переменную "cyrCheck"
    То поле "Поиск" содержит текст "cyrCheck"

  Сценарий: в поле "([^"]*)" введено "([^"]*)" случайных символов на (кириллице|латинице) и сохранено в переменную "([^"]*)" extra
    Когда совершен переход на страницу "Главная" по ссылке "url.google"
    И в поле "Поиск" введено "9" случайных символов на кириллице и сохранено в переменную "latCheck"
    То поле "Поиск" содержит текст "latCheck"

  Сценарий: в поле "([^"]*)" введено случайное число из "([^"]*)" (?:цифр|цифры)
    Когда совершен переход на страницу "Главная" по ссылке "url.google"
    И в поле "Поиск" введено случайное число из "11" цифр
    То в поле "Поиск" содержится 11 символов

  Сценарий: в поле "([^"]*)" введено случайное число из (\d+) (?:цифр|цифры) и сохранено в переменную "([^"]*)"
    Когда совершен переход на страницу "Главная" по ссылке "url.google"
    И в поле "Поиск" введено случайное число из 12 цифр и сохранено в переменную "numCheck"
    То поле "Поиск" содержит текст "numCheck"

########################################################################################################################
############################################# Скролл страницы до элемента ##############################################
########################################################################################################################

  Сценарий: страница прокручена до элемента "([^"]*)"
    Когда совершен переход на страницу "Википедия" по ссылке "url.wikipedia"
    И страница прокручена до элемента "Платформа сайта"
    То элемент "Платформа сайта" расположен в видимой части страницы

  Сценарий: страница прокручена до появления элемента "([^"]*)"
    Когда совершен переход на страницу "Википедия" по ссылке "url.wikipedia"
    И страница прокручена до появления элемента "Платформа сайта"
    То элемент "Платформа сайта" расположен в видимой части страницы

  Сценарий: страница прокручена до появления элемента с текстом "([^"]*)" [HARDCODING]
    Когда совершен переход на страницу "Википедия" по ссылке "url.wikipedia"
    И страница прокручена до появления элемента с текстом "Заявление о куки"
    То элемент содержащий текст "Заявление о куки" расположен в видимой части страницы

  Сценарий: страница прокручена до появления элемента с текстом "([^"]*)" [VARIABLE]
    Когда совершен переход на страницу "Википедия" по ссылке "url.wikipedia"
    И установлено значение переменной "toScrollTo" равным "Заявление о куки"
    И страница прокручена до появления элемента с текстом "toScrollTo"
    То элемент содержащий текст "toScrollTo" расположен в видимой части страницы

  Сценарий: страница прокручена до появления элемента с текстом "([^"]*)" [PROPERTIES]
    Когда совершен переход на страницу "Википедия" по ссылке "url.wikipedia"
    И страница прокручена до появления элемента с текстом "to.scroll"
    То элемент содержащий текст "to.scroll" расположен в видимой части страницы

  Сценарий: совершен переход на страницу "([^"]*)" в новой вкладке по ссылке "([^"]*)" [PROPERTIES]
    Когда совершен переход по ссылке "url.wikipedia"
    И совершен переход на страницу "Главная" в новой вкладке по ссылке "url.google"
    То поле "Поиск" отображается на странице

#####################################################################################
#
#  Сценарий: выполнен переход на страницу "([^"]*)" после нажатия на (?:ссылку|кнопку) "([^"]*)"
#    Когда совершен переход на страницу "BCS Брокер" по ссылке "url.broker"
#    И выполнен переход на страницу "Главная" после нажатия на кнопку "Демо-счет"
#    То поле "ФИО" отображается на странице
#
#####################################################################################
#
#  Сценарий: (?:страница|блок|форма|вкладка) "([^"]*)" (?:загрузилась|загрузился)
#    Когда совершен переход по ссылке "url.broker"
#    То страница "BCS Брокер" загрузилась
#
#####################################################################################
#
#  Сценарий: (?:страница|блок|форма|вкладка) "([^"]*)" не (?:загрузилась|загрузился)
#    Когда совершен переход по ссылке "url.broker"
#    То страница "Главная" не загрузилась
#
#####################################################################################
#
#  Сценарий: выполнено нажатие на (?:кнопку|ссылку|поле|чекбокс|радиокнопу|текст|элемент) "([^"]*)"
#    Когда совершен переход на страницу "BCS Брокер" по ссылке "url.broker"
#    И выполнено нажатие на кнопку "Демо-счет"
#    То страница "Главная" загрузилась
#    То поле "ФИО" отображается на странице
#
#####################################################################################
#
#  Сценарий: выполнено умное нажатие на (?:кнопку|ссылку|поле|чекбокс|радиокнопу|текст|элемент) "([^"]*)"
#    Когда совершен переход на страницу "BCS Брокер" по ссылке "url.broker"
#    И выполнено умное нажатие на кнопку "Демо-счет"
#    То страница "Главная" загрузилась
#    То поле "ФИО" отображается на странице
#
#####################################################################################
#
#  Сценарий: выполнено нажатие на клавиатуре "([^"]*)"
#    Когда совершен переход на страницу "Главная" по ссылке "url.google"
#    И в поле "ФИО" набирается значение "Иванова"
#    И выполнено нажатие на клавиатуре "а"
#    И выполнено нажатие на клавиатуре "BACK_SPACE"
#    И выполнено нажатие на клавиатуре "BACK_SPACE"
#    Тогда содержимое текста "ФИО" равно "Иванов"
#
#####################################################################################
#
#  Сценарий: выполнено нажатие на сочетание клавиш из таблицы
#    Когда совершен переход на страницу "Главная" по ссылке "url.google"
#    И в поле "ФИО" набирается значение "Иванов"
#    И выполнено нажатие на сочетание клавиш из таблицы
#      | SPACE |
#      | И     |
#      | в     |
#      | а     |
#      | н     |
#    Тогда содержимое текста "ФИО" равно "Иванов Иван"
#
#####################################################################################
#
#  Сценарий: в поле "([^"]*)" введено значение "(.*)" [однострочно] [HARDCODING]
#    Когда совершен переход на страницу "Главная" по ссылке "url.google"
#    И в поле "ФИО" введено значение "Иванов Иван Иванович"
#    И значение поля "ФИО" равно "text.fio"
#
#  Сценарий: в поле "([^"]*)" введено значение "(.*)" [однострочно] [VARIABLE]
#    Когда установлено значение переменной "fio" равным "Иванов Иван Иванович"
#    Когда совершен переход на страницу "Главная" по ссылке "url.google"
#    И в поле "ФИО" введено значение "fio"
#    И значение поля "ФИО" равно "text.fio"
#
#  Сценарий: в поле "([^"]*)" введено значение "(.*)" [однострочно] [PROPERTIES]
#    Когда совершен переход на страницу "Главная" по ссылке "url.google"
#    И в поле "ФИО" введено значение "text.fio"
#    И значение поля "ФИО" равно "text.fio"
#
#####################################################################################
#
#  Сценарий: в поле "([^"]*)" введено значение "(.*)" [многострочно]
#    Когда совершен переход на страницу "Главная" по ссылке "url.google"
#    И в поле "Номер телефона" введено значение
#    """
#    9123456789
#    """
#    И значение поля "Номер телефона" равно "+7 912 345-67-89"
#
#####################################################################################
#
#  Сценарий: в поле "([^"]*)" набирается значение "(.*)" [однострочно] [HARDCODING]
#    Когда совершен переход на страницу "Главная" по ссылке "url.google"
#    И в поле "" посимвольно набирается значение
#    И в поле "ФИО" набирается значение "Иванов Иван Иванович"
#    И значение поля "ФИО" равно "text.fio"
#
#  Сценарий: в поле "([^"]*)" набирается значение "(.*)" [однострочно] [VARIABLE]
#    Когда установлено значение переменной "fio" равным "Иванов Иван Иванович"
#    Когда совершен переход на страницу "Главная" по ссылке "url.google"
#    И в поле "ФИО" набирается значение "fio"
#    И значение поля "ФИО" равно "text.fio"
#
#  Сценарий: в поле "([^"]*)" набирается значение "(.*)" [однострочно] [PROPERTIES]
#    Когда совершен переход на страницу "Главная" по ссылке "url.google"
#    И в поле "ФИО" набирается значение "text.fio"
#    И значение поля "ФИО" равно "text.fio"
#
#####################################################################################
#
#  Сценарий: в поле "([^"]*)" набирается значение "(.*)" [многострочно]
#    Когда совершен переход на страницу "Главная" по ссылке "url.google"
#    И в поле "Номер телефона" набирается значение
#    """
#    9123456789
#    """
#    И значение поля "Номер телефона" равно "+7 912 345-67-89"
#
#####################################################################################
#
#  Сценарий: в поле "([^"]*)" посимвольно набирается значение "([^"]*)" [однострочно] [HARDCODING]
#    Когда совершен переход на страницу "Главная" по ссылке "url.google"
#    И в поле "ФИО" посимвольно набирается значение "Иванов Иван Иванович"
#    И значение поля "ФИО" равно "text.fio"
#
#  Сценарий: в поле "([^"]*)" посимвольно набирается значение "([^"]*)" [однострочно] [VARIABLE]
#    Когда установлено значение переменной "fio" равным "Иванов Иван Иванович"
#    Когда совершен переход на страницу "Главная" по ссылке "url.google"
#    И в поле "ФИО" посимвольно набирается значение "fio"
#    И значение поля "ФИО" равно "text.fio"
#
#  Сценарий: в поле "([^"]*)" посимвольно набирается значение "([^"]*)" [однострочно] [PROPERTIES]
#    Когда совершен переход на страницу "Главная" по ссылке "url.google"
#    И в поле "ФИО" посимвольно набирается значение "text.fio"
#    И значение поля "ФИО" равно "text.fio"
#
#####################################################################################
#
#  Сценарий: в поле "([^"]*)" посимвольно набирается значение "([^"]*)" [многострочно]
#    Когда совершен переход на страницу "Главная" по ссылке "url.google"
#    И в поле "Номер телефона" посимвольно набирается значение
#    """
#    9123456789
#    """
#    И значение поля "Номер телефона" равно "+7 912 345-67-89"
#
#####################################################################################
#
#  Сценарий: очищено поле "([^"]*)"$
#    Когда совершен переход на страницу "Главная" по ссылке "url.google"
#    И в поле "ФИО" посимвольно набирается значение "Иванов Иван Иванович"
#    И очищено поле "ФИО"
#    И значение поля "ФИО" равно ""
#
#####################################################################################
#
#  Сценарий: выполнен ховер на (?:кнопку|ссылку|поле|чекбокс|радиокнопу|текст|элемент) "([^"]*)"
#    И написание автотеста в работе
#    Когда совершен переход на страницу "Главная" по ссылке "url.google"
#    И выполнен ховер на текст "ФИО"
#
#####################################################################################
#
#  Сценарий: в (?:поле|элемент) "([^"]*)" дописывается значение "(.*)" [однострочно] [HARDCODING]
#    Когда совершен переход на страницу "Главная" по ссылке "url.google"
#    И в поле "ФИО" введено значение "Иванов "
#    И в поле "ФИО" дописывается значение "Иван Иванович"
#    И значение поля "ФИО" равно "text.fio"
#
#  Сценарий: в (?:поле|элемент) "([^"]*)" дописывается значение "(.*)" [однострочно] [VARIABLE]
#    Когда установлено значение переменной "fio" равным "Иван Иванович"
#    Когда совершен переход на страницу "Главная" по ссылке "url.google"
#    И в поле "ФИО" введено значение "Иванов "
#    И в поле "ФИО" дописывается значение "fio"
#    И значение поля "ФИО" равно "text.fio"
#
#  Сценарий: в (?:поле|элемент) "([^"]*)" дописывается значение "(.*)" [однострочно] [PROPERTIES]
#    Когда совершен переход на страницу "Главная" по ссылке "url.google"
#    И в поле "ФИО" введено значение "Иванов "
#    И в поле "ФИО" дописывается значение "text.io"
#    И значение поля "ФИО" равно "text.fio"
#
#####################################################################################
#
#  Сценарий: выполнено нажатие на элемент с текстом "(.*)"
#    Когда совершен переход на страницу "BCS Брокер" по ссылке "url.broker"
#    И выполнено нажатие на элемент с текстом "Демо-счет"
#    То страница "Главная" загрузилась
#    То поле "ФИО" отображается на странице
#
#####################################################################################
#
#  Сценарий: (?:поле|элемент) "([^"]*)" заполняется текущей датой в формате "([^"]*)"
#    Когда совершен переход на страницу "Главная" по ссылке "url.google"
#    И установлено значение переменной "текущая_дата" с текущей датой в формате "dd.MM.yyyy"
#    И поле "Поиск" заполняется текущей датой в формате "dd.MM.yyyy"
#    И значение поля "ФИО" равно "текущая_дата"
#
#####################################################################################
#
#  Сценарий: вставлено значение "([^"]*)" в (?:поле|элемент) "([^"]*)" с помощью горячих клавиш [HARDCODING]
#    Когда совершен переход на страницу "Главная" по ссылке "url.google"
#    И вставлено значение "Иванов Иван Иванович" в поле "ФИО" с помощью горячих клавиш
#    И значение поля "ФИО" равно "text.fio"
#
#  Сценарий: вставлено значение "([^"]*)" в (?:поле|элемент) "([^"]*)" с помощью горячих клавиш [VARIABLE]
#    Когда установлено значение переменной "fio" равным "Иванов Иван Иванович"
#    Когда совершен переход на страницу "Главная" по ссылке "url.google"
#    И вставлено значение "fio" в поле "ФИО" с помощью горячих клавиш
#    И значение поля "ФИО" равно "text.fio"
#
#  Сценарий: вставлено значение "([^"]*)" в (?:поле|элемент) "([^"]*)" с помощью горячих клавиш [PROPERTIES]
#    Когда совершен переход на страницу "Главная" по ссылке "url.google"
#    И вставлено значение "text.fio" в поле "ФИО" с помощью горячих клавиш
#    И значение поля "ФИО" равно "text.fio"
#
#####################################################################################
#
#  Сценарий: файл по пути "(.*)" выгрузился в поле "(.*)"
#    И написание автотеста в работе
#    И файл по пути "аааа" выгрузился в поле "ааааа"
#
#####################################################################################
#
#  Сценарий: страница прокручена до элемента "([^"]*)"
#    Когда совершен переход на страницу "Главная" по ссылке "url.google"
#    И страница прокручена до элемента "ФИО"
#
#####################################################################################
#
#  Сценарий: в поле "([^"]*)" введено (\d+) случайных символов на (кириллице|латинице) [кириллице]
#    Когда совершен переход на страницу "Главная" по ссылке "url.google"
#    И в поле "ФИО" введено 10 случайных символов на кириллице
#    И значение поля "ФИО" сохранено в переменную "10_символов_на_кириллице"
#    И длина строки переменной "10_символов_на_кириллице" равна 10
#
#  Сценарий: в поле "([^"]*)" введено (\d+) случайных символов на (кириллице|латинице) [латиница]
#    Когда совершен переход на страницу "Главная" по ссылке "url.google"
#    И в поле "ФИО" введено 10 случайных символов на латинице
#    И значение поля "ФИО" сохранено в переменную "10_символов_на_латинице"
#    И длина строки переменной "10_символов_на_латинице" равна 10
#
#####################################################################################
#
#  Сценарий: в поле "([^"]*)" введено (\d+) случайных символов на (кириллице|латинице) и сохранено в переменную "([^"]*)"[ кириллице]
#    Когда совершен переход на страницу "Главная" по ссылке "url.google"
#    И в поле "ФИО" введено 10 случайных символов на кириллице и сохранено в переменную "10_символов_на_кириллице"
#    И длина строки переменной "10_символов_на_кириллице" равна 10
#
#  Сценарий: в поле "([^"]*)" введено (\d+) случайных символов на (кириллице|латинице) и сохранено в переменную "([^"]*)"[ латиница]
#    Когда совершен переход на страницу "Главная" по ссылке "url.google"
#    И в поле "ФИО" введено 10 случайных символов на латинице и сохранено в переменную "10_символов_на_латинице"
#    И длина строки переменной "10_символов_на_латинице" равна 10
#
#####################################################################################
#
#  Сценарий: в поле "([^"]*)" введено случайное число из (\d+) (?:цифр|цифры)
#    Когда совершен переход на страницу "Главная" по ссылке "url.google"
#    И в поле "ФИО" введено случайное число из 8 цифр
#    И значение поля "ФИО" сохранено в переменную "число_из_8_цифр"
#    Тогда длина строки переменной "число_из_8_цифр" равна 8
#
#####################################################################################
#
#  Сценарий: в поле "([^"]*)" введено случайное число из (\d+) (?:цифр|цифры) и сохранено в переменную "([^"]*)"
#    Когда совершен переход на страницу "Главная" по ссылке "url.google"
#    И в поле "ФИО" введено случайное число из 8 цифр и сохранено в переменную "число_из_8_цифр"
#    Тогда длина строки переменной "число_из_8_цифр" равна 8
#
#####################################################################################
#
#  Сценарий: выполнен js-скрипт "([^"]*)"
#    Когда написание автотеста в работе
#    Когда совершен переход на страницу "Главная" по ссылке "url.google"
#    Тогда выполнен js-скрипт "в_работе"
#
#####################################################################################
#
#  Сценарий: страница прокручена до появления элемента "([^"]*)"
#    Когда совершен переход на страницу "Главная" по ссылке "url.google"
#    Тогда страница прокручена до появления элемента "ФИО"
#
#####################################################################################
#
#  Сценарий: страница прокручена до появления элемента с текстом "([^"]*)"
#    Когда совершен переход на страницу "BCS Брокер" по ссылке "url.broker"
#    Тогда страница прокручена до появления элемента с текстом "Демо-счет"
#
#####################################################################################
#
#  Сценарий: выполнено нажатие на кнопку "([^"]*)" и загружен файл "([^"]*)"
#    Когда написание автотеста в работе
#    Когда совершен переход на страницу "Главная" по ссылке "url.google"
#    Тогда выполнено нажатие на кнопку "ААА" и загружен файл "BBB"
#
#####################################################################################
#
#  Сценарий: выполнено нажатие на (?:кнопку|ссылку|поле|чекбокс|радиокнопу|текст|элемент) "([^"]*)" в блоке "([^"]*)"
#    Когда написание автотеста в работе
#    Когда совершен переход на страницу "BCS Брокер" по ссылке "url.broker"
#    Тогда выполнено нажатие на кнопку "Демо-счет" в блоке "Навигация"
#    То страница "Главная" загрузилась
#    То поле "ФИО" отображается на странице
#
#####################################################################################
#
#  Сценарий: пользователь "([^"]*)" ввел логин и пароль
#    Когда написание автотеста в работе
#    Когда совершен переход на страницу "Главная" по ссылке "url.google"
#    Тогда пользователь "Клиент" ввел логин и пароль
#
#####################################################################################