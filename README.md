# Information_systems_lab1

---

* Студент: `Рыскаль Никита Алексеевич`
* Группа: `P3318`
* ИСУ: `368738`

---

## Гайд по запуску

- Выполняем в терминале команду
    ```bash
     git clone https://github.com/bullerun/Information_systems_lab1.git
     ```

- Если вы на **Windows**
  - создаем в корне проекта файл `.env`
  - пишем в `.env`
     ```
     DB_URL=jdbc:postgresql://localhost:5432/your_database_name
     DB_USERNAME=your_database_username
     DB_PASSWORD=your_database_password
     ``` 
- Если вы на **Linux**, то можете просто выполнить данную команду:
  - Выполняем команду в терминале
     ```bash 
     echo -e DB_URL=jdbc:postgresql://localhost:5432/your_database_name \\n\
    DB_USERNAME=your_database_username \\n\
    DB_PASSWORD=your_database_password > .env
     ```
- Не забудьте изменить `Environments` в `.env`
- Теперь вы можете запустить код в своей Idea