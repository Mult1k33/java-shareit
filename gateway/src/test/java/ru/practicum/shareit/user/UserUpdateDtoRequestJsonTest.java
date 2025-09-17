package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.user.dto.UserUpdateDtoRequest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты для проверки сериализации и десериализации UserUpdateDtoRequest
 * с использованием аннотации @JsonTest для изолированного тестирования JSON
 */
@JsonTest
public class UserUpdateDtoRequestJsonTest {

    @Autowired
    private JacksonTester<UserUpdateDtoRequest> json;

    // Тест сериализации полного обновления
    @Test
    public void serialize_shouldHandleFullUpdate() throws Exception {
        UserUpdateDtoRequest dto = new UserUpdateDtoRequest("Новое имя", "new@mail.ru");

        assertThat(json.write(dto)).isEqualToJson("""
            {
                "name": "Новое имя",
                "email": "new@mail.ru"
            }
            """);
    }

    // Тест сериализации частичного обновления (только имя)
    @Test
    public void serialize_shouldHandlePartialNameUpdate() throws Exception {
        UserUpdateDtoRequest dto = new UserUpdateDtoRequest("Только имя", null);

        assertThat(json.write(dto)).isEqualToJson("""
            {
                "name": "Только имя"
            }
            """);
    }

    // Тест сериализации частичного обновления (только email)
    @Test
    public void serialize_shouldHandlePartialEmailUpdate() throws Exception {
        UserUpdateDtoRequest dto = new UserUpdateDtoRequest(null, "only@mail.ru");

        assertThat(json.write(dto)).isEqualToJson("""
            {
                "email": "only@mail.ru"
            }
            """);
    }

    // Тест десериализации полного обновления
    @Test
    public void deserialize_shouldParseFullUpdate() throws Exception {
        String jsonContent = """
            {
                "name": "Новое имя",
                "email": "new@mail.ru"
            }
            """;

        UserUpdateDtoRequest result = json.parseObject(jsonContent);

        assertThat(result.getName()).isEqualTo("Новое имя");
        assertThat(result.getEmail()).isEqualTo("new@mail.ru");
    }

    // Тест десериализации частичного обновления (только имя)
    @Test
    public void deserialize_shouldParsePartialNameUpdate() throws Exception {
        String jsonContent = """
            {
                "name": "Только имя"
            }
            """;

        UserUpdateDtoRequest result = json.parseObject(jsonContent);

        assertThat(result.getName()).isEqualTo("Только имя");
        assertThat(result.getEmail()).isNull();
    }

    // Тест десериализации частичного обновления (только email)
    @Test
    public void deserialize_shouldParsePartialEmailUpdate() throws Exception {
        String jsonContent = """
            {
                "email": "only@mail.ru"
            }
            """;

        UserUpdateDtoRequest result = json.parseObject(jsonContent);

        assertThat(result.getName()).isNull();
        assertThat(result.getEmail()).isEqualTo("only@mail.ru");
    }

    // Тест десериализации с null значениями
    @Test
    public void deserialize_shouldHandleNullValues() throws Exception {
        String jsonContent = """
            {
                "name": null,
                "email": null
            }
            """;

        UserUpdateDtoRequest result = json.parseObject(jsonContent);

        assertThat(result.getName()).isNull();
        assertThat(result.getEmail()).isNull();
    }

    // Тест десериализации с пустыми полями
    @Test
    public void deserialize_shouldHandleEmptyFields() throws Exception {
        String jsonContent = """
            {
                "name": "",
                "email": ""
            }
            """;

        UserUpdateDtoRequest result = json.parseObject(jsonContent);

        assertThat(result.getName()).isEqualTo("");
        assertThat(result.getEmail()).isEqualTo("");
    }
}