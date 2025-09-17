package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты для проверки сериализации и десериализации ItemRequestDtoRequest
 * с использованием аннотации @JsonTest для изолированного тестирования JSON
 */
@JsonTest
public class ItemRequestDtoRequestJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDtoRequest> json;

    // Тест сериализации объекта в JSON
    @Test
    public void serialize_shouldProduceCorrectJson() throws Exception {
        ItemRequestDtoRequest dto = new ItemRequestDtoRequest("Нужна дрель");

        assertThat(json.write(dto)).isEqualToJson("""
            {
                "description": "Нужна дрель"
            }
            """);
    }

    // Тест десериализации JSON в объект
    @Test
    public void deserialize_shouldParseJsonCorrectly() throws Exception {
        String jsonContent = """
            {
                "description": "Нужна дрель"
            }
            """;

        ItemRequestDtoRequest result = json.parseObject(jsonContent);

        assertThat(result.getDescription()).isEqualTo("Нужна дрель");
    }

    // Тест сериализации с пустой строкой
    @Test
    public void serialize_shouldHandleEmptyString() throws Exception {
        ItemRequestDtoRequest dto = new ItemRequestDtoRequest("");

        assertThat(json.write(dto)).isEqualToJson("""
            {
                "description": ""
            }
            """);
    }

    // Тест десериализации с пустой строкой
    @Test
    public void deserialize_shouldParseEmptyString() throws Exception {
        String jsonContent = """
            {
                "description": ""
            }
            """;

        ItemRequestDtoRequest result = json.parseObject(jsonContent);

        assertThat(result.getDescription()).isEqualTo("");
    }

    // Тест сериализации с null значением
    @Test
    public void serialize_shouldHandleNullValue() throws Exception {
        ItemRequestDtoRequest dto = new ItemRequestDtoRequest(null);

        assertThat(json.write(dto)).isEqualToJson("""
            {
                "description": null
            }
            """);
    }

    // Тест десериализации с null значением
    @Test
    public void deserialize_shouldParseNullValue() throws Exception {
        String jsonContent = """
            {
                "description": null
            }
            """;

        ItemRequestDtoRequest result = json.parseObject(jsonContent);

        assertThat(result.getDescription()).isNull();
    }
}