package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.user.dto.UserDtoRequest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты для проверки сериализации и десериализации UserDtoRequest
 * с использованием аннотации @JsonTest для изолированного тестирования JSON
 */
@JsonTest
public class UserDtoRequestJsonTest {

    @Autowired
    private JacksonTester<UserDtoRequest> json;

    // Тест сериализации объекта в JSON
    @Test
    public void serialize_shouldProduceCorrectJson() throws Exception {
        UserDtoRequest dto = new UserDtoRequest("Иван Иванов", "ivan@mail.ru");

        assertThat(json.write(dto)).isEqualToJson("""
            {
                "name": "Иван Иванов",
                "email": "ivan@mail.ru"
            }
            """);
    }

    // Тест десериализации JSON в объект
    @Test
    public void deserialize_shouldParseJsonCorrectly() throws Exception {
        String jsonContent = """
            {
                "name": "Иван Иванов",
                "email": "ivan@mail.ru"
            }
            """;

        UserDtoRequest result = json.parseObject(jsonContent);

        assertThat(result.getName()).isEqualTo("Иван Иванов");
        assertThat(result.getEmail()).isEqualTo("ivan@mail.ru");
    }

    // Тест сериализации с пустыми строками
    @Test
    public void serialize_shouldHandleEmptyStrings() throws Exception {
        UserDtoRequest dto = new UserDtoRequest("", "");

        assertThat(json.write(dto)).isEqualToJson("""
            {
                "name": "",
                "email": ""
            }
            """);
    }

    // Тест десериализации с пустыми строками
    @Test
    public void deserialize_shouldParseEmptyStrings() throws Exception {
        String jsonContent = """
            {
                "name": "",
                "email": ""
            }
            """;

        UserDtoRequest result = json.parseObject(jsonContent);

        assertThat(result.getName()).isEqualTo("");
        assertThat(result.getEmail()).isEqualTo("");
    }
}