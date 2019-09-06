## language: ru
#@all
#@api
#Функционал: Создание портфеля
#
#  Сценарий: Получение token
#    Когда выполнен GET запрос на URL "url.token" с headers и parameters из таблицы. Полученный ответ сохранен в переменную "token_response"
#      | HEADER         | Content-Type  | application/x-www-form-urlencoded |
#      | FORM_PARAMETER | grant_type    | grant_type                        |
#      | FORM_PARAMETER | client_id     | client_id                         |
#      | FORM_PARAMETER | username      | username                          |
#      | FORM_PARAMETER | password      | password                          |
#      | FORM_PARAMETER | client_secret | client_secret                     |
#
#
#    И в ответе "token_response" statusCode: 405
#    И в ответе "token_response" содержатся header со значениями из таблицы
#      | Server         | nginx |
#      | Content-Length | 0     |
