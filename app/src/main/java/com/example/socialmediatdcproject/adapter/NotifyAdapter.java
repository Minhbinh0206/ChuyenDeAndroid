package com.example.socialmediatdcproject.adapter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Handler; // Thêm import này
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmediatdcproject.API.AdminBusinessAPI;
import com.example.socialmediatdcproject.API.AdminDefaultAPI;
import com.example.socialmediatdcproject.API.AdminDepartmentAPI;
import com.example.socialmediatdcproject.API.NotifyAPI;
import com.example.socialmediatdcproject.API.StudentAPI;
import com.example.socialmediatdcproject.API.UserAPI;
import com.example.socialmediatdcproject.R;
import com.example.socialmediatdcproject.activity.NotifyDetailActivity;
import com.example.socialmediatdcproject.model.AdminBusiness;
import com.example.socialmediatdcproject.model.AdminDefault;
import com.example.socialmediatdcproject.model.AdminDepartment;
import com.example.socialmediatdcproject.model.Notify;
import com.example.socialmediatdcproject.model.Student;
import com.example.socialmediatdcproject.model.User;
import com.google.firebase.auth.FirebaseAuth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NotifyAdapter extends RecyclerView.Adapter<NotifyAdapter.NotifyViewHolder> {

    private List<Notify> notifyList;
    private Handler handler = new Handler(); // Thêm handler để lập lịch
    private Runnable bellRunnable; // Thêm runnable cho chuông
    private Context context;

    // Constructor
    public NotifyAdapter(List<Notify> memberList, Context context) {
        this.notifyList = memberList;
        this.context = context;
    }

    // Tạo ViewHolder
    @NonNull
    @Override
    public NotifyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notify_list, parent, false);
        return new NotifyViewHolder(view);
    }

    // Phương thức cập nhật danh sách Notify trong adapter
    public void updateList(List<Notify> newNotifyList) {
        if (newNotifyList != null) {
            this.notifyList.clear();  // Xóa tất cả phần tử cũ trong danh sách
            this.notifyList.addAll(newNotifyList);  // Thêm phần tử mới vào danh sách
            notifyDataSetChanged();  // Cập nhật dữ liệu cho RecyclerView
        }
    }

    @Override
    public void onBindViewHolder(@NonNull NotifyAdapter.NotifyViewHolder holder, int position) {
        Notify notify = notifyList.get(position);
        UserAPI userAPI = new UserAPI();

        if (notify != null) {
            // Set dữ liệu cho các view
            userAPI.getAllUsers(new UserAPI.UserCallback() {
                @Override
                public void onUserReceived(User user) {
                    // Không làm gì ở đây
                }

                @Override
                public void onUsersReceived(List<User> users) {
                    for (User u : users) {
                        if (u.getUserId() == notify.getUserSendId()) {
                            holder.userSendID.setText(u.getFullName());

                            Glide.with(context)
                                    .load(u.getAvatar())
                                    .circleCrop()
                                    .into(holder.avatarUser);

                            break;
                        }
                    }
                }
            });

            holder.notifyTitle.setText(notify.getNotifyTitle());

            holder.createAt.setText(getTimeAgo(notify.getCreateAt()));

            // Cắt content xuống tối đa 100 ký tự
            String shortContent = getShortenedContent(notify.getNotifyContent(), 60);
            holder.notifyContent.setText(shortContent);
            StudentAPI studentAPI = new StudentAPI();
            String userKey = FirebaseAuth.getInstance().getCurrentUser().getUid();
            AdminDepartmentAPI adminDepartmentAPI = new AdminDepartmentAPI();
            AdminBusinessAPI adminBusinessAPI = new AdminBusinessAPI();
            AdminDefaultAPI adminDefaultAPI = new AdminDefaultAPI();
            NotifyAPI notifyAPI = new NotifyAPI();

            studentAPI.getStudentByKey(userKey, new StudentAPI.StudentCallback() {
                @Override
                public void onStudentReceived(Student student) {
                    notifyAPI.checkIfUserHasRead(notify, student.getUserId(), new NotifyAPI.CheckReadStatusCallback() {
                        @Override
                        public void onResult(boolean hasRead) {
                            // Chỉ rung chuông khi isRead là false
                            if (!hasRead)
                            {
                                scheduleBellAnimation(holder.bellIcon);
                            } else {
                                holder.bellIcon.clearAnimation(); // Dừng mọi animation của chuông nếu đã đọc
                            }
                        }
                    });
                }

                @Override
                public void onStudentsReceived(List<Student> students) {

                }
            });

            adminBusinessAPI.getAdminBusinessByKey(userKey, new AdminBusinessAPI.AdminBusinessCallBack() {
                @Override
                public void onUserReceived(AdminBusiness adminBusiness) {
                    notifyAPI.checkIfUserHasRead(notify, adminBusiness.getUserId(), new NotifyAPI.CheckReadStatusCallback() {
                        @Override
                        public void onResult(boolean hasRead) {
                            // Chỉ rung chuông khi isRead là false
                            if (!hasRead)
                            {
                                scheduleBellAnimation(holder.bellIcon);
                            } else {
                                holder.bellIcon.clearAnimation(); // Dừng mọi animation của chuông nếu đã đọc
                            }
                        }
                    });
                }

                @Override
                public void onUsersReceived(List<AdminBusiness> adminBusiness) {

                }

                @Override
                public void onError(String s) {

                }
            });

            adminDepartmentAPI.getAdminDepartmentByKey(userKey, new AdminDepartmentAPI.AdminDepartmentCallBack() {
                @Override
                public void onUserReceived(AdminDepartment adminDepartment) {
                    notifyAPI.checkIfUserHasRead(notify, adminDepartment.getUserId(), new NotifyAPI.CheckReadStatusCallback() {
                        @Override
                        public void onResult(boolean hasRead) {
                            // Chỉ rung chuông khi isRead là false
                            if (!hasRead)
                            {
                                scheduleBellAnimation(holder.bellIcon);
                            } else {
                                holder.bellIcon.clearAnimation(); // Dừng mọi animation của chuông nếu đã đọc
                            }
                        }
                    });
                }

                @Override
                public void onUsersReceived(List<AdminDepartment> adminDepartment) {

                }

                @Override
                public void onError(String s) {

                }
            });

            adminDefaultAPI.getAdminDefaultByKey(userKey, new AdminDefaultAPI.AdminDefaultCallBack() {
                @Override
                public void onUserReceived(AdminDefault adminDefault) {
                    notifyAPI.checkIfUserHasRead(notify, adminDefault.getUserId(), new NotifyAPI.CheckReadStatusCallback() {
                        @Override
                        public void onResult(boolean hasRead) {
                            // Chỉ rung chuông khi isRead là false
                            if (!hasRead)
                            {
                                scheduleBellAnimation(holder.bellIcon);
                            } else {
                                holder.bellIcon.clearAnimation(); // Dừng mọi animation của chuông nếu đã đọc
                            }
                        }
                    });
                }

                @Override
                public void onUsersReceived(List<AdminDefault> adminDefault) {

                }
            });

            holder.buttonRead.setOnClickListener(v -> {
                studentAPI.getStudentByKey(userKey, new StudentAPI.StudentCallback() {
                    @Override
                    public void onStudentReceived(Student student) {
                        notifyAPI.addUserToRead(notify, student.getUserId());
                    }

                    @Override
                    public void onStudentsReceived(List<Student> students) {

                    }
                });

                adminDepartmentAPI.getAdminDepartmentByKey(userKey, new AdminDepartmentAPI.AdminDepartmentCallBack() {
                    @Override
                    public void onUserReceived(AdminDepartment adminDepartment) {
                        notifyAPI.addUserToRead(notify, adminDepartment.getUserId());
                    }

                    @Override
                    public void onUsersReceived(List<AdminDepartment> adminDepartment) {

                    }

                    @Override
                    public void onError(String s) {

                    }
                });

                adminBusinessAPI.getAdminBusinessByKey(userKey, new AdminBusinessAPI.AdminBusinessCallBack() {
                    @Override
                    public void onUserReceived(AdminBusiness adminBusiness) {
                        notifyAPI.addUserToRead(notify, adminBusiness.getUserId());
                    }

                    @Override
                    public void onUsersReceived(List<AdminBusiness> adminBusiness) {

                    }

                    @Override
                    public void onError(String s) {

                    }
                });

                adminDefaultAPI.getAdminDefaultByKey(userKey, new AdminDefaultAPI.AdminDefaultCallBack() {
                    @Override
                    public void onUserReceived(AdminDefault adminDefault) {
                        notifyAPI.addUserToRead(notify, adminDefault.getUserId());
                    }

                    @Override
                    public void onUsersReceived(List<AdminDefault> adminDefault) {

                    }
                });

                // Đánh dấu là đã đọc
                Intent intent = new Intent(v.getContext(), NotifyDetailActivity.class);
                intent.putExtra("notifyId", notify.getNotifyId());
                intent.putExtra("userSend", notify.getUserSendId());
                intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                v.getContext().startActivity(intent);
            });
        } else {
            Log.e("NotifyAdapter", "Notify at position " + position + " is null");
        }
    }


    @Override
    public int getItemCount() {
        return notifyList.size();
    }

    private String getShortenedContent(String content, int maxLength) {
        if (content != null && content.length() > maxLength) {
            return content.substring(0, maxLength) + "..."; // Thêm dấu "..." nếu chuỗi bị cắt
        } else {
            return content;
        }
    }


    // Lớp ViewHolder
    public static class NotifyViewHolder extends RecyclerView.ViewHolder {
        TextView userSendID;
        TextView notifyTitle;
        TextView notifyContent;
        ImageView bellIcon;
        Button buttonRead;
        ImageView avatarUser;
        TextView createAt;

        public NotifyViewHolder(@NonNull View itemView) {
            super(itemView);
            userSendID = itemView.findViewById(R.id.notify_user_send);
            notifyTitle = itemView.findViewById(R.id.notify_title);
            notifyContent = itemView.findViewById(R.id.notify_content);
            bellIcon = itemView.findViewById(R.id.bell_icon);
            buttonRead = itemView.findViewById(R.id.button_read_notify);
            avatarUser = itemView.findViewById(R.id.avatar_user_send);
            createAt = itemView.findViewById(R.id.notify_createAt);
        }
    }

    private void scheduleBellAnimation(View view) {
        // Lập lịch rung chuông mỗi 3 giây
        bellRunnable = new Runnable() {
            @Override
            public void run() {
                startBellAnimation(view);
                handler.postDelayed(this, 1000); // Lập lịch lại sau 1 giây
            }
        };
        handler.post(bellRunnable); // Bắt đầu lập lịch
    }

    private String getTimeAgo(String createdAt) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); // Điều chỉnh định dạng cho đúng với chuỗi thời gian của bạn
        try {
            Date createdDate = format.parse(createdAt);
            Date currentDate = new Date();

            long diffInMillis = currentDate.getTime() - createdDate.getTime();
            long minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis);
            long hours = TimeUnit.MILLISECONDS.toHours(diffInMillis);
            long days = TimeUnit.MILLISECONDS.toDays(diffInMillis);

            if (minutes == 0) {
                return "Vừa xong";
            } else if (minutes < 60) {
                return minutes + " phút trước";
            } else if (hours < 24) {
                return hours + " giờ trước";
            } else {
                return days + " ngày trước";
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    private void startBellAnimation(View view) {
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(view, "translationX", 0f, 15f);
        ObjectAnimator animatorXReverse = ObjectAnimator.ofFloat(view, "translationX", 15f, -15f);
        ObjectAnimator animatorXBack = ObjectAnimator.ofFloat(view, "translationX", -15f, 15f);
        ObjectAnimator animatorXReturn = ObjectAnimator.ofFloat(view, "translationX", 15f, 0f);

        animatorX.setDuration(100);
        animatorXReverse.setDuration(100);
        animatorXBack.setDuration(100);
        animatorXReturn.setDuration(100);

        // Tạo AnimatorSet để chạy các animation liên tiếp
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(animatorX, animatorXReverse, animatorXReturn);
        animatorSet.setInterpolator(new LinearInterpolator());
        animatorSet.start();
    }

    // Đừng quên dừng lại khi adapter không còn sử dụng
    public void stopAnimation() {
        handler.removeCallbacks(bellRunnable); // Dừng lịch
    }
}
