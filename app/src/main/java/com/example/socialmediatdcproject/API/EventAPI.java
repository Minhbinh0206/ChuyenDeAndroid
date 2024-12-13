package com.example.socialmediatdcproject.API;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.socialmediatdcproject.model.Assist;
import com.example.socialmediatdcproject.model.Event;
import com.example.socialmediatdcproject.model.RollCall;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import org.checkerframework.checker.units.qual.A;

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
        eventDatabase.child(String.valueOf(event.getAdminEventId())).child(String.valueOf(eventId)).setValue(event).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("EventAPI", "Event added successfully.");
            } else {
                Log.e("EventAPI", "Failed to add event.", task.getException());
            }
        });
    }

    public void listenForNewUserAssist(Event event, OnNewUserAddedListener listener) {
        // Tham chiếu đến userAssist trong sự kiện cụ thể
        DatabaseReference userAssistRef = eventDatabase.child(String.valueOf(event.getAdminEventId())).child(String.valueOf(event.getEventId())).child("userAssist");

        userAssistRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Lấy phần tử mới
                Assist newUserId = snapshot.getValue(Assist.class);
                if (newUserId != null && listener != null) {
                    // Trả về phần tử mới qua callback
                    listener.onNewUserAdded(newUserId);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Xử lý nếu phần tử bị thay đổi
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                // Xử lý nếu phần tử bị xóa
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Xử lý nếu phần tử bị di chuyển
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("UserAssistError", "Lỗi khi lắng nghe userAssist: " + error.getMessage());
            }
        });
    }

    public interface OnNewUserAddedListener {
        void onNewUserAdded(Assist assist);
    }

    public void listenForEventStatusChange(Event event, final EventStatusCallback callback) {
        eventDatabase.child(String.valueOf(event.getAdminEventId())).child(String.valueOf(event.getEventId())).addValueEventListener(new ValueEventListener() {
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
    public void listenForUserAssistStatusChange(Event event, int userId, final UserAssistStatusCallback callback) {
        eventDatabase.child(String.valueOf(event.getAdminEventId())).child(String.valueOf(event.getEventId())).addValueEventListener(new ValueEventListener() {
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
        eventDatabase.child(String.valueOf(event.getAdminEventId())).child(String.valueOf(eventId)).setValue(event).addOnCompleteListener(task -> {
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

    public void getAllEvents(final EventCallback callback) {
        eventDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Event> eventList = new ArrayList<>();

                // Duyệt qua các AdminId trong node Events
                for (DataSnapshot adminSnapshot : snapshot.getChildren()) {
                    // Duyệt qua các EventID dưới mỗi AdminId
                    for (DataSnapshot eventSnapshot : adminSnapshot.getChildren()) {
                        Event event = eventSnapshot.getValue(Event.class);
                        if (event != null) {
                            eventList.add(event); // Thêm sự kiện vào danh sách
                        }
                    }
                }
                callback.onEventsReceived(eventList); // Trả về danh sách sự kiện
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("EventAPI", "Error fetching events: " + error.getMessage());
            }
        });
    }

    public void getAllEventByOneAdmin(int adminId, final EventCallback callback) {
        eventDatabase.child(String.valueOf(adminId)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Event> eventList = new ArrayList<>();

                // Duyệt qua các EventID dưới AdminId
                for (DataSnapshot eventSnapshot : snapshot.getChildren()) {
                    Event event = eventSnapshot.getValue(Event.class);
                    if (event != null) {
                        eventList.add(event); // Thêm sự kiện vào danh sách
                    }
                }
                callback.onEventsReceived(eventList); // Trả về danh sách sự kiện
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("EventAPI", "Error fetching events: " + error.getMessage());
            }
        });
    }

    // Lấy sự kiện theo ID
    public void getEventById(int eventId, int admin, final EventCallback callback) {
        eventDatabase.child(String.valueOf(admin)).child(String.valueOf(eventId)).addListenerForSingleValueEvent(new ValueEventListener() {
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
    public void updateUserAssistStatus(int eventId, int eventAdmin, int userId, boolean isAssisted, UpdateCallback callback) {
        eventDatabase.child(String.valueOf(eventAdmin)).child(String.valueOf(eventId)).addListenerForSingleValueEvent(new ValueEventListener() {
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
                        eventDatabase.child(String.valueOf(event.getAdminEventId())).child(String.valueOf(event.getEventId())).setValue(event).addOnCompleteListener(task -> {
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
    public void updateUserJoinVerification(int eventId, int adminId, String sNum, boolean isVerified) {
        eventDatabase.child(String.valueOf(adminId)).child(String.valueOf(eventId)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Event event = snapshot.getValue(Event.class);
                if (event != null) {
                    List<RollCall> userJoin = event.getUserJoin();
                    if (userJoin != null) {
                        for (RollCall rollCall : userJoin) {
                            if (rollCall.getStudentNumber().equals(sNum)) {
                                break; // Thoát khỏi vòng lặp sau khi tìm thấy
                            }
                        }
                        // Cập nhật lại sự kiện với danh sách đã thay đổi
                        eventDatabase.child(String.valueOf(event.getAdminEventId())).child(String.valueOf(event.getEventId())).setValue(event).addOnCompleteListener(task -> {
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

    // Lắng nghe sự thay đổi của danh sách userJoin trong Event
    public void listenForNewRollCall(int eventId, int adminId ,final NewRollCallCallback callback) {
        eventDatabase.child(String.valueOf(adminId)).child(String.valueOf(eventId)).addValueEventListener(new ValueEventListener() {
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
    public void listenForCodeChange(int eventId, int adminId, final EventCallback callback) {
        eventDatabase.child(String.valueOf(adminId)).child(String.valueOf(eventId)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Event event = snapshot.getValue(Event.class);
                if (event != null && event.getUserJoin() != null) {
                    callback.onEventReceived(event);
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

    // Lắng nghe sự kiện mới được thêm
    public void listenForNewEvent(OnNewEventListener listener) {
        eventDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Lặp qua các AdminEventId
                for (DataSnapshot adminSnapshot : snapshot.getChildren()) {
                    // Lấy event mới thêm
                    Event newEvent = adminSnapshot.getValue(Event.class);
                    if (newEvent != null && listener != null) {
                        listener.onNewEventAdded(newEvent);
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Không xử lý
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                // Không xử lý
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Không xử lý
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("EventAPI", "Error listening for new events: " + error.getMessage());
            }
        });
    }

    // Interface callback để trả về event mới
    public interface OnNewEventListener {
        void onNewEventAdded(Event event);
    }

}
