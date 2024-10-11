package com.example.socialmediatdcproject.database;

import com.example.socialmediatdcproject.model.Notify;
import com.example.socialmediatdcproject.model.User;

import java.util.ArrayList;

public class NotifyDatabase {
    protected ArrayList<Notify> notifiesDtb = new ArrayList<>();
    UserDatabase userDatabase = new UserDatabase();

    // Tạo mảng chỉ có các sinh viên
    ArrayList<User> students = new ArrayList<>();

    public ArrayList<Notify> dataNotifies() {
        // Lấy danh sách sinh viên từ cơ sở dữ liệu người dùng
        for (User u : userDatabase.dataUser()) {
            if (u.getRoleId() == User.ROLE_STUDENT) {
                students.add(u);
            }
        }

        // Danh sách nội dung thông báo
        String[] notifyContents = {
                "Hãy tham gia buổi họp mặt sinh viên vào thứ 6 tới!",
                "Nhớ nộp bài tập trước hạn vào cuối tuần này.",
                "Thông báo: Lịch thi giữa kỳ đã được công bố.",
                "Có một buổi hội thảo về lập trình vào thứ 3 tới, mời các bạn tham gia.",
                "Cần tuyển tình nguyện viên cho sự kiện từ thiện vào tháng tới.",
                "Chúc mừng các bạn đã đạt giải trong cuộc thi lập trình vừa qua!",
                "Đừng quên tham gia lớp học thêm vào thứ 4 hàng tuần.",
                "Thông báo: Chương trình học bổng cho sinh viên năm cuối.",
                "Có cuộc thi thể thao vào cuối tháng này, hãy đăng ký tham gia!",
                "Mời các bạn tham gia cuộc thi sáng tạo khoa học diễn ra vào tháng tới."
        };

        // Tạo 10 thông báo với nội dung khác nhau
        for (int i = 0; i < notifyContents.length; i++) {
            Notify notify = new Notify();
            notify.setNotifyId(i + 1); // ID thông báo
            notify.setUserSendId(User.ID_ADMIN_DEPARTMENT_CNTT); // ID người gửi (có thể thay đổi theo ý)
            notify.setNotifyTitle("Thông báo " + (i + 1)); // Tiêu đề thông báo
            notify.setNotifyContent(notifyContents[i]); // Nội dung thông báo

            // Thiết lập danh sách sinh viên nhận thông báo
            notify.setUserGetId(new ArrayList<>(students)); // Gửi cho tất cả sinh viên

            // Thêm thông báo vào danh sách
            notifiesDtb.add(notify);
        }

        return notifiesDtb; // Trả về danh sách thông báo
    }
}
