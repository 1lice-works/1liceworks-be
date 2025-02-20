package com.elice.iliceworksbe.common.exception;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 에러 코드 관리
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    /**
     * 200 : 요청 성공
     */
    SUCCESS(true, HttpStatus.OK.value(), "요청 성공"),
    NO_CONTENT(true, HttpStatus.NO_CONTENT.value(), "요청 성공 (no content)"),
    CREATED(true, HttpStatus.CREATED.value(), "요청 성공 (created)"),


    /**
     * 400 : Request, Response 오류
     */
    USERS_INFO_UNKNOWN(false, HttpStatus.BAD_REQUEST.value(), "알 수 없는 유저입니다."),

    UNVERIFIED_EMAIL(false, HttpStatus.BAD_REQUEST.value(), "인증되지 않은 이메일입니다."),
    POST_USERS_INVALID_EMAIL(false, HttpStatus.BAD_REQUEST.value(), "이메일 형식을 확인해주세요."),
    POST_USERS_EXISTS_ACCOUNTID(false,HttpStatus.BAD_REQUEST.value(),"중복된 계정ID입니다."),
    POST_TEST_EXISTS_MEMO(false,HttpStatus.BAD_REQUEST.value(),"중복된 메모입니다."),

    WRONG_AUTH_CODE(false, HttpStatus.CONFLICT.value(), "잘못된 인증코드입니다."),

    RESPONSE_ERROR(false, HttpStatus.NOT_FOUND.value(), "값을 불러오는데 실패하였습니다."),

    DUPLICATED_ACCOUNTID(false, HttpStatus.BAD_REQUEST.value(), "중복된 계정ID입니다."),
    DUPLICATED_EMAIL(false, HttpStatus.BAD_REQUEST.value(), "중복된 이메일입니다."),
    FAILED_TO_LOGIN(false,HttpStatus.NOT_FOUND.value(),"없는 아이디거나 비밀번호가 틀렸습니다."),
    EMPTY_JWT(false, HttpStatus.UNAUTHORIZED.value(), "JWT를 입력해주세요."),
    INVALID_JWT(false, HttpStatus.UNAUTHORIZED.value(), "유효하지 않은 JWT입니다."),
    INVALID_USER_JWT(false,HttpStatus.FORBIDDEN.value(),"권한이 없는 유저의 접근입니다."),
    NOT_FIND_USER(false,HttpStatus.NOT_FOUND.value(),"일치하는 유저가 없습니다."),

    USER_TYPE_NOT_FOUND(false, HttpStatus.NOT_FOUND.value(), "일치하는 유저 타입이 없습니다."),
    POSITION_NOT_FOUND(false, HttpStatus.NOT_FOUND.value(), "일치하는 직급이 없습니다."),

    EVENT_NOT_FOUND(false, HttpStatus.NOT_FOUND.value(), "일정을 찾을 수 없습니다."),
    JOB_TITLE_NOT_FOUND(false, HttpStatus.NOT_FOUND.value(), "일치하는 직책이 없습니다."),

    ROLE_PERMISSION_DENIED(false, HttpStatus.FORBIDDEN.value(), "해당 작업에 대한 권한이 없습니다."),
    EMPLOYEE_NOT_FOUND(false, HttpStatus.NOT_FOUND.value(), "일치하는 직원이 없습니다."),
    /**
     * 500 :  Database, Server 오류
     */
    DATABASE_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버와의 연결에 실패하였습니다."),
    PASSWORD_ENCRYPTION_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "비밀번호 암호화에 실패하였습니다."),
    PASSWORD_DECRYPTION_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "비밀번호 복호화에 실패하였습니다."),

    UNEXPECTED_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "예상치 못한 에러가 발생했습니다."),

    /**
     * 400 : Validation Error
     */
    VALIDATION_ERROR(false, HttpStatus.BAD_REQUEST.value(), "요청 데이터 검증 오류"),
    WRONG_AUTHORIZATION(false, HttpStatus.CONFLICT.value(), "잘못된 권한입니다.");

    private final boolean isSuccess;
    private final int code;
    private final String message;
}
