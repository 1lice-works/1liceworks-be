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
    NOT_FOUND_USER(false, HttpStatus.NOT_FOUND.value(), "일치하는 유저가 없습니다."),
    NOT_FOUND_EMPLOYEE(false, HttpStatus.NOT_FOUND.value(), "일치하는 직원이 없습니다."),
    NOT_FOUND_POSITION(false, HttpStatus.NOT_FOUND.value(), "일치하는 직급이 없습니다."),
    NOT_FOUND_JOB_TITLE(false, HttpStatus.NOT_FOUND.value(), "일치하는 직책이 없습니다."),
    NOT_FOUND_USER_TYPE(false, HttpStatus.NOT_FOUND.value(), "일치하는 사용자 유형이 없습니다."),
    NOT_FOUND_CALENDAR(false, HttpStatus.NOT_FOUND.value(), "캘린더를 찾을 수 없습니다."),
    NOT_FOUND_TEAM(false, HttpStatus.NOT_FOUND.value(), "팀을 찾을 수 없습니다."),
    NOT_FOUND_EVENT(false, HttpStatus.NOT_FOUND.value(), "일정을 찾을 수 없습니다."),
    NOT_FOUND_WEBHOOK(false, HttpStatus.NOT_FOUND.value(), "웹훅을 찾을 수 없습니다."),

    FAILED_TO_FIND_RESPONSE(false, HttpStatus.NOT_FOUND.value(), "값을 불러오는데 실패하였습니다."),
    FAILED_TO_LOGIN(false, HttpStatus.NOT_FOUND.value(), "없는 아이디거나 비밀번호가 틀렸습니다."),
    FAILED_TO_MATCH_BEFORE_PASSWORD(false, HttpStatus.BAD_REQUEST.value(), "새로운 비밀번호가 현재 비밀번호와 동일합니다."),
    FAILED_TO_DELETE_IMAGE(false, HttpStatus.BAD_REQUEST.value(), "이미지 삭제에 실패했습니다."),
    FAILED_TO_UPLOAD_IMAGE(false, HttpStatus.BAD_REQUEST.value(), "이미지 업로드에 실패했습니다."),

    INVALID_AUTH_CODE(false, HttpStatus.CONFLICT.value(), "잘못된 인증코드입니다."),
    INVALID_CONTENT_TYPE(false, HttpStatus.CONFLICT.value(), "잘못된 Content-Type입니다."),
    INVALID_JWT(false, HttpStatus.UNAUTHORIZED.value(), "유효하지 않은 JWT입니다."),
    INVALID_USER_JWT(false, HttpStatus.FORBIDDEN.value(), "권한이 없는 유저의 접근입니다."),
    INVALID_EMAIL_FORMAT(false, HttpStatus.BAD_REQUEST.value(), "이메일 형식을 확인해주세요."),
    INVALID_AUTHORIZATION(false, HttpStatus.CONFLICT.value(), "잘못된 권한입니다."),

    DUPLICATED_ACCOUNT_ID(false, HttpStatus.BAD_REQUEST.value(), "중복된 계정 ID입니다."),
    DUPLICATED_EMAIL(false, HttpStatus.BAD_REQUEST.value(), "중복된 이메일입니다."),
    DUPLICATED_POSITION_NAME(false, HttpStatus.BAD_REQUEST.value(), "중복된 직급명입니다."),
    DUPLICATED_JOB_TITLE_NAME(false, HttpStatus.BAD_REQUEST.value(), "중복된 직책명입니다."),
    DUPLICATED_USER_TYPE_NAME(false, HttpStatus.BAD_REQUEST.value(), "중복된 사용자 유형입니다."),
    DUPLICATED_WEBHOOK(false, HttpStatus.BAD_REQUEST.value(), "해당 캘린더에 이미 웹훅이 등록되어 있습니다."),


    JOB_TITLE_NOT_FOUND(false, HttpStatus.NOT_FOUND.value(), "일치하는 직책이 없습니다."),

    MUST_START_TIME_BEFORE_END_TIME(false, HttpStatus.BAD_REQUEST.value(), "일정 시작 시간은 끝 시간보다 이전에 와야합니다."),

    IMAGE_DELETE_FAILED(false, HttpStatus.BAD_REQUEST.value(), "이미지 삭제에 실패했습니다."),
    IMAGE_UPLOAD_FAILED(false, HttpStatus.BAD_REQUEST.value(), "이미지 업로드에 실패했습니다."),
    MISSING_JWT(false, HttpStatus.UNAUTHORIZED.value(), "JWT를 입력해주세요."),
    UNVERIFIED_EMAIL(false, HttpStatus.BAD_REQUEST.value(), "인증되지 않은 이메일입니다."),

    /**
     * 500 : Database, Server 오류
     */
    FAILED_TO_CONNECT_DATABASE(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "데이터베이스 연결에 실패하였습니다."),
    FAILED_TO_CONNECT_SERVER(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버와의 연결에 실패하였습니다."),
    FAILED_TO_ENCRYPT_PASSWORD(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "비밀번호 암호화에 실패하였습니다."),
    FAILED_TO_DECRYPT_PASSWORD(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "비밀번호 복호화에 실패하였습니다."),
    FAILED_TO_SEND_WEBHOOK(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "웹훅 전송 중 오류가 발생했습니다."),
    UNEXPECTED_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "예상치 못한 에러가 발생했습니다."),

    /**
     * 400 : Validation Error
     */
    VALIDATION_ERROR(false, HttpStatus.BAD_REQUEST.value(), "요청 데이터 검증 오류");

    private final boolean isSuccess;
    private final int code;
    private final String message;
}
