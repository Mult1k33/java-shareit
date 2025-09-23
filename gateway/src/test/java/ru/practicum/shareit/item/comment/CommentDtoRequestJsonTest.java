package ru.practicum.shareit.item.comment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.comment.dto.CommentDtoRequest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты для проверки сериализации и десериализации CommentDtoRequest
 * с использованием аннотации @JsonTest для изолированного тестирования JSON
 */
@JsonTest
public class CommentDtoRequestJsonTest {

    @Autowired
    private JacksonTester<CommentDtoRequest> json;

    // Тест корректной сериализации объекта в JSON
    @Test
    public void serialize_shouldProduceCorrectJsonStructure() throws Exception {
        CommentDtoRequest dto = CommentDtoRequest.builder()
                .text("Тестовый комментарий")
                .build();

        assertThat(json.write(dto))
                .hasJsonPathStringValue("$.text", "Тестовый комментарий");
    }

    // Тест корректной десериализации JSON в объект
    @Test
    public void deserialize_shouldParseJsonCorrectly() throws Exception {
        String jsonContent = "{\"text\": \"Комментарий из JSON\"}";

        CommentDtoRequest result = json.parseObject(jsonContent);

        assertThat(result.getText()).isEqualTo("Комментарий из JSON");
    }

    // Тест сериализации с пустой строкой
    @Test
    public void serialize_shouldHandleEmptyString() throws Exception {
        CommentDtoRequest dto = CommentDtoRequest.builder()
                .text("")
                .build();

        assertThat(json.write(dto))
                .hasJsonPathStringValue("$.text", "");
    }

    // Тест десериализации с пустой строкой
    @Test
    public void deserialize_shouldParseEmptyString() throws Exception {
        String jsonContent = "{\"text\": \"\"}";

        CommentDtoRequest result = json.parseObject(jsonContent);

        assertThat(result.getText()).isEqualTo("");
    }

    // Тест сериализации при null значении - используем конструктор
    @Test
    public void serialize_shouldHandleNullValue() throws Exception {
        CommentDtoRequest dto = new CommentDtoRequest(null);

        assertThat(json.write(dto))
                .hasEmptyJsonPathValue("$.text");
    }

    // Тест десериализации с null значением
    @Test
    public void deserialize_shouldParseNullValue() throws Exception {
        String jsonContent = "{\"text\": null}";

        CommentDtoRequest result = json.parseObject(jsonContent);

        assertThat(result.getText()).isNull();
    }
}