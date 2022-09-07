package sparta.seed.exception;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
//
@Getter
@Builder
public class ErrorResponse {

    private String msg;
    private String errorCode;
    private HttpStatus httpStatus;

    public static ResponseEntity<ErrorResponse> of(ErrorCode code) {
        return ResponseEntity
                .status(code.getHttpStatus())
                .body(
                        ErrorResponse.builder()
                                .msg(code.getMsg())
                                .errorCode(code.getErrorCode())
                                .httpStatus(code.getHttpStatus())
                                .build()
                );
    }

}