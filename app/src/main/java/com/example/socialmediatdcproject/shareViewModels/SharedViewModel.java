package com.example.socialmediatdcproject.shareViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.socialmediatdcproject.model.Lecturer;
import java.util.ArrayList;
import java.util.List;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<Boolean> isEditMode = new MutableLiveData<>(false);
    private final MutableLiveData<List<Lecturer>> lecturerListLiveData = new MutableLiveData<>(new ArrayList<>());

    public LiveData<Boolean> getIsEditMode() {
        return isEditMode;
    }

    public void setEditMode(boolean editMode) {
        isEditMode.setValue(editMode);
    }

    public LiveData<List<Lecturer>> getLecturerList() {
        return lecturerListLiveData;
    }

    public void removeLecturer(Lecturer lecturer) {
        List<Lecturer> currentList = lecturerListLiveData.getValue();
        if (currentList != null) {
            currentList.remove(lecturer);
            lecturerListLiveData.setValue(new ArrayList<>(currentList)); // Cập nhật LiveData sau khi xóa
        }
    }

    public void setLecturerList(List<Lecturer> lecturers) {
        lecturerListLiveData.setValue(lecturers);
    }
}
