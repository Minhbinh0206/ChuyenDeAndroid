package com.example.socialmediatdcproject.API;

import com.example.socialmediatdcproject.model.Rating;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RatingAPI {

    private DatabaseReference databaseReference;

    public RatingAPI() {
        // Tham chiếu nút gốc Ratings
        databaseReference = FirebaseDatabase.getInstance().getReference("Ratings");
    }

    public void getAllRating(int adminId, int eventId, int surveyId, AllRatingsCallback callback) {
        // Tham chiếu đến Ratings/AdminId/EventId/SurveyId
        databaseReference.child(adminId+"")
                .child(eventId+"")
                .child(String.valueOf(surveyId))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        List<Rating> ratings = new ArrayList<>();
                        for (DataSnapshot ratingSnapshot : snapshot.getChildren()) {
                            Rating rating = ratingSnapshot.getValue(Rating.class);
                            if (rating != null) {
                                ratings.add(rating); // Thêm vào danh sách đánh giá
                            }
                        }
                        // Gọi callback với danh sách đánh giá
                        callback.onSuccess(ratings);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Gọi callback khi có lỗi
                        callback.onFailure(error.toException());
                    }
                });
    }

    public void getAllRatingRealtime(int adminId, int eventId, int surveyId, AllRatingsCallback callback) {
        // Tham chiếu đến Ratings/AdminId/EventId/SurveyId
        databaseReference.child(adminId+"")
                .child(eventId+"")
                .child(String.valueOf(surveyId))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        List<Rating> ratings = new ArrayList<>();
                        for (DataSnapshot ratingSnapshot : snapshot.getChildren()) {
                            Rating rating = ratingSnapshot.getValue(Rating.class);
                            if (rating != null) {
                                ratings.add(rating); // Thêm vào danh sách đánh giá
                            }
                        }
                        // Gọi callback với danh sách đánh giá
                        callback.onSuccess(ratings);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Gọi callback khi có lỗi
                        callback.onFailure(error.toException());
                    }
                });
    }

    public interface AllRatingsCallback {
        void onSuccess(List<Rating> ratings);
        void onFailure(Exception e);
    }


    public void findRatingByUserId(int adminId, int eventId, int surveyId, int userId, SingleRatingCallback callback) {
        // Tham chiếu đến Ratings/AdminId/EventId/SurveyId
        databaseReference.child(adminId+"").child(eventId+"").child(String.valueOf(surveyId))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        for (DataSnapshot ratingSnapshot : snapshot.getChildren()) {
                            Rating rating = ratingSnapshot.getValue(Rating.class);
                            if (rating != null && rating.getUserId() == userId) {
                                // Rating tìm được, gọi callback
                                callback.onSuccess(rating);
                                return; // Dừng tìm kiếm
                            }
                        }
                        // Không tìm thấy Rating nào
                        callback.onFailure(new Exception("No rating found for userId: " + userId));
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Gọi lại callback khi có lỗi
                        callback.onFailure(error.toException());
                    }
                });
    }

    public void addRating(int adminId, int eventId, int surveyId, Rating rating) {
        // Tạo key duy nhất cho từng rating
        String uniqueKey = UUID.randomUUID().toString();

        // Tham chiếu Ratings/AdminId/EventId/SurveyId/UniqueKey
        databaseReference.child(adminId+"")
                .child(eventId+"")
                .child(String.valueOf(surveyId))
                .child(uniqueKey)
                .setValue(rating)
                .addOnSuccessListener(aVoid -> {
                    System.out.println("Rating saved successfully.");
                })
                .addOnFailureListener(e -> {
                    System.err.println("Failed to save rating: " + e.getMessage());
                });
    }

    public interface SingleRatingCallback {
        void onSuccess(Rating rating);
        void onFailure(Exception e);
    }
}