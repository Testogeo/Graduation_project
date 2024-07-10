# Дипломный проект 
### Дипломный проект представляет собой автоматизацию тестирования комплексного сервиса, взаимодействующего с СУБД и API Банка.
#### Приложение представляет собой веб-сервис, который предоставляет возможность купить тур по определённой цене с помощью двух способов:
- Обычная оплата по дебетовой карте 
- Уникальная технология: выдача кредита по данным банковской карты 

#### В процессе работы над данным проектом были созданы следующие документы:
1. [План автоматизации](https://github.com/Testogeo/Graduation_project/blob/main/Docs/Plan.md)
2. [Отчет о проведенном тестировании](https://github.com/Testogeo/Graduation_project/blob/main/Docs/Report.md)
3. [Отчет о проведённой автоматизации тестирования](https://github.com/Testogeo/Graduation_project/blob/main/Docs/Summary.md)

#### Необходимое окружение:
- установленный Docker;
- убедитесь, что порты 8080, 9999 и 5432 или 3306 (в зависимости от выбранной базы данных) свободны;

#### Инструкции по установке
1. Клонировать репозиторий на локальный ПК `git clone https://github.com/Testogeo/Graduation_project.git`;

2. Запустите контейнер, в котором разворачивается база данных (далее БД) `docker-compose up -d --force-recreate`

3. Убедитесь в том, что БД готова к работе (логи смотреть через `docker-compose logs -f <applicationName>` (mysql или postgres)
4. Запустить SUT во вкладке Terminal Intellij IDEA командой:
`java -jar artifacts/aqa-shop.jar`
5. Для запуска авто-тестов в Terminal Intellij IDEA открыть новую сессию и ввести команду:
`./gradlew clean test allureReport -Dheadless=true`
где:
`allureReport` - подготовка данных для отчета Allure;
`-Dheadless=true` - запускает авто-тесты в headless-режиме (без открытия браузера).
6. Для просмотра отчета Allure в терминале ввести команду:
`./gradlew allureServe`



