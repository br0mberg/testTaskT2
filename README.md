# Получение Server Time and UTC и какой Web Api лучше?
Привет! Меня зовут Бромбин Андрей. 
Контакт для связи: телеграмм: https://t.me/brmbn_ndrw

Задача:
Написать приложение, которое по REST API (GET) предоставляет текущее время сервера с точностью до секунд, с указанием таймзоны.
Выбор URL конечной точки и формата ответа - на усмотрение кандидата.

Выбор реализации web API - обосновать.
Корректность подтвердить с помощью тестов (JUnit5).

## Реализация
Для начала что касается формулировки задачи: "необходимо вернуть время сервера", то есть, в моём понимании, 
я не использую ntp-сервера времени для глобальной точности, ведь задачи такой не стоит.
### Описание реализации
TimeController: Класс-контроллер, который обрабатывает HTTP GET запросы по пути "/server/time" и возвращает текущее время сервера
с точностью до секунды и указанием таймзоны.
ZonedDateTime: Используется для получения текущего времени с учетом таймзоны.
DateTimeFormatter: Используется для форматирования времени в строку с определенным форматом.

/server - URL для всех запросов на этот контроллер;
/time - URL для получения времени сервера;
## WebFlux или Spring MVC - это фреймворки, один из которых реализует парадигму реактивного программирования, а второй паттерна MVC.
Написать @RestController в обоих случаях несложно, поэтому основной вопрос стоит в том, что именно подходит в рассматриваемом случае.
Итак, основное отличие двух подходов - в блокирующих и неблокирующих вызовах. То есть в Spring MVC поток, который получил задачу 
выдать время сервера блокируется на всё время выполнения запроса. В случае же WebFlux, поток делегирует задачу отправки результата, 
не дожидаясь полного выполнения, тем самым увеличивая пропускную способность REST API сервиса.

Если представить себе высоконагруженное приложение, например, выполняющую задачу поиска в базе данных телефонных номеров по ФИО,
то такой подход Flux'овый был бы эффективнее при большом количестве одновременных пользователей. Поскольку мы могли бы сразу отдать 
действующие номера (если это быстро), и не блокируя поток, делегировать поиск предыдущих номеров пользователя и уже доотправить 
результат после нахождения.

## Итак, как экспериментировал я:
Запустил на Tomcat MVC модель, а на Netty WebFlux модель: В Jmeter провёл "нагрузочное тестирование" в 1000 одновременных 
запросов пользователей в 2 круга. Получил результаты до нагрузки такие: ![Image Alt](https://github.com/br0mberg/testTaskT2/blob/develop/src/main/resources/beforeLoadVVmMVC.png)
![Image Alt](https://github.com/br0mberg/testTaskT2/blob/develop/src/main/resources/beforeLoadVVmFlux.png)
Как можем видеть, 1 поток reactor-http-nio (Flux) и 10 потоков http-nio-8080-exec (MVC) до нагрузки.
После запуска тестирования получили:
![Image Alt](https://github.com/br0mberg/testTaskT2/blob/develop/src/main/resources/afterLoadVVmMVC.png)
![Image Alt](https://github.com/br0mberg/testTaskT2/blob/develop/src/main/resources/afterLoadVVmFlux.png)
порядка 200 потоков в случае MVC, и 8 потоков в случае Flux.

В Jmeter получил такие результаты:
![Image Alt](https://github.com/br0mberg/testTaskT2/blob/develop/src/main/resources/jMeter.png)
## Из всего проделаного для данной задачи можно сделать выводы:
### Меньшее число потоков - обрабатывает большее количество задач в WebFlux реализации.
### Конкретно для такого простого RESTAPI сервиса, я считаю подходит больше MVC, поскольку нет высокой нагрузки на блокирующие потоки и они отрабатывают быстро. 
#### В теории, если представить что мы будем что-то масштабировать, то будем исходить из знания: 
### WebFlux подходит в случаях: большого количества одновременных нагруженных запросов, система способна к соблюдению подхода Reactive Manifesto ( нет блокирующих узлов и соединений), устраивает использование реактивных драйверов на базы данных.
### MVC в случае если есть большое количество блокирующих вызовов и малое количество одновременных запросов.

## В ходе работы я познакомился с новым для меня понятием WebFlux, узнал в чём смысл подхода, скрывающимся за данным понятием,
и узнал в каких случаях и как можно его использовать. Также впервые познакомился с Jmeter. Искренне жду правки и рекомендации по работе.
