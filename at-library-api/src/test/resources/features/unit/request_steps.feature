# language: ru
@api
@unit
Функционал: RequestSteps

####################################################################################

  Сценарий: выполнен GET| запрос на URL "([^"]+)". Полученный ответ сохранен в переменную "([^"]+)"
    Когда выполнен GET запрос на URL "url.store.inventory". Полученный ответ сохранен в переменную "response"
    И в ответе "response" statusCode: 200

####################################################################################

  Сценарий: выполнен POST запрос на URL "([^"]+)" с headers и parameters из таблицы. Полученный ответ сохранен в переменную "([^"]+)"
    Когда выполнен POST запрос на URL "url.pet" с headers и parameters из таблицы. Полученный ответ сохранен в переменную "response"
      | HEADER | accept       | application/json |
      | HEADER | Content-Type | application/json |
      | BODY   | json         | json.post.pet    |
    Тогда в ответе "response" statusCode: 200

  Сценарий: выполнен GET запрос на URL "([^"]+)" с headers и parameters из таблицы. Полученный ответ сохранен в переменную "([^"]+)"
    Когда выполнен GET запрос на URL "url.pet.petId" с headers и parameters из таблицы. Полученный ответ сохранен в переменную "response"
      | HEADER         | accept | application/json |
      | PATH_PARAMETER | petId  | 17122019         |
    И в ответе "response" statusCode: 200

  Сценарий: выполнен PUT запрос на URL "([^"]+)" с headers и parameters из таблицы. Полученный ответ сохранен в переменную "([^"]+)"
    Когда выполнен PUT запрос на URL "url.pet" с headers и parameters из таблицы. Полученный ответ сохранен в переменную "response"
      | HEADER | accept       | application/json |
      | HEADER | Content-Type | application/json |
      | BODY   | json         | json.put.pet     |
    И в ответе "response" statusCode: 200

  Сценарий: выполнен DELETE запрос на URL "([^"]+)" с headers и parameters из таблицы. Полученный ответ сохранен в переменную "([^"]+)"
    Когда выполнен DELETE запрос на URL "url.pet.petId" с headers и parameters из таблицы. Полученный ответ сохранен в переменную "response"
      | HEADER         | accept | application/json |
      | PATH_PARAMETER | petId  | 17122019         |
    Тогда в ответе "response" statusCode: 200
