# Лабораторная №4, ПнаЯВУ  
### группа 334701 Вариант №13  

### `👁‍🗨^👁‍🗨 [By Anton Kozlov] 👁‍🗨^👁‍🗨`
---
 
Обработка 400 и 500 ошибок (+ 404)
Добавлены глобальные обработчики исключений:

`@ControllerAdvice` +` @ExceptionHandler` для кастомных ошибок.

`400 Bad Request`
`500 Internal Error`
Реализовано через Spring AOP (`@Aspect`):
обавлена зависимость `springdoc-openapi-starter-webmvc-ui`.
Доступна по URL: `http://localhost:8080/swagger-ui.html`.


