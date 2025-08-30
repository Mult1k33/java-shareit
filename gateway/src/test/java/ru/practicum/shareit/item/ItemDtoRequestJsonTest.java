package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.ItemDtoRequest;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

/**
 * Тесты для проверки сериализации и десериализации ItemDtoRequest
 * с использованием аннотации @JsonTest для изолированного тестирования JSON
 */
@JsonTest
public class ItemDtoRequestJsonTest {

    @Autowired
    private JacksonTester<ItemDtoRequest> json;

    // Тест сериализации полного объекта в JSON
    @Test
    public void serialize_shouldProduceCorrectJsonStructure() throws Exception {
        ItemDtoRequest dto = new ItemDtoRequest("Дрель", "Аккумуляторная дрель",
                true, 123L);

        assertThat(json.write(dto))
                .hasJsonPathStringValue("$.name", "Дрель")
                .hasJsonPathStringValue("$.description", "Аккумуляторная дрель")
                .hasJsonPathBooleanValue("$.available", true)
                .hasJsonPathNumberValue("$.requestId", 123L);
    }

    // Тест десериализации JSON в объект
    @Test
    public void deserialize_shouldParseJsonCorrectly() throws Exception {
        String jsonContent = """
                {
                    "name": "Дрель",
                    "description": "Аккумуляторная дрель",
                    "available": true,
                    "requestId": 123
                }
                """;

        ItemDtoRequest result = json.parseObject(jsonContent);

        assertThat(result)
                .hasFieldOrPropertyWithValue("name", "Дрель")
                .hasFieldOrPropertyWithValue("description", "Аккумуляторная дрель")
                .hasFieldOrPropertyWithValue("available", true)
                .hasFieldOrPropertyWithValue("requestId", 123L);
    }

    // Тест сериализации без requestId (nullable поле)
    @Test
    public void serialize_shouldOmitNullRequestId() throws Exception {
        ItemDtoRequest dto = new ItemDtoRequest("Дрель", "Аккумуляторная дрель",
                true, null);

        assertThat(json.write(dto))
                .hasJsonPathStringValue("$.name", "Дрель")
                .doesNotHaveJsonPathValue("$.requestId");
    }

    // Тест десериализации без requestId
    @Test
    public void deserialize_shouldHandleMissingRequestId() throws Exception {
        String jsonContent = """
                {
                    "name": "Дрель",
                    "description": "Аккумуляторная дрель",
                    "available": true
                }
                """;

        ItemDtoRequest result = json.parseObject(jsonContent);

        assertThat(result)
                .hasFieldOrPropertyWithValue("name", "Дрель")
                .hasFieldOrPropertyWithValue("requestId", null);
    }
}