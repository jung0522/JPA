package io.goorm.jpa.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Object[] args;
    private final String detailMessage;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessageKey());
        this.errorCode = errorCode;
        this.args = null;
        this.detailMessage = null;
    }

    public BusinessException(ErrorCode errorCode, String detailMessage) {
        super(errorCode.getMessageKey());
        this.errorCode = errorCode;
        this.args = null;
        this.detailMessage = detailMessage;
    }

    public BusinessException(ErrorCode errorCode, Object[] args) {
        super(errorCode.getMessageKey());
        this.errorCode = errorCode;
        this.args = args;
        this.detailMessage = null;
    }

    public BusinessException(ErrorCode errorCode, Object[] args, String detailMessage) {
        super(errorCode.getMessageKey());
        this.errorCode = errorCode;
        this.args = args;
        this.detailMessage = detailMessage;
    }

    public BusinessException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessageKey(), cause);
        this.errorCode = errorCode;
        this.args = null;
        this.detailMessage = null;
    }
}
