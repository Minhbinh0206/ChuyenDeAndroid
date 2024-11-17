package com.example.socialmediatdcproject.API;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.socialmediatdcproject.model.Assist;
import com.example.socialmediatdcproject.model.Event;
import com.example.socialmediatdcproject.model.RollCall;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

public class EventAPI {
    private DatabaseReference eventDatabase;

    public EventAPI() {
        // Khởi tạo reference đến nút "Events" trong Firebase
        eventDatabase = FirebaseDatabase.getInstance().getReference("Events");
    }

    // Thêm sự kiện mới vào Firebase
    public void addEvent(Event event) {
        int eventId = event.getEventId();
        eventDatabase.child(String.valueOf(eventId)).setValue(event).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("EventAPI", "Event added successfully.");
            } else {
                Log.e("EventAPI", "Failed to add event.", task.getException());
            }
        });
    }

    public void listenForEventStatusChange(int eventId, final EventStatusCallback callback) {
        eventDatabase.child(String.valueOf(eventId)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Event event = snapshot.getValue(Event.class);
                if (event != null) {
                    callback.onEventStatusChanged(event);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("EventAPI", "Error fetching event status: " + error.getMessage());
            }
        });
    }

    // Define the interface for status callback
    public interface EventStatusCallback {
        void onEventStatusChanged(Event event);
    }

    // Lắng nghe sự thay đổi của thuộc tính isAssist trong userAssist của một Event
    public void listenForUserAssistStatusChange(int eventId, int userId, final UserAssistStatusCallback callback) {
        eventDatabase.child(String.valueOf(eventId)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Event event = snapshot.getValue(Event.class);
                if (event != null && event.getUserAssist() != null) {
                    List<Assist> userAssist = event.getUserAssist();
                    for (Assist assist : userAssist) {
                        if (assist.getUserId() == userId) {
                            // Gọi callback với Assist khi có sự thay đổi
                            callback.onUserAssistStatusChanged(assist);
                            break; // Thoát vòng lặp sau khi tìm thấy
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("EventAPI", "Error fetching event for userAssist status changes: " + error.getMessage());
            }
        });
    }

    // Định nghĩa interface UserAssistStatusCallback
    public interface UserAssistStatusCallback {
        void onUserAssistStatusChanged(Assist assist);
    }

    // Cập nhật sự kiện
    public void updateEvent(Event event) {
        int eventId = event.getEventId();
        eventDatabase.child(String.valueOf(eventId)).setValue(event).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("EventAPI", "Event updated successfully.");
            } else {
                Log.e("EventAPI", "Failed to update event.", task.getException());
            }
        });
    }

    // Xóa sự kiện
    public void deleteEvent(int eventId) {
        eventDatabase.child(String.valueOf(eventId)).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("EventAPI", "Event deleted successfully.");
            } else {
                Log.e("EventAPI", "Failed to delete event.", task.getException());
            }
        });
    }

    // Lấy tất cả sự kiện
    public void getAllEvents(final EventCallback callback) {
        eventDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Event> eventList = new ArrayList<>();
                for (DataSnapshot eventSnapshot : snapshot.getChildren()) {
                    Event event = eventSnapshot.getValue(Event.class);
                    if (event != null) {
                        eventList.add(event);
                    }
                }
                callback.onEventsReceived(eventList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("EventAPI", "Error fetching events: " + error.getMessage());
            }
        });
    }

    // Lấy sự kiện theo ID
    public void getEventById(int eventId, final EventCallback callback) {
        eventDatabase.child(String.valueOf(eventId)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Event event = snapshot.getValue(Event.class);
                callback.onEventReceived(event);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("EventAPI", "Error fetching event by ID: " + error.getMessage());
            }
        });
    }

    // Cập nhật trạng thái của UserAssist trong Event
    public void updateUserAssistStatus(int eventId, int userId, boolean isAssisted, UpdateCallback callback) {
        eventDatabase.child(String.valueOf(eventId)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Event event = snapshot.getValue(Event.class);
                if (event != null) {
                    List<Assist> userAssistList = event.getUserAssist();
                    if (userAssistList != null) {
                        for (Assist assist : userAssistList) {
                            if (assist.getUserId() == userId) { // Sử dụng equals cho String
                                assist.setAssist(isAssisted);
                                break;
                            }
                        }
                        // Cập nhật lại sự kiện với danh sách đã thay đổi
                        eventDatabase.child(String.valueOf(eventId)).setValue(event).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d("EventAPI", "User assist status updated successfully.");
                                callback.onUpdateSuccess(); // Thông báo thành công
                            } else {
                                Log.e("EventAPI", "Failed to update user assist status.", task.getException());
                                callback.onUpdateFailed(); // Thông báo thất bại
                            }
                        });
                    }
                } else {
                    Log.e("EventAPI", "Event not found.");
                    callback.onUpdateFailed(); // Thông báo thất bại
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("EventAPI", "Error fetching event: " + error.getMessage());
                callback.onUpdateFailed(); // Thông báo thất bại
            }
        });
    }

    // Interface Callback
    public interface UpdateCallback {
        void onUpdateSuccess();
        void onUpdateFailed();
    }

    // Cập nhật trạng thái isVerified của UserJoin trong Event
    public void updateUserJoinVerification(int eventId, String sNum, boolean isVerified) {
        eventDatabase.child(String.valueOf(eventId)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Event event = snapshot.getValue(Event.class);
                if (event != null) {
                    List<RollCall> userJoin = event.getUserJoin();
                    if (userJoin != null) {
                        for (RollCall rollCall : userJoin) {
                            if (rollCall.getStudentNumber().equals(sNum)) {
                                rollCall.setIsVerify(isVerified ? RollCall.SENT : RollCall.JOIN); // Cập nhật trạng thái
                                break; // Thoát khỏi vòng lặp sau khi tìm thấy
                            }
                        }
                        // Cập nhật lại sự kiện với danh sách đã thay đổi
                        eventDatabase.child(String.valueOf(eventId)).setValue(event).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d("EventAPI", "User verification status updated successfully.");
                            } else {
                                Log.e("EventAPI", "Failed to update user verification status.", task.getException());
                            }
                        });
                    }
                } else {
                    Log.e("EventAPI", "Event not found.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("EventAPI", "Error fetching event: " + error.getMessage());
            }
        });
    }

    public void updateUserJoinVerificationDone(int eventId, String sNum) {
        eventDatabase.child(String.valueOf(eventId)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Event event = snapshot.getValue(Event.class);
                if (event != null) {
                    List<RollCall> userJoin = event.getUserJoin();
                    if (userJoin != null) {
                        for (RollCall rollCall : userJoin) {
                            if (rollCall.getStudentNumber().equals(sNum)) {
                                rollCall.setIsVerify(2);
                                break; // Thoát khỏi vòng lặp sau khi tìm thấy
                            }
                        }
                        // Cập nhật lại sự kiện với danh sách đã thay đổi
                        eventDatabase.child(String.valueOf(eventId)).setValue(event).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d("EventAPI", "User verification status updated successfully.");
                            } else {
                                Log.e("EventAPI", "Failed to update user verification status.", task.getException());
                            }
                        });
                    }
                } else {
                    Log.e("EventAPI", "Event not found.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("EventAPI", "Error fetching event: " + error.getMessage());
            }
        });
    }

    // Lấy sự kiện theo ID
    public void getEventByStatus(int status, final EventCallback callback) {
        eventDatabase.orderByChild(String.valueOf(status)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Event event = snapshot.getValue(Event.class);
                callback.onEventReceived(event);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("EventAPI", "Error fetching event by ID: " + error.getMessage());
            }
        });
    }

    // Lấy sự kiện theo admin ID
    public void getEventsByAdminId(int adminId, final EventCallback callback) {
        eventDatabase.orderByChild("adminEventId").equalTo(adminId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Event> eventList = new ArrayList<>();
                for (DataSnapshot eventSnapshot : snapshot.getChildren()) {
                    Event event = eventSnapshot.getValue(Event.class);
                    if (event != null) {
                        eventList.add(event);
                    }
                }
                callback.onEventsReceived(eventList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("EventAPI", "Error fetching events by admin ID: " + error.getMessage());
            }
        });
    }

    // Lắng nghe sự thay đổi của danh sách userJoin trong Event
    public void listenForNewRollCall(int eventId, final NewRollCallCallback callback) {
        eventDatabase.child(String.valueOf(eventId)).addValueEventListener(new ValueEventListener() {
            private int previousCount = 0;

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Event event = snapshot.getValue(Event.class);
                if (event != null && event.getUserJoin() != null) {
                    List<RollCall> userJoin = event.getUserJoin();
                    int currentCount = userJoin.size();

                    // Kiểm tra xem có phần tử mới được thêm không
                    if (currentCount > previousCount) {
                        RollCall newRollCall = userJoin.get(currentCount - 1); // Lấy phần tử mới nhất
                        callback.onNewRollCallAdded(newRollCall);
                    }

                    // Cập nhật số lượng để kiểm tra cho lần gọi tiếp theo
                    previousCount = currentCount;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("EventAPI", "Error fetching event for new RollCall: " + error.getMessage());
            }
        });
    }

    // Định nghĩa interface NewRollCallCallback
    public interface NewRollCallCallback {
        void onNewRollCallAdded(RollCall rollCall);
    }


    // Lắng nghe sự thay đổi của isVerified cho một RollCall cụ thể trong danh sách userJoin của Event
    public void listenForUserJoinVerificationChange(int eventId, String sNum, final RollCallCallback callback) {
        eventDatabase.child(String.valueOf(eventId)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Event event = snapshot.getValue(Event.class);
                if (event != null && event.getUserJoin() != null) {
                    List<RollCall> userJoin = event.getUserJoin();
                    for (RollCall rollCall : userJoin) {
                        if (rollCall.getStudentNumber().equals(sNum)) {
                            // Trả về đối tượng RollCall khi có sự thay đổi
                            callback.onRollCallChanged(rollCall);
                            break; // Thoát vòng lặp sau khi tìm thấy
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("EventAPI", "Error fetching event for verification changes: " + error.getMessage());
            }
        });
    }

    // Định nghĩa interface RollCallCallback
    public interface RollCallCallback {
        void onRollCallChanged(RollCall rollCall);
    }

    // Lấy sự kiện theo trạng thái
    public void getEventsByStatus(int status, final EventCallback callback) {
        eventDatabase.orderByChild("status").equalTo(status).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Event> eventList = new ArrayList<>();
                for (DataSnapshot eventSnapshot : snapshot.getChildren()) {
                    Event event = eventSnapshot.getValue(Event.class);
                    if (event != null) {
                        eventList.add(event);
                    }
                }
                callback.onEventsReceived(eventList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("EventAPI", "Error fetching events by status: " + error.getMessage());
            }
        });
    }

    // Định nghĩa interface EventCallback
    public interface EventCallback {
        void onEventReceived(Event event);
        void onEventsReceived(List<Event> events);
    }
}
