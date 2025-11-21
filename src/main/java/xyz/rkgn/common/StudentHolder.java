package xyz.rkgn.common;


import xyz.rkgn.dto.StudentDto;

public class StudentHolder {
    private static final ThreadLocal<StudentDto> tl = new ThreadLocal<>();

    public static void setStudent(StudentDto studentDto) {
        tl.set(studentDto);
    }

    public static StudentDto getStudent() {
        return tl.get();
    }
    public static void removeStudent() {
        tl.remove();
    }
}
