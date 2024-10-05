package com.example.socialmediatdcproject.database;

import com.example.socialmediatdcproject.model.Bussiness;
import com.example.socialmediatdcproject.model.User;

import java.util.ArrayList;

public class BussinessDatabase {
    ArrayList<Bussiness> bussinessesDtb = new ArrayList<>();

    public ArrayList<Bussiness> dataBussiness(){
        // Doanh nghiệp FPT
        Bussiness b1 = new Bussiness();
        b1.setBussinessId(User.ID_ADMIN_BUSSINESS_FPT);
        b1.setBussinessName("Doanh nghiệp FPT");
        b1.setAddress("Tầng 2, tòa nhà FPT Cầu Giấy, số 17 Duy Tân, Phường Dịch Vọng Hậu, Quận Cầu Giấy, TP. Hà Nội");
        b1.setBussinessAdminId(User.ID_ADMIN_BUSSINESS_FPT);
        bussinessesDtb.add(b1);

        // Doanh nghiệp MB Bank
        Bussiness b2 = new Bussiness();
        b2.setBussinessId(User.ID_ADMIN_BUSSINESS_MBBANK);
        b2.setBussinessName("Doanh nghiệp MB Bank");
        b2.setAddress("Một phần tầng 1 vị trí L1-A-B tại dự án Tổ hợp văn phòng, dịch vụ thương mại, căn hộ và khu đỗ xe công cộng tại số 69B phố Thụy Khuê, Phường Thụy Khuê, Quận Tây Hồ, Thành phố Hà Nội");
        b2.setBussinessAdminId(User.ID_ADMIN_BUSSINESS_MBBANK);
        bussinessesDtb.add(b2);

        // Doanh nghiệp Vinfast
        Bussiness b3 = new Bussiness();
        b3.setBussinessId(User.ID_ADMIN_BUSSINESS_VINFAST);
        b3.setBussinessName("Doanh nghiệp Vinfast");
        b3.setAddress("Số 7, đường Bằng Lăng 1, Khu đô thị Vinhomes Riverside, Phường Việt Hưng, Quận Long Biên, Thành phố Hà Nội, Việt Nam");
        b3.setBussinessAdminId(User.ID_ADMIN_BUSSINESS_VINFAST);
        bussinessesDtb.add(b3);

        // Doanh nghiệp Apple
        Bussiness b4 = new Bussiness();
        b4.setBussinessId(User.ID_ADMIN_BUSSINESS_APPLE);
        b4.setBussinessName("Doanh nghiệp Apple");
        b4.setAddress("Công Ty TNHH Apple Việt Nam, Tòa nhà Deutsches Haus Ho Chi Minh City, Số 33, đường Lê Duẩn, phường Bến Nghé, quận 1, TP. Hồ Chí Minh, Việt Nam");
        b4.setBussinessAdminId(User.ID_ADMIN_BUSSINESS_APPLE);
        bussinessesDtb.add(b4);

        // Doanh nghiệp Grab
        Bussiness b5 = new Bussiness();
        b5.setBussinessId(User.ID_ADMIN_BUSSINESS_GRAB);
        b5.setBussinessName("Doanh nghiệp Grab");
        b5.setAddress("Tòa nhà Mapletree Business Centre, 1060 Nguyễn Văn Linh, Phường Tân Phong, Quận 7, Thành phố Hồ Chí Minh, Việt Nam.");
        b5.setBussinessAdminId(User.ID_ADMIN_BUSSINESS_GRAB);
        bussinessesDtb.add(b5);

        // Doanh nghiệp EVN
        Bussiness b6 = new Bussiness();
        b6.setBussinessId(User.ID_ADMIN_BUSSINESS_EVN);
        b6.setBussinessName("Doanh nghiệp EVN");
        b6.setAddress("Số 11 Cửa Bắc, phường Trúc Bạch, quận Ba Đình, Thành phố Hà Nội.");
        b6.setBussinessAdminId(User.ID_ADMIN_BUSSINESS_EVN);
        bussinessesDtb.add(b6);

        return  bussinessesDtb;
    }
}
