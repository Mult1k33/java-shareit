package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.ItemUpdateDtoRequest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты для проверки сериализации и десериализации ItemUpdateDtoRequest
 * с использованием аннотации @JsonTest для изолированного тестирования JSON
 */
@JsonTest
public class ItemUpdateDtoRequestJsonTest {

    @Autowired
    private JacksonTester<ItemUpdateDtoRequest> json;

    // Тест сериализации частичного обновления
    @Test
    public void serialize_shouldHandlePartialData() throws Exception {
        ItemUpdateDtoRequest dto = new ItemUpdateDtoRequest("Дрель", null,
                false, null);

        assertThat(json.write(dto)).isEqualToJson("""
                {
                    "name": "Дрель",
                    "available": false
                }
                """);
    }

    // Тест сериализации с полными данными
    @Test
    public void serialize_shouldHandleAllFields() throws Exception {
        ItemUpdateDtoRequest dto = new ItemUpdateDtoRequest("Дрель", "Аккумуляторная дрель",
                true, 123L);

        assertThat(json.write(dto)).isEqualToJson("""
                {
                    "name": "Дрель",
                    "description": "Аккумуляторная дрель",
                    "available": true,
                    "requestId": 123
                }
                """);
    }

    // Тест сериализации с null значениями
    @Test
    public void serialize_shouldHandleNullValues() throws Exception {
        ItemUpdateDtoRequest dto = new ItemUpdateDtoRequest(null, null, null, null);

        assertThat(json.write(dto)).isEqualToJson("""
                {
                    "name": null,
                    "description": null,
                    "available": null,
                    "requestId": null
                }
                """);
    }

    // Тест десериализации частичного обновления
    @Test
    public void deserialize_shouldParsePartialData() throws Exception {
        String jsonContent = """
                {
                    "name": "Дрель",
                    "available": false
                }
                """;

        ItemUpdateDtoRequest result = json.parseObject(jsonContent);

        assertThat(result.getName()).isEqualTo("Дрель");
        assertThat(result.getAvailable()).isEqualTo(false);
        assertThat(result.getDescription()).isNull();
        assertThat(result.getRequestId()).isNull();
    }

    // Тест десериализации с полными данными
    @Test
    public void deserialize_shouldParseAllFields() throws Exception {
        String jsonContent = """
                {
                    "name": "Дрель",
                    "description": "Аккумуляторная дрель",
                    "available": true,
                    "requestId": 123
                }
                """;

        ItemUpdateDtoRequest result = json.parseObject(jsonContent);

        assertThat(result.getName()).isEqualTo("Дрель");
        assertThat(result.getDescription()).isEqualTo("Аккумуляторная дрель");
        assertThat(result.getAvailable()).isEqualTo(true);
        assertThat(result.getRequestId()).isEqualTo(123L);
    }

    // Тест десериализации с явными null значениями
    @Test
    public void deserialize_shouldHandleExplicitNulls() throws Exception {
        String jsonContent = """
                {
                    "name": null,
                    "description": null,
                    "available": null,
                    "requestId": null
                }
                """;

        ItemUpdateDtoRequest result = json.parseObject(jsonContent);

        assertThat(result.getName()).isNull();
        assertThat(result.getDescription()).isNull();
        assertThat(result.getAvailable()).isNull();
        assertThat(result.getRequestId()).isNull();
    }
}