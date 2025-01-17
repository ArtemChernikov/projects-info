
[![GitHub Action](https://github.com/ArtemChernikov/projects-info/actions/workflows/maven.yml/badge.svg)](https://github.com/ArtemChernikov/projects-info/actions/workflows/maven.yml)

# <span style="font-size: 2.5em; font-weight: bold; color: #4CAF50;">Projects Info</span>

## <span style="font-size: 1.8em; color: #2196F3;">О проекте</span>

<p style="font-size: 1.2em; line-height: 1.6; font-family: Arial, sans-serif;">
<strong>Projects Info</strong> — это современное веб-приложение для управления проектами. 
Приложение предоставляет удобный и интуитивно понятный пользовательский интерфейс, для сотрудников компании по разработке ПО.
</p>

<p style="font-size: 1.2em; line-height: 1.6; font-family: Arial, sans-serif; color: #555;">
Проект создан с использованием современных технологий, включая <strong>Java</strong>, <strong>Spring Boot</strong>, и <strong>Vaadin</strong>, что обеспечивает высокую производительность и простоту использования.
</p>

## Основные возможности приложения

<div style="font-family: Arial, sans-serif;">

### Поддержка ролей с различными уровнями доступа

#### <span style="color: #ff5733; font-weight: bold;">Admin</span>
<ul>
    <li>Просмотр всех сотрудников</li>
    <li>Просмотр всех сотрудников (с возможностью редактирования)</li>
    <li>Добавление сотрудника</li>
    <li>Просмотр всех проектов (с возможностью редактирования)</li>
    <li>Добавление проекта</li>
    <li>Просмотр всех задач (с возможностью редактирования)</li>
    <li>Создание резервной копии БД</li>
    <li>Восстановление БД из резервной копии</li>
    <li>Генерация отчета по всем задачам</li>
</ul>

#### <span style="color: #5d8aa8; font-weight: bold;">Project Manager</span>
<ul>
    <li>Просмотр всех сотрудников</li>
    <li>Просмотр сотрудников (на текущих проектах сотрудника)</li>
    <li>Просмотр проектов (на текущих проектах сотрудника, с возможностью редактирования)</li>
    <li>Добавление проекта</li>
    <li>Просмотр задач (на текущих проектах сотрудника, с возможностью редактирования)</li>
    <li>Добавить задачу (к текущим проектам сотрудника)</li>
    <li>Просмотр багов (на текущих проектах сотрудника)</li>
    <li>Генерация отчета по всем задачам на текущих проектах сотрудника</li>
    <li>Генерация отчета по активным задачам на текущих проектах сотрудника</li>
    <li>Генерация отчета по завершенным задачам на текущих проектах сотрудника</li>
    <li>Генерация отчета по всем багам на текущих проектах сотрудника</li>
</ul>

#### <span style="color: #6a5acd; font-weight: bold;">Developer (Fullstack, Backend, Frontend)</span>
<ul>
    <li>Просмотр проектов (на которых задействован сотрудник)</li>
    <li>Просмотр задач (своих задач, с возможностью изменения статуса задачи)</li>
    <li>Просмотр багов (на текущих проектах сотрудника, с возможностью изменения статуса бага)</li>
</ul>

#### <span style="color: #2e8b57; font-weight: bold;">Tester (QA, AQA)</span>
<ul>
    <li>Просмотр проектов (на которых задействован сотрудник)</li>
    <li>Просмотр задач (своих задач, с возможностью изменения статуса задачи)</li>
    <li>Просмотр багов (на текущих проектах сотрудника, с возможностью редактирования)</li>
    <li>Добавление бага</li>
</ul>

#### <span style="color: #ff4500; font-weight: bold;">User (DevOps, Data Scientist, Data Analyst)</span>
<ul>
    <li>Просмотр проектов (на которых задействован сотрудник)</li>
    <li>Просмотр задач (своих задач, с возможностью изменения статуса задачи)</li>
</ul>

</div>


## <span style="font-size: 1.8em; color: #FF5722;">Инструменты</span>

<div style="font-family: Arial, sans-serif; font-size: 1.2em; line-height: 1.8;">

<ul style="list-style-type: none; padding: 0;">
  <li>🚀 <strong>Java 17+</strong>: основной язык разработки.</li>
  <li>🌱 <strong>Spring Boot 3.x</strong>: создание веб-приложений и REST API.</li>
  <li>🎨 <strong>Vaadin 24.x</strong>: разработка пользовательского интерфейса.</li>
  <li>🔒 <strong>Spring Security</strong>: управление аутентификацией и авторизацией.</li>
  <li>💾 <strong>Hibernate/JPA</strong>: работа с базами данных через ORM.</li>
  <li>🛢️ <strong>PostgreSQL</strong>: реляционная база данных.</li>
  <li>📋 <strong>Liquibase</strong>: управление миграциями базы данных.</li>
  <li>🔧 <strong>Mapstruct</strong>: автоматический маппинг объектов.</li>
  <li>🛠️ <strong>Maven</strong>: система сборки и управления зависимостями.</li>
  <li>✂️ <strong>Lombok</strong>: упрощение написания кода.</li>
  <li>🧪 <strong>JUnit 5</strong>: написание и выполнение модульных тестов.</li>
</ul>

</div>

## <span style="font-size: 1.8em; color: #4CAF50;">Сборка и запуск</span>

<div style="font-family: Arial, sans-serif; font-size: 1.2em; line-height: 1.8;">

### <span style="color: #2196F3;">Требования</span>

<ul style="list-style-type: none; padding: 0; margin: 0;">
  <li>✔️ Установленный <strong>JDK 17</strong> или новее.</li>
  <li>✔️ Установленный <strong>Maven 3.6</strong> или новее.</li>
</ul>

---

### <span style="color: #FF5722;">Шаги для запуска</span>

<ol style="font-size: 1.1em; margin-left: 20px;">
  <li>
    <strong>Клонируйте репозиторий:</strong>
    <pre style="background-color: #f4f4f4; padding: 10px; border-radius: 5px; border: 1px solid #ddd;">git clone https://github.com/ArtemChernikov/projects-info.git</pre>
  </li>
  <li>
    <strong>Перейдите в папку с проектом:</strong>
    <pre style="background-color: #f4f4f4; padding: 10px; border-radius: 5px; border: 1px solid #ddd;">cd projects-info</pre>
  </li>
  <li>
    <strong>Выполните сборку проекта:</strong>
    <pre style="background-color: #f4f4f4; padding: 10px; border-radius: 5px; border: 1px solid #ddd;">mvn clean install</pre>
  </li>
  <li>
    <strong>Запустите приложение:</strong>
    <pre style="background-color: #f4f4f4; padding: 10px; border-radius: 5px; border: 1px solid #ddd;">mvn spring-boot:run</pre>
  </li>
  <li>
    <strong>Откройте приложение в браузере по адресу:</strong>
    <pre style="background-color: #f4f4f4; padding: 10px; border-radius: 5px; border: 1px solid #ddd;">http://localhost:8080</pre>
  </li>
</ol>

</div>
## Скриншоты интерфейса

![login_page](src/main/resources/static/application-images/login_page.png)

---
