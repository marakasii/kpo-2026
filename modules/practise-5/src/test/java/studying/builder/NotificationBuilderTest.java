package studying.builder;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import studying.model.NotificationChannel;
import studying.model.NotificationDraft;

/** Unit-тесты Builder: проверяем результат и ошибки без Spring. */
@DisplayName("Builder черновика уведомления")
class NotificationBuilderTest {
    private static final int HIGH_PRIORITY = 3;
    private static final int INVALID_HIGH_PRIORITY = 4;
    private static final LocalDateTime CREATED_AT =
            LocalDateTime.parse("2026-10-01T10:15:00");

    @Test
    @DisplayName("Builder переносит в черновик все заданные поля")
    void создаётЧерновикСоВсемиПолями() {
        NotificationDraft draft = validBuilder()
                .priority(HIGH_PRIORITY).build();

        assertAll(
                () -> assertEquals(NotificationChannel.EMAIL, draft.channel()),
                () -> assertEquals("student@example.org", draft.recipient()),
                () -> assertEquals("Ваш заказ передан в доставку",
                        draft.text()),
                () -> assertEquals(CREATED_AT, draft.createdAt()),
                () -> assertEquals(HIGH_PRIORITY, draft.priority()));
    }

    @Test
    @DisplayName("Без указания приоритета используется обычный приоритет 2")
    void используетОбычныйПриоритетПоУмолчанию() {
        assertEquals(2, validBuilder().build().priority());
    }

    @ParameterizedTest(name = "Допустимый приоритет: {0}")
    @ValueSource(ints = {1, 2, HIGH_PRIORITY})
    @DisplayName("Builder принимает приоритеты от 1 до 3")
    void принимаетДопустимыйПриоритет(final int priority) {
        assertEquals(priority, validBuilder().priority(priority)
                .build().priority());
    }

    @ParameterizedTest(name = "Недопустимый приоритет: {0}")
    @ValueSource(ints = {-1, 0, INVALID_HIGH_PRIORITY, Integer.MAX_VALUE})
    @DisplayName("Builder отклоняет приоритет вне диапазона")
    void отклоняетНедопустимыйПриоритет(final int priority) {
        assertThrows(IllegalArgumentException.class,
                () -> validBuilder().priority(priority).build());
    }

    @ParameterizedTest(name = "Недопустимый текст: «{0}»")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t\n"})
    @DisplayName("Builder отклоняет отсутствующий или пустой текст")
    void отклоняетПустойТекст(final String text) {
        assertThrows(IllegalArgumentException.class,
                () -> validBuilder().text(text).build());
    }

    @ParameterizedTest(name = "Недопустимый получатель: «{0}»")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t\n"})
    @DisplayName("Builder отклоняет отсутствующего или пустого получателя")
    void отклоняетПустогоПолучателя(final String recipient) {
        assertThrows(IllegalArgumentException.class,
                () -> validBuilder().recipient(recipient).build());
    }

    @Test
    @DisplayName("Builder требует канал")
    void отклоняетОтсутствующийКанал() {
        assertThrows(IllegalArgumentException.class,
                () -> validBuilder().channel(null).build());
    }

    @Test
    @DisplayName("Builder требует явно заданное время")
    void отклоняетОтсутствующееВремя() {
        assertThrows(IllegalArgumentException.class,
                () -> validBuilder().createdAt(null).build());
    }

    @Test
    @DisplayName("Повторное использование Builder не меняет готовый черновик")
    void сохраняетНеизменяемостьЧерновика() {
        NotificationBuilder builder = validBuilder();
        NotificationDraft first = builder.build();

        NotificationDraft second = builder.text("Новый текст").build();

        assertEquals("Ваш заказ передан в доставку", first.text());
        assertEquals("Новый текст", second.text());
    }

    // Каждый тест получает новый Builder: сценарии не влияют друг на друга.
    private static NotificationBuilder validBuilder() {
        return new NotificationBuilder()
                .channel(NotificationChannel.EMAIL)
                .recipient("student@example.org")
                .text("Ваш заказ передан в доставку")
                .createdAt(CREATED_AT);
    }
}
