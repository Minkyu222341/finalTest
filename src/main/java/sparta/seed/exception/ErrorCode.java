package sparta.seed.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    /**
     * 로그인 ,토큰관련
     */
    BE_NOT_VALID_TOKEN(HttpStatus.BAD_REQUEST,"400","유효하지않은 토큰입니다."),
    MEMBER_MISMATCH(HttpStatus.BAD_REQUEST,"400","유저정보가 일치하지 않습니다."),
    NEED_A_LOGIN(HttpStatus.BAD_REQUEST, "400", "로그인이 필요합니다."),
    INDEX_NOT_FOUND(HttpStatus.BAD_REQUEST, "1001", "인덱스가 존재하지 않습니다."),
    BOARD_NOT_FOUND(HttpStatus.BAD_REQUEST, "400", "게시글을 찾을 수 없습니다."),
    UNKNOWN_ERROR(HttpStatus.BAD_REQUEST, "400", "토큰이 존재하지 않습니다."),
    WRONG_TYPE_TOKEN(HttpStatus.BAD_REQUEST, "400", "변조된 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.BAD_REQUEST, "401", "만료된 토큰입니다."),
    UNSUPPORTED_TOKEN(HttpStatus.BAD_REQUEST, "400", "변조된 토큰입니다."),
    UNKNOWN_USER(HttpStatus.BAD_REQUEST, "400", "사용자를 알 수 없습니다."),

    /**
     * 권한
     */
    ACCESS_DENIED(HttpStatus.BAD_REQUEST, "400", "권한이 없습니다."),

    /**
     * 커뮤니티
     */
    EXCESS_PARTICIPANT(HttpStatus.BAD_REQUEST,"400","참가 인원 초과"),
    INCORRECT_USERID(HttpStatus.BAD_REQUEST, "400", "작성자가 아닙니다."),
    ALREADY_PARTICIPATED(HttpStatus.BAD_REQUEST,"400","이미 참여 했습니다."),
    NOT_PARTICIPATED(HttpStatus.BAD_REQUEST,"400","캠페인 참가자만 인증글을 작성할 수 있습니다."),

    NOT_FOUND_COMMUNITY(HttpStatus.BAD_REQUEST, "400", "게시글을 찾을 수 없습니다."),
    NOT_FOUND_PROOF(HttpStatus.BAD_REQUEST, "400", "인증글을 찾을 수 없습니다."),
    NOT_FOUND_COMMENT(HttpStatus.BAD_REQUEST, "400", "댓글을 찾을 수 없습니다."),
    NOT_FOUND_IMG(HttpStatus.BAD_REQUEST, "400", "이미지를 찾을 수 없습니다."),
    EXCEED_IMG_CNT(HttpStatus.BAD_REQUEST, "400", "이미지는 최대 5장까지 등록할 수 있습니다."),
    NOT_BEGIN(HttpStatus.BAD_REQUEST,"400","캠페인이 시작하지 않았습니다."),
    ALREADY_END_COMMUNITY(HttpStatus.BAD_REQUEST,"400","캠페인이 종료되었습니다."),

    /**
     * 유저정보
     */
    CLOSED_USER(HttpStatus.BAD_REQUEST, "400", "비공개 처리된 유저입니다."),
    DUPLICATE_NICKNAME(HttpStatus.BAD_REQUEST, "400", "중복된 닉네임입니다."),

    /**
     * 미션
     */
    NOT_FOUND_MISSION(HttpStatus.BAD_REQUEST, "400", "오늘의 미션에서 찾을 수 없습니다.");




    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String msg;
    }

