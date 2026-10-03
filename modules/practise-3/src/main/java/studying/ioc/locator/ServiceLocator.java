package studying.ioc.locator;

import java.util.HashMap;
import java.util.Map;
import studying.exception.ApplicationErrorCode;
import studying.exception.ApplicationException;

/** Stores and provides application services by their contract type. */
public final class ServiceLocator {
    /** Services registered under their contract types. */
    private final Map<Class<?>, Object> services = new HashMap<>();

    /** Registers a service under its contract type.
     *
     * @param type service contract
     * @param service implementation of the contract
     * @param <T> service type
     */
    public <T> void register(final Class<T> type, final T service) {
        if (type == null || service == null) {
            throw new ApplicationException(
                    ApplicationErrorCode.VALIDATION_ERROR,
                    "Тип и реализация сервиса не могут быть null"
            );
        }
        services.put(type, service);
    }

    /** Returns a registered service.
     *
     * @param type service contract
     * @param <T> service type
     * @return registered implementation
     */
    public <T> T get(final Class<T> type) {
        final Object service = services.get(type);
        if (service == null) {
            throw new ApplicationException(
                    ApplicationErrorCode.SERVICE_NOT_FOUND,
                    "Сервис не зарегистрирован: " + type.getName()
            );
        }
        return type.cast(service);
    }
}
