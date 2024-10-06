package com.example.socialmediatdcproject.database;

import com.example.socialmediatdcproject.model.Post;
import com.example.socialmediatdcproject.model.User;

import java.util.ArrayList;

public class PostDatabase {
    protected ArrayList<Post> postsDtb = new ArrayList<>();
    protected ArrayList<Post> postsBussiness = new ArrayList<>();

    public Post getPostById(int id){
        for (Post p : dataPost()) {
            if (p.getPostId() == id){
                return p;
            }
        }
        return null;
    }

    public ArrayList<Post> dataPost() {
        // Post 1
        Post p1 = new Post();
        p1.setPostId(0);
        p1.setUserId(User.ID_ADMIN_DOANTHANHNIEN);
        p1.setTitle("Chia sẻ kinh nghiệm học tập");
        p1.setContent("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");
        p1.setIsPublic(1);
        p1.setPostImage(null);
        p1.setStatus(1);
        postsDtb.add(p1);

        // Post 2
        Post p2 = new Post();
        p2.setPostId(1);
        p2.setUserId(User.ID_ADMIN_DEPARTMENT_CNTT);
        p2.setTitle("Tìm người học nhóm môn lập trình");
        p2.setContent("Cần tìm nhóm học tập môn lập trình Java. Ai quan tâm thì liên hệ.");
        p2.setIsPublic(1);
        p2.setStatus(1);
        p2.setPostImage("Nuhll");
        postsDtb.add(p2);

        // Post 3
        Post p3 = new Post();
        p3.setPostId(2);
        p3.setUserId(User.ID_ADMIN_DOANTHANHNIEN);
        p3.setTitle("Thông báo về hội thảo khoa học");
        p3.setContent("Hội thảo khoa học sẽ được tổ chức vào ngày 15/10. Mọi người đăng ký tham gia nhé.");
        p3.setIsPublic(1);
        p3.setStatus(1);
        p3.setPostImage("null");
        postsDtb.add(p3);

        // Post 4
        Post p4 = new Post();
        p4.setPostId(3);
        p4.setUserId(User.ID_ADMIN_DEPARTMENT_CNTT);
        p4.setTitle("Cần hỗ trợ giải bài tập toán cao cấp");
        p4.setContent("Mình gặp khó khăn với bài tập toán cao cấp. Bạn nào giỏi có thể giúp mình không?");
        p4.setIsPublic(0);
        p4.setStatus(1);
        p4.setPostImage(null);
        postsDtb.add(p4);

        // Post 5
        Post p5 = new Post();
        p5.setPostId(4);
        p5.setUserId(User.ID_ADMIN_DOANTHANHNIEN);
        p5.setTitle("Giới thiệu tài liệu lập trình C++");
        p5.setContent("Mình có tài liệu hay về C++. Ai cần thì mình chia sẻ.");
        p5.setIsPublic(1);
        p5.setStatus(1);
        p5.setPostImage(null);
        postsDtb.add(p5);

        // Post 6
        Post p6 = new Post();
        p6.setPostId(5);
        p6.setUserId(User.ID_ADMIN_DEPARTMENT_CNTT);
        p6.setTitle("Cập nhật lịch thi cuối kỳ");
        p6.setContent("Lịch thi cuối kỳ sẽ được cập nhật vào ngày 10/10. Sinh viên chú ý theo dõi.");
        p6.setIsPublic(1);
        p6.setStatus(1);
        p6.setPostImage(null);
        postsDtb.add(p6);

        // Post 7
        Post p7 = new Post();
        p7.setPostId(6);
        p7.setUserId(User.ID_ADMIN_DOANTHANHNIEN);
        p7.setTitle("Chương trình thiện nguyện mùa đông");
        p7.setContent("Chúng tôi đang tổ chức chương trình thiện nguyện cho mùa đông năm nay. Ai muốn tham gia xin đăng ký.");
        p7.setIsPublic(1);
        p7.setStatus(1);
        p7.setPostImage("null");
        postsDtb.add(p7);

        // Post 8
        Post p8 = new Post();
        p8.setPostId(7);
        p8.setUserId(User.ID_ADMIN_BUSSINESS_FPT);
        p8.setTitle("Hướng dẫn cài đặt Android Studio");
        p8.setContent("Bài hướng dẫn này sẽ giúp các bạn cài đặt Android Studio và thiết lập môi trường phát triển.");
        p8.setIsPublic(1);
        p8.setStatus(1);
        p8.setPostImage(null);
        postsDtb.add(p8);

        // Post 9
        Post p9 = new Post();
        p9.setPostId(8);
        p9.setUserId(User.ID_ADMIN_BUSSINESS_GRAB);
        p9.setTitle("Cuộc thi lập trình sáng tạo");
        p9.setContent("Cuộc thi lập trình sáng tạo sẽ được tổ chức vào tháng 11. Mọi người đừng bỏ lỡ nhé.");
        p9.setIsPublic(1);
        p9.setStatus(1);
        p9.setPostImage(null);
        postsDtb.add(p9);

        // Post 10
        Post p10 = new Post();
        p10.setPostId(9);
        p10.setUserId(User.ID_ADMIN_BUSSINESS_MBBANK);
        p10.setTitle("Câu lạc bộ công nghệ thông tin tuyển thành viên");
        p10.setContent("Câu lạc bộ CNTT đang tuyển thành viên mới. Mọi người quan tâm hãy đăng ký tham gia.");
        p10.setIsPublic(1);
        p10.setStatus(1);
        p10.setPostImage("null");
        postsDtb.add(p10);

        // Post 11
        Post p11 = new Post();
        p11.setPostId(10);
        p11.setUserId(User.ID_ADMIN_BUSSINESS_TITAN);
        p11.setTitle("Đăng ký tham gia học tiếng Anh miễn phí");
        p11.setContent("Chương trình học tiếng Anh miễn phí cho sinh viên có hoàn cảnh khó khăn. Hãy nhanh tay đăng ký.");
        p11.setIsPublic(1);
        p11.setStatus(1);
        p11.setPostImage(null);
        postsDtb.add(p11);

        // Post 12
        Post p12 = new Post();
        p12.setPostId(11);
        p12.setUserId(User.ID_ADMIN_BUSSINESS_APPLE);
        p12.setTitle("Hội thảo công nghệ AI và ML");
        p12.setContent("Hội thảo công nghệ về trí tuệ nhân tạo và máy học sẽ diễn ra vào cuối tháng này.");
        p12.setIsPublic(1);
        p12.setStatus(1);
        p12.setPostImage("null");
        postsDtb.add(p12);

        return postsDtb;
    }

    public ArrayList<Post> dataPostFilterBussiness(){
        for (Post p : dataPost()) {
            if (p.getUserId() == User.ID_ADMIN_BUSSINESS_FPT || p.getUserId() == User.ID_ADMIN_BUSSINESS_APPLE || p.getUserId() == User.ID_ADMIN_BUSSINESS_VINFAST || p.getUserId() == User.ID_ADMIN_BUSSINESS_TITAN || p.getUserId() == User.ID_ADMIN_BUSSINESS_MBBANK || p.getUserId() == User.ID_ADMIN_BUSSINESS_GRAB || p.getUserId() == User.ID_ADMIN_BUSSINESS_EVN) {
                postsBussiness.add(p);
            }
        }
        return postsBussiness;
    }
}
