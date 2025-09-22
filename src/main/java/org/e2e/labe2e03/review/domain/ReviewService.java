package org.e2e.labe2e03.review.domain;

import lombok.RequiredArgsConstructor;
import org.e2e.labe2e03.driver.exception.DriverNotFoundException;
import org.e2e.labe2e03.driver.infrastructure.DriverRepository;
import org.e2e.labe2e03.exception.BadRequestException;
import org.e2e.labe2e03.review.dto.DriverReviewResponseDto;
import org.e2e.labe2e03.review.dto.ReviewRequestDto;
import org.e2e.labe2e03.review.exception.ReviewNotFoundException;
import org.e2e.labe2e03.review.exception.ReviewTargetNotFoundException;
import org.e2e.labe2e03.review.infrastructure.ReviewRepository;
import org.e2e.labe2e03.ride.domain.Ride;
import org.e2e.labe2e03.ride.domain.Status;
import org.e2e.labe2e03.ride.exception.RideNotFoundException;
import org.e2e.labe2e03.ride.infrastructure.RideRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;

    private final RideRepository rideRepository;

    private final DriverRepository driverRepository;

    private final ModelMapper modelMapper;

    public Review createReview(ReviewRequestDto reviewRequestDto) {
        Ride ride = rideRepository
                .findById(reviewRequestDto.getRideId())
                .orElseThrow(RideNotFoundException::new);
        Review review = modelMapper.map(reviewRequestDto, Review.class);
        review.setRide(ride);

        if (!Objects.equals(ride.getStatus(), Status.COMPLETED))
            throw new BadRequestException("Ride not completed");

        if (Objects.equals(reviewRequestDto.getTargetId(), ride.getDriver().getId())) {
            review.setTarget(ride.getDriver());
            review.setAuthor(ride.getPassenger());
        } else if (Objects.equals(reviewRequestDto.getTargetId(), ride.getPassenger().getId())) {
            review.setTarget(ride.getPassenger());
            review.setAuthor(ride.getDriver());
        } else {
            throw new ReviewTargetNotFoundException();
        }

        return reviewRepository.save(review);
    }

    public void deleteReviewById(Long id) {
        reviewRepository
                .findById(id)
                .orElseThrow(ReviewNotFoundException::new);
        reviewRepository.deleteById(id);
    }

    public Page<DriverReviewResponseDto> getReviewsByDriverId(Long driverId, Pageable pageable) {
        if (!driverRepository.existsById(driverId))
            throw new DriverNotFoundException();
        return reviewRepository.findByTargetId(driverId, pageable);
    }
}