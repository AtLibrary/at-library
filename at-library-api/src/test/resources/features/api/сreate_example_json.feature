# language: ru
@all
@api
Функционал: Создание портфеля

  Сценарий: Получение token
    И заполняю json-шаблон "/src/test/resources/restBodies/example.json" данными из таблицы и сохраняю в переменную "готовый-json"
      | example glossary | test_test |

    И в json "готовый-json" значения равны значениям из таблицы
      | glossary.title | test_test |

