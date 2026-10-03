package studying.exception;

/** Codes that identify errors raised by the report application. */
public enum ApplicationErrorCode {
    /** Input validation failed. */
    VALIDATION_ERROR,
    /** Writing the report file failed. */
    FILE_WRITE_ERROR,
    /** A required service could not be found. */
    SERVICE_NOT_FOUND
}
