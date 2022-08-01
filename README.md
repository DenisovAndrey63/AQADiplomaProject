
# AQADiplomaProject
[![Build status](https://ci.appveyor.com/api/projects/status/lj5rmw5slet0jy2q?svg=true)](https://ci.appveyor.com/project/DenDro163/aqadiplomaproject)
___
## Документация.

[Задание](https://github.com/netology-code/qa-diploma)

[План тестирования](https://github.com/DenDro163/AQADiplomaProject/blob/master/docs/Plan.md)
___
## Итоги тестирования.
[Отчет о проведенном тестировании](https://github.com/DenDro163/AQADiplomaProject/blob/master/docs/Report.md)

[Отчет о проведенной автоматизации](https://github.com/DenDro163/AQADiplomaProject/blob/master/docs/Summery.md)
___
## Процедура запуска авто-тестов.

### Перед запуском тестов необходимо установить окружение:

* [AdoptOpenJDK 11.0.11+9](https://adoptopenjdk.net/index.html)
* [Docker Desktop](https://www.docker.com/products/docker-desktop)

#### Работа выполнена на ПК с установленным ПО:
* Windows 10 Pro x64
* IntelliJ IDEA 2022.1.1 (Ultimate Edition).
* OpenJDK 11
* Google Chrome 102.0.5005.115 x64

### Запуск:

* Скачать код проекта с [репозитория](https://github.com/DenDro163/AQADiplomaProject.git)
* Запустить Docker Desktop.
* Открыть терминал в папке с проектом (Использовался Git Bash). 
* Выполнить: 
  `docker-compose up -d --force-recreate`
* Запустить SUT aqa-shop.jar командой:
  `java -jar artifacts\aqa-shop.jar &`
* Запустить авто тесты командой:  
  `./gradlew clean test --info`
* Создать отчёты Allure и открыть в браузере:
  `./gradlew allureReport allureServe`

### По умолчанию подключается MySQL.
### Для работы с PostgreSQL:
* перезапустить SUT aqa-shop.jar командой: 
  `java -jar artifacts/aqa-shop.jar --spring.datasource.url=jdbc:postgresql://localhost:5432/app &`
* Запустить авто тесты командой:
  `./gradlew clean test -DdataBase.url=jdbc:postgresql://localhost:5432/app -Dusername=app -Dpassword=pass --info`
