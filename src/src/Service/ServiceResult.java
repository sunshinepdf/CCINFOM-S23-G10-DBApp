package Service;

public final class ServiceResult<T> {
    private final T data;
    private final String error; // null when success

    private ServiceResult(T data, String error) {
        this.data = data;
        this.error = error;
    }

    public static <T> ServiceResult<T> ok(T data) {
        return new ServiceResult<>(data, null);
    }

    public static <T> ServiceResult<T> fail(String error) {
        return new ServiceResult<>(null, error);
    }

    public boolean isSuccess() { return error == null; }
    public T getData() { return data; }
    public String getError() { return error; }
}
