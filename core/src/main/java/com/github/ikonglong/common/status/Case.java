package com.github.ikonglong.common.status;

import com.github.ikonglong.common.status.Status.Code;

import static java.util.Objects.requireNonNull;

/**
 * An instance of this type represents a kind of more specific error situation.
 */
public interface Case {

    /**
     * Returns a string that uniquely identifies this case. It can be a numerical code or a
     * descriptive title/name. For example, two numerical codes: 1000, 1_1_1000; a descriptive
     * title/name: purchase_limit_exceeded.
     */
    String identifier();

    /**
     * Returns the status code to which this case is mapped to.
     */
    Status.Code statusCode();

    /**
     * Default implementation for {@link Case} interface. It is mainly used for deserialization.
     */
    class Default implements Case {
        private final String identifier;
        private final Status.Code statusCode;

        public Default(String identifier, int statusCode) {
            this.identifier = requireNonNull(identifier, "identifier");
            this.statusCode = Status.fromCodeValue(statusCode).code();
        }

        public Default(String identifier, String status) {
            this.identifier = requireNonNull(identifier, "identifier");
            this.statusCode = Status.fromCode(Code.valueOf(status.toUpperCase())).code();
        }

        public Default(String identifier, Code statusCode) {
            this.identifier = requireNonNull(identifier, "identifier");
            this.statusCode = requireNonNull(statusCode, "statusCode");
        }

        @Override
        public String identifier() {
            return identifier;
        }

        @Override
        public Status.Code statusCode() {
            return statusCode;
        }

        @Override
        public String toString() {
            return identifier;
        }
    }
}
