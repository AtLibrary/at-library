# language: ru
@mobile
Функция: Тестирование мобильного приложения

  Предыстория: загрузка приложения
    Когда экран "Главный" загрузился

  Сценарий: Проверка общей суммы всех девайсов 1
    И значение текста "description" равно "Total money:"
    И выполнено нажатие на кнопку "iphone"
    И выполнено нажатие на кнопку "mouse"
    И выполнено нажатие на кнопку "ps4"
    И выполнено нажатие на кнопку "photo"
    И выполнено нажатие на кнопку "keyboard"
    И выполнено нажатие на кнопку "notebook"
    Тогда значение поля "money" равно "157300"

  Сценарий: Проверка общей суммы всех девайсов 2
    То выбор всех товаров и проверка обшей суммы: "157300"

  Сценарий: Проверка перехода в корзину
    И выполнено нажатие на кнопку "iphone"
    И выполнено нажатие на кнопку "В корзину"
    Когда экран "Корзина" загрузился
    И значение поля "money" равно "60000"

  Сценарий: Проверка покупки
    И выполнено нажатие на кнопку "mouse"
    И выполнено нажатие на кнопку "Купить"
    Когда экран "Покупки" загрузился
    И значение поля "money" равно "300"
    И выполнено нажатие на кнопку "Назад"
    Когда экран "Главный" загрузился
    И значение поля "money" равно "0"

  Сценарий: Проверка покупки в корзине
    И выполнено нажатие на кнопку "mouse"
    И выполнено нажатие на кнопку "В корзину"
    Тогда экран "Корзина" загрузился
    Тогда выполнено нажатие на кнопку "Купить"
    И экран "Покупки" загрузился
    То значение поля "money" равно "300"
    Тогда выполнено нажатие на кнопку "Назад"
    И экран "Главный" загрузился
    Тогда значение поля "money" равно "0"

  Сценарий: Проверка описания страниц в корзине
    И значение текста "description" равно "Total money:"
    И выполнено нажатие на кнопку "iphone"
    И выполнено нажатие на кнопку "В корзину"
    Тогда экран "Корзина" загрузился
    И значение текста "description" равно "At this step you can buy the goods or go back to the store. Total money:"
    Тогда выполнено нажатие на кнопку "Купить"
    И экран "Покупки" загрузился
    И значение текста "description" равно "Thank you for your purchase. By clicking on the button you can return to the store. Total money:"
