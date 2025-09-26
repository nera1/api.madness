package kr.mdns.madness.response;

import lombok.Getter;

@Getter
public class ApiResponse<T> {
    private final int code;
    private final String message;
    private final T data;

    // 생성자 숨기기 (외부에서 직접 인스턴스 생성 불가)
    public ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 기본 성공 응답 생성
     * 
     * @param data 응답 데이터
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(0, "success", data);
    }

    /**
     * 커스텀 응답 생성
     * 
     * @param code    응답 코드
     * @param message 응답 메시지
     * @param data    응답 데이터
     */
    public static <T> ApiResponse<T> of(int code, String message, T data) {
        return new ApiResponse<>(code, message, data);
    }

    /**
     * 오류 응답 생성 (데이터 없음)
     * 
     * @param code    오류 코드
     * @param message 오류 메시지
     */
    public static <T> ApiResponse<T> failure(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }
}