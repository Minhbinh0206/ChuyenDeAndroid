package com.example.socialmediatdcproject.database;

import com.example.socialmediatdcproject.model.Major;

import java.util.ArrayList;

public class MajorDatabase {
    ArrayList<Major> majorsDtb = new ArrayList<>();

    public ArrayList<Major> dataMajors(){
        // Ngành
        Major m1 = new Major();
        m1.setMajorId(0);
        m1.setMajorName("Công nghệ kỹ thuật ôtô");
        majorsDtb.add(m1);

        Major m2 = new Major();
        m2.setMajorId(1);
        m2.setMajorName("Chế tạo thiết bị cơ khí");
        majorsDtb.add(m2);

        Major m3 = new Major();
        m3.setMajorId(2);
        m3.setMajorName("Công nghệ kỹ thuật cơ khí");
        majorsDtb.add(m3);

        Major m4 = new Major();
        m4.setMajorId(3);
        m4.setMajorName("Công nghệ kỹ thuật cơ điện tử");
        majorsDtb.add(m4);

        Major m5 = new Major();
        m5.setMajorId(4);
        m5.setMajorName("Công nghệ kỹ thuật điều khiển và tự động hóa");
        majorsDtb.add(m5);

        Major m6 = new Major();
        m6.setMajorId(5);
        m6.setMajorName("Tự động hóa công nghiệp");
        majorsDtb.add(m6);

        Major m7 = new Major();
        m7.setMajorId(6);
        m7.setMajorName("Công nghệ thông tin");
        majorsDtb.add(m7);

        Major m8 = new Major();
        m8.setMajorId(7);
        m8.setMajorName("Thiết kế đồ họa");
        majorsDtb.add(m8);

        Major m9 = new Major();
        m9.setMajorId(8);
        m9.setMajorName("Truyền thông và mạng máy tính");
        majorsDtb.add(m9);

        Major m10 = new Major();
        m10.setMajorId(9);
        m10.setMajorName("Công nghệ kỹ thuật điện - điện tử");
        majorsDtb.add(m10);

        Major m11 = new Major();
        m11.setMajorId(10);
        m11.setMajorName("Công nghệ kỹ thuật điện tử - truyền thông");
        majorsDtb.add(m11);

        Major m12 = new Major();
        m12.setMajorId(11);
        m12.setMajorName("Điện công nghiệp");
        majorsDtb.add(m12);

        Major m13 = new Major();
        m13.setMajorId(12);
        m13.setMajorName("Điện tử công nghiệp");
        majorsDtb.add(m13);

        Major m14 = new Major();
        m14.setMajorId(13);
        m14.setMajorName("Kỹ thuật lắp đặt điện và điều khiển trong công nghiệp");
        majorsDtb.add(m14);

        Major m15 = new Major();
        m15.setMajorId(14);
        m15.setMajorName("Kỹ thuật máy lạnh và điều hòa không khí");
        majorsDtb.add(m15);

        Major m16 = new Major();
        m16.setMajorId(15);
        m16.setMajorName("Tiếng Hàn Quốc");
        majorsDtb.add(m16);

        Major m17 = new Major();
        m17.setMajorId(16);
        m17.setMajorName("Quản trị dịch vụ du lịch và lữ hành");
        majorsDtb.add(m17);

        Major m18 = new Major();
        m18.setMajorId(17);
        m18.setMajorName("Quản trị khách sạn");
        majorsDtb.add(m18);

        Major m19 = new Major();
        m19.setMajorId(18);
        m19.setMajorName("Quản trị nhà hàng");
        majorsDtb.add(m19);

        Major m20 = new Major();
        m20.setMajorId(19);
        m20.setMajorName("Kinh doanh thương mại");
        majorsDtb.add(m20);

        Major m21 = new Major();
        m21.setMajorId(20);
        m21.setMajorName("Logistics");
        majorsDtb.add(m21);

        Major m22 = new Major();
        m22.setMajorId(21);
        m22.setMajorName("Marketing");
        majorsDtb.add(m22);

        Major m23 = new Major();
        m23.setMajorId(22);
        m23.setMajorName("Quản lý siêu thị");
        majorsDtb.add(m23);

        Major m24 = new Major();
        m24.setMajorId(23);
        m24.setMajorName("Quản trị kinh doanh");
        majorsDtb.add(m24);

        Major m25 = new Major();
        m25.setMajorId(24);
        m25.setMajorName("Tiếng Anh");
        majorsDtb.add(m25);

        Major m26 = new Major();
        m26.setMajorId(25);
        m26.setMajorName("Kế toán");
        majorsDtb.add(m26);

        Major m27 = new Major();
        m27.setMajorId(26);
        m27.setMajorName("Kế toán tin học");
        majorsDtb.add(m27);

        Major m28 = new Major();
        m28.setMajorId(27);
        m28.setMajorName("Tài chính - Ngân hàng");
        majorsDtb.add(m28);

        Major m29 = new Major();
        m29.setMajorId(28);
        m29.setMajorName("Tiếng Nhật");
        majorsDtb.add(m29);

        Major m30 = new Major();
        m30.setMajorId(29);
        m30.setMajorName("Tiếng Trung Quốc");
        majorsDtb.add(m30);

        return majorsDtb;
    }
}
