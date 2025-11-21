package xyz.rkgn.service.impl;

import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import xyz.rkgn.service.InformationService;
import xyz.rkgn.service.StudentClassService;

/**
 * @author SuohaChan
 * @data 2025/9/13
 */

@SpringBootTest
public class test {

    public static void main(String[] args) {
        StudentClassService studentClassService = Mockito.mock(StudentClassService.class);
        InformationService informationService = new InformationServiceImpl(studentClassService);

        Long a = 1L;
        informationService.getInformationByUserId(a);
    }
}
