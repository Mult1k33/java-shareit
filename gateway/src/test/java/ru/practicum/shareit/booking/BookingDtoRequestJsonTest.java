package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Тесты для проверки сериализации и десериализации BookingDtoRequest
 * с использованием аннотации @JsonTest для изолированного тестирования JSON
 */
@JsonTest
public class BookingDtoRequestJsonTest {

    @Autowired
    private JacksonTester<BookingDtoRequest> json;

    private Instant start;
    private Instant end;

    @BeforeEach
    void setUp() {
        start = Instant.parse("2024-01-01T10:00:00Z");
        end = Instant.parse("2024-01-02T10:00:00Z");
    }

    // Тест корректной сериализации объекта в JSON
    @Test
    public void serialize_shouldProduceCorrectDateFormat() throws Exception {
        BookingDtoRequest dto = BookingDtoRequest.builder()
                .itemId(1L)
                .start(start)
                .end(end)
                .build();

        assertThat(json.write(dto))
                .hasJsonPathNumberValue("$.itemId", 1L)
                .hasJsonPathStringValue("$.start", "2024-01-01T10:00:00")
                .hasJsonPathStringValue("$.end", "2024-01-02T10:00:00");
    }

    // Тест корректной десериализации JSON в объект
    @Test
    public void deserialize_shouldParseWithoutZ() throws Exception {
        String jsonContent = """
                {
                    "itemId": 1,
                    "start": "2024-01-01T10:00:00",
                    "end": "2024-01-02T10:00:00"
                }
                """;

        BookingDtoRequest result = json.parse(jsonContent).getObject();

        assertThat(result.getItemId()).isEqualTo(1L);
        assertThat(result.getStart()).isEqualTo(Instant.parse("2024-01-01T10:00:00Z"));
        assertThat(result.getEnd()).isEqualTo(Instant.parse("2024-01-02T10:00:00Z"));
    }

    // Тест проверяет обработку некорректного формата даты(InvalidFormatException)
    @Test
    public void deserialize_shouldThrowExceptionForInvalidDateFormat() throws Exception {
        String jsonContent = """
                {
                    "itemId": 1,
                    "start": "2024-01-01T10:00:00Z",
                    "end": "2024-01-02T10:00:00"
                }
                """;

        assertThatThrownBy(() -> json.parse(jsonContent))
                .isInstanceOf(InvalidFormatException.class);
    }

    // Тест проверяет обработку некорректного формата числового поля(InvalidFormatException)
    @Test
    public void deserialize_shouldThrowExceptionForInvalidItemIdFormat() throws Exception {
        String jsonContent = """
                {
                    "itemId": "not_a_number",
                    "start": "2024-01-01T10:00:00",
                    "end": "2024-01-02T10:00:00"
                }
                """;

        assertThatThrownBy(() -> json.parse(jsonContent))
                .isInstanceOf(InvalidFormatException.class);
    }

    // Тест сериализации при null значениях(Jackson обрабатывает и сериализует их как пустые значения в JSON)
    @Test
    public void serialize_shouldHandleNullValues() throws Exception {
        BookingDtoRequest dto = BookingDtoRequest.builder()
                .itemId(null)
                .start(null)
                .end(null)
                .build();

        assertThat(json.write(dto))
                .hasEmptyJsonPathValue("$.itemId")
                .hasEmptyJsonPathValue("$.start")
                .hasEmptyJsonPathValue("$.end");
    }
}