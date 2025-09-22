package org.e2e.labe2e03.review.exception;

import org.e2e.labe2e03.exception.ResourceNotFoundException;

public class ReviewTargetNotFoundException extends ResourceNotFoundException {
    public ReviewTargetNotFoundException() {
        super("Review target not found");
    }
}
